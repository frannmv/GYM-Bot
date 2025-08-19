package org.example;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;

import java.io.InputStream;
import java.util.List;

public class SheetsService {
    private final Sheets sheets;
    private static final String APPLICATION_NAME = "GymBot Sheets";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public SheetsService() throws Exception {
        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        InputStream in = SheetsService.class.getResourceAsStream("/credentials.json");
        if (in == null) throw new IllegalStateException("No se encontró /credentials.json");
        var credential = GoogleCredential.fromStream(in, httpTransport, JSON_FACTORY)
                .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets"));
        this.sheets = new Sheets.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
