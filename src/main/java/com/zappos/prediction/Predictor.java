package com.zappos.prediction;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.prediction.Prediction;
import com.google.api.services.prediction.PredictionScopes;
import com.google.api.services.prediction.model.Input;
import com.google.api.services.prediction.model.Output;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * Created by maxkeene on 6/12/14.
 */
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


    public String predict(List<Object> values) throws IOException, GeneralSecurityException {
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
                .setApplicationName("tri-fi").build();
        Input input = new Input();
        Input.InputInput inputInput = new Input.InputInput();
        inputInput.setCsvInstance(values);
        input.setInput(inputInput);
        Output output = prediction.trainedmodels().predict("tri-fi", "x-4-1", input).execute();
        return output.getOutputValue();
    }

}
