package org.example;

import com.google.api.services.sheets.v4.model.ValueRange;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class Bot extends TelegramLongPollingBot {

    private final SheetsService sheets;
    private final String botToken = System.getenv("GYMBOT_TOKEN");

    public Bot(SheetsService sheets) {
        this.sheets = sheets;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        User user = msg.getFrom();
        Long chatId = msg.getChatId();

        if (msg.hasText()) {
            if(msg.getText().equals("hola")){
                try {
                    ValueRange result = sheets.read(System.getenv("SPREADSHEET_ID"),"Ejercicios!A1:E1");
                    String respuesta = result.getValues().toString() != null ? result.getValues().toString() : "La hoja está vacia en ese rango";
                    getEjercicio(chatId, respuesta);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                deleteMessage(chatId,msg.getMessageId());
                sendMessage(chatId);
            }
            //copyMessage(chatId,msg.getText());
            forwardMessage(chatId,user.getId(),msg.getMessageId());
            System.out.println("Fist Name: " + user.getFirstName() + " Last Name: " + user.getLastName());
            System.out.println("Is Bot? " + user.getIsBot());
            System.out.println("Mensaje enviado: " + msg.getText());
        }

        if(msg.hasLocation()){
            Venue venue = msg.getVenue();
            System.out.println("Local: " + venue.getTitle());
            System.out.println("Direccion: " + venue.getAddress());
            System.out.println("Google Type: " + venue.getGooglePlaceType());
        }
    }

    @Override
    public String getBotUsername() {
        return "GymBot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    public void getEjercicio(Long chatId, String message) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .parseMode("MarkdownV2")
                .text("Contenido de la Hoja: "+ message)
                .build();

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
    public void copyMessage(Long chatId, String message) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .parseMode("MarkdownV2")
                .text("_"+ message+"_")
                .build();

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Long chatId) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .parseMode("MarkdownV2")
                .text("*SE SALUDA EN MAYUSCULA PIBE\\!*")
                .build();

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void forwardMessage(Long chatId, Long from, Integer msgId) {
        ForwardMessage msg = ForwardMessage.builder()
                .chatId(chatId)
                .fromChatId(from)
                .messageId(msgId).build();

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage msg = DeleteMessage.builder().chatId(chatId).messageId(messageId).build();

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
