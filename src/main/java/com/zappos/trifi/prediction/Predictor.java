package com.zappos.trifi.prediction;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.prediction.Prediction;
import com.google.api.services.prediction.PredictionScopes;
import com.google.api.services.prediction.model.Input;
import com.google.api.services.prediction.model.Output;
import com.google.appengine.repackaged.com.google.common.collect.Queues;
import com.google.common.io.Files;
import com.zappos.trifi.model.Location;
import com.zappos.trifi.model.Router;
import com.zappos.trifi.model.RouterSignature;
import com.zappos.trifi.util.TriFiConstants;
import com.zappos.trifi.util.TriFiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The {@code Predictor} is a queue-backed machine for connecting to the Google Prediction API to make {@code Location}
 * predictions given a {code RouterSignature}. The prediction API is not especially fast so multi-threading it should
 * be beneficial.
 */
@Component
public class Predictor {

    @Value("${google.clientId}")
    private String googleAccessKey;

    @Value("${google.secret}")
    private String googleSecretKey;

    @Value("${google.service.email}")
    private String serviceAccountEmail;

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Value("${prediction.x.model}")
    private String xModel;

    @Value("${prediction.y.model}")
    private String yModel;

    @Value("${prediction.floor.model}")
    private String floorModel;

    @Resource(name = "knownRouters")
    private List<String> knownRouters;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    private LinkedBlockingQueue<RouterSignature> signatureQueue = Queues.newLinkedBlockingQueue(1000);
    private final ScheduledExecutorService predictorPool = Executors.newScheduledThreadPool(5);

    private final Logger LOG = LoggerFactory.getLogger(Predictor.class);

    public void queuePrediction(RouterSignature bd) {
        try {
            if (!signatureQueue.offer(bd, TriFiConstants.PREDICTOR_QUEUE_OFFERING_TIMEOUT, TimeUnit.MILLISECONDS)) {
                LOG.warn("Predictor pool filled, dropping message!");
            }
        } catch (Throwable t) {
            LOG.error("Something went wrong queuing the RouterSignature", t);
        }
    }

    @PostConstruct
    private void start() {
        // Schedule Predictor Threads
        LOG.info("Starting Predictor Threads");
        for (int i = 0; i < 5; i++) { //
            predictorPool.scheduleWithFixedDelay(new PredictorRunnable(), 1000L, 50 + (long) (Math.random() * 450),
                    TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Runnable class that will pull a RouterSignature from the queue to process and save to Dynamo.
     */
    private class PredictorRunnable implements Runnable {

        @Override
        public void run() {
            RouterSignature rs;
            // Pull the next RouterSignature from the queue. If there are none, die for a short while.
            while (null != (rs = signatureQueue.poll())) {
                Map<String, Router> routers = rs.getRouters();
                List<Object> values = new ArrayList<>();

                for(String router : knownRouters) {
                    values.add(TriFiUtils.getSignalStrength(routers.get(router)));
                }

                try {
                    Double x = Double.valueOf(predict(values, xModel));
                    Double y = Double.valueOf(predict(values, yModel));
                    Double floor = Double.valueOf(predict(values, floorModel));

                    Location location = new Location();
                    location.setHostname(rs.getHostname());
                    location.setTimestamp(rs.getTimestamp());
                    location.setX(x);
                    location.setY(y);
                    location.setFloor(floor);

                    dynamoDBMapper.save(location);

                } catch (IOException | GeneralSecurityException e) {
                    // Log the error and continue without saving to the DB
                    LOG.error(e.getMessage());
                }

            }
        }
    }

    public String predict(List<Object> values, String model) throws IOException, GeneralSecurityException {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        // check for valid setup
        if (serviceAccountEmail.startsWith("Enter ")) {
            System.err.println(serviceAccountEmail);
            System.exit(1);
        }
        String p12Content = Files.readFirstLine(new File("key.p12"), Charset.defaultCharset());
        if (p12Content.startsWith("Please")) {
            System.err.println(p12Content);
            System.exit(1);
        }
        // service account credential (uncomment setServiceAccountUser for domain-wide delegation)
        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(serviceAccountEmail)
                .setServiceAccountScopes(Collections.singleton(PredictionScopes.PREDICTION))
                .setServiceAccountPrivateKeyFromP12File(new File("key.p12"))
                .build();


        Prediction prediction = new Prediction.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(TriFiConstants.GOOGLE_APP_NAME).build();
        Input input = new Input();
        Input.InputInput inputInput = new Input.InputInput();
        inputInput.setCsvInstance(values);
        input.setInput(inputInput);
        Output output = prediction.trainedmodels().predict(TriFiConstants.GOOGLE_APP_NAME, model, input).execute();
        return output.getOutputValue();
    }

}
