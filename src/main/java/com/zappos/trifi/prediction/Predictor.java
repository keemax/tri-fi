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
import com.google.api.services.prediction.model.Insert2;
import com.google.api.services.prediction.model.Output;

import com.google.api.services.prediction.model.Update;
import com.google.appengine.repackaged.com.google.common.collect.Queues;
import com.google.common.io.Files;
import com.zappos.trifi.model.Location;
import com.zappos.trifi.model.Router;
import com.zappos.trifi.model.RouterSignature;
import com.zappos.trifi.model.TrainingSignature;
import com.zappos.trifi.util.TriFiConstants;
import com.zappos.trifi.util.TriFiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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

    @Value("${prediction.model}")
    private String predictionModel;

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
                if(routers.size() == 0) {
                    continue;
                }
                List<Object> values = new ArrayList<>();

                for(String router : knownRouters) {
                    values.add(TriFiUtils.getSignalStrength(routers.get(router)));
                }

                try {
                    Double x = Double.valueOf(predict(values, "x-" + predictionModel));
                    Double y = Double.valueOf(predict(values, "y-" + predictionModel));
                    Double floor = Double.valueOf(predict(values, "floor-" + predictionModel));

                    Location location = new Location();
                    location.setHostname(rs.getHostname());
                    location.setTimestamp(rs.getTimestamp());
                    location.setX(x);
                    location.setY(y);
                    location.setFloor(floor);
                    location.setOriginRouterSignature(rs.getId());
                    location.setOriginModel(predictionModel);

                    dynamoDBMapper.save(location);

                } catch (IOException | GeneralSecurityException e) {
                    // Log the error and continue without saving to the DB
                    LOG.error(e.getMessage());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public String predict(List<Object> values, String model) throws IOException, GeneralSecurityException,
            URISyntaxException {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        // check for valid setup
        if (serviceAccountEmail.startsWith("Enter ")) {
            System.err.println(serviceAccountEmail);
            System.exit(1);
        }
        String p12Content = Files.readFirstLine(new File(this.getClass().getClassLoader().getResource("key.p12").toURI()),
                Charset.defaultCharset());
        if (p12Content.startsWith("Please")) {
            System.err.println(p12Content);
            System.exit(1);
        }
        // service account credential (uncomment setServiceAccountUser for domain-wide delegation)
        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(serviceAccountEmail)
                .setServiceAccountScopes(Collections.singleton(PredictionScopes.PREDICTION))
                .setServiceAccountPrivateKeyFromP12File(new File(this.getClass().getClassLoader().getResource("key.p12").toURI()))
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

    public String updateModel(TrainingSignature trainingSignature) throws IOException, GeneralSecurityException, URISyntaxException {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        // check for valid setup
        if (serviceAccountEmail.startsWith("Enter ")) {
            System.err.println(serviceAccountEmail);
            System.exit(1);
        }


        String p12Content = Files.readFirstLine(new File(this.getClass().getClassLoader().getResource("key.p12").toURI()),
                Charset.defaultCharset());
        if (p12Content.startsWith("Please")) {
            System.err.println(p12Content);
            System.exit(1);
        }
        // service account credential (uncomment setServiceAccountUser for domain-wide delegation)
        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(serviceAccountEmail)
                .setServiceAccountScopes(Collections.singleton(PredictionScopes.PREDICTION))
                .setServiceAccountPrivateKeyFromP12File(new File(this.getClass().getClassLoader().getResource("key.p12").toURI()))
                .build();


        Prediction prediction = new Prediction.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(TriFiConstants.GOOGLE_APP_NAME).build();

        Update xUpdate = new Update();
        xUpdate.setCsvInstance(getCSVInstance(trainingSignature, "X"));
        xUpdate.setOutput(String.valueOf(Math.round(trainingSignature.getX())));
        prediction.trainedmodels().update("tri-fi", "x-" + trainingSignature.getVersion(),
                xUpdate).execute();

        Update yUpdate = new Update();
        yUpdate.setCsvInstance(getCSVInstance(trainingSignature, "Y"));
        yUpdate.setOutput(String.valueOf(Math.round(trainingSignature.getY())));
        prediction.trainedmodels().update("tri-fi", "y-" + trainingSignature.getVersion(),
                yUpdate).execute();

        Update fUpdate = new Update();
        fUpdate.setCsvInstance(getCSVInstance(trainingSignature, "Floor"));
        fUpdate.setOutput(String.valueOf(Math.round(trainingSignature.getFloor())));
        prediction.trainedmodels().update("tri-fi", "floor-" + trainingSignature.getVersion(),
                fUpdate).execute();


//        Prediction.Trainedmodels.Update xoutput = prediction.trainedmodels().update("tri-fi",
//                "x-" + trainingSignature.getVersion(),
//                new Update().setCsvInstance(getCSVInstance(trainingSignature, "X")));
//        Prediction.Trainedmodels.Update youtput = prediction.trainedmodels().update("tri-fi",
//                "y-" + trainingSignature.getVersion(),
//                new Update().setCsvInstance());
//        Prediction.Trainedmodels.Update flooroutput = prediction.trainedmodels().update("tri-fi",
//                "floor-" + trainingSignature.getVersion(),
//                new Update().setCsvInstance(getCSVInstance(trainingSignature, "Floor")));
//
//        Insert2 xi = xoutput.execute();
//        Insert2 yi = youtput.execute();
//        Insert2 fi = flooroutput.execute();


        return "true";
    }

    private List<Object> getCSVInstance(TrainingSignature trainingSignature, String dimension) {
        List<Object> csvInstance = new ArrayList<>();
//        switch (dimension) {
//            case "X":
//                csvInstance.add(Math.round(trainingSignature.getX()));
//                break;
//            case "Y":
//                csvInstance.add(Math.round(trainingSignature.getY()));
//                break;
//            case "Floor":
//                csvInstance.add(Math.round(trainingSignature.getFloor()));
//                break;
//        }

        Map<String, Router> routers = trainingSignature.getRouterSignature().getRouters();
        for(String router : knownRouters) {
            csvInstance.add(TriFiUtils.getSignalStrength(routers.get(router)));
        }
        return csvInstance;
    }

}
