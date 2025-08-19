package org.example;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
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

    public ValueRange read(String spreadsheetsId, String range) throws IOException {
        ValueRange result = null;
        try{
            result = sheets.spreadsheets().values().get(spreadsheetsId,range).execute();
            int numRows = result.getValues() != null ? result.getValues().size() : 0;
            System.out.println("rows retrieved: " + numRows);
            //List<List<Object>> values = result.getValues();
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                System.out.printf("Spreadsheet not found with id '%s'.\n", spreadsheetsId);
            } else {
                throw e;
            }
        }
        return result;
    }
}
