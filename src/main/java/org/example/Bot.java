package org.example;

import com.google.api.services.sheets.v4.model.ValueRange;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    private final SheetsService sheets;
    private final String BOT_TOKEN = System.getenv("GYMBOT_TOKEN");
    private final String SPREADSHEET_ID = System.getenv("SPREADSHEET_ID");

    private InlineKeyboardButton volver = InlineKeyboardButton.builder()
            .text("VOLVER")
            .callbackData("VOLVER")
            .build();

    private InlineKeyboardButton pecho = InlineKeyboardButton.builder()
            .text("PECHO")
            .callbackData("PECHO")
            .build();

    private InlineKeyboardButton espalda = InlineKeyboardButton.builder()
            .text("ESPALDA")
            .callbackData("ESPALDA")
            .build();

    private InlineKeyboardButton biceps = InlineKeyboardButton.builder()
            .text("BICEPS")
            .callbackData("BICEPS")
            .build();

    private InlineKeyboardButton triceps = InlineKeyboardButton.builder()
            .text("TRICEPS")
            .callbackData("TRICEPS")
            .build();

    private InlineKeyboardButton hombro = InlineKeyboardButton.builder()
            .text("HOMBRO")
            .callbackData("HOMBRO")
            .build();

    private InlineKeyboardButton pierna = InlineKeyboardButton.builder()
            .text("PIERNA")
            .callbackData("PIERNA")
            .build();

    private InlineKeyboardButton press_banca = InlineKeyboardButton.builder()
            .text("PRESS_BANCA")
            .callbackData("PRESS_BANCA")
            .build();

    private InlineKeyboardButton press_banca_inclinado = InlineKeyboardButton.builder()
            .text("PRESS_BANCA_INCLINADO")
            .callbackData("PRESS_BANCA_INCLINADO")
            .build();


    private InlineKeyboardMarkup menu = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(pecho, triceps))
            .keyboardRow(List.of(espalda, biceps))
            .keyboardRow(List.of(pierna, hombro))
            .build();

    private InlineKeyboardMarkup ejercicios_pecho = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(press_banca, press_banca_inclinado))
            .keyboardRow(List.of(volver))
            .build();

    public Bot(SheetsService sheets) {
        this.sheets = sheets;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            var cbMsg = callbackQuery.getMessage();
            var msgId = cbMsg.getMessageId();
            var chatId = cbMsg.getChatId();
            var data = callbackQuery.getData();
            var queryId = callbackQuery.getId();

            try {
                buttonTap(chatId, queryId,data ,msgId);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        Message msg = update.getMessage();
        User user = msg.getFrom();
        Long chatId = msg.getChatId();

        if (msg.hasText()) {

            if(msg.isCommand()) {

                switch(msg.getText()){
                    case "/start":
                        sendMessage(chatId,"Bienvenido!");
                        return;
                    case "/menu":
                        sendMenu(chatId,"¿Que rutina estas haciendo? 🏋️‍♂️",menu);
                        return;
                    case "/help":
                        sendMessage(chatId, "Para registrar un ejercicio escribi: /log <ejercicio> <seriesXReps> <peso>");
                    default:
                        sendMessage(chatId,"❌ Comando no reconocido");
                }
            }

            if(msg.getText().equals("hola")){
                try {
                    ValueRange result = sheets.read(SPREADSHEET_ID,"Ejercicios!A1:E1");
                    String respuesta = result.getValues().toString() != null ? result.getValues().toString() : "La hoja está vacia en ese rango";
                    sendMessage(chatId,"Contenido de la Hoja: " + respuesta);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if(msg.getText().startsWith("/log")){
                try {
                    logHandler(chatId, msg.getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            //copyMessage(chatId,msg.getText());
            //forwardMessage(chatId,user.getId(),msg.getMessageId());
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
        return BOT_TOKEN;
    }

    @Override
    public void onRegister() {
        super.onRegister();
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

    public void sendMessage(Long chatId, String text) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
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

    private void logHandler(Long chatId, String message) throws IOException {

        try {

            String[] partes = message.split(" ", 2);
            if (partes.length < 2) {
                sendMessage(chatId, "⚠️ Uso correcto: /log <ejercicio> <detalle>");
                return;
            }

            String[] contenido = partes[1].split(" ");
            String ejercicio = contenido[0];
            String seriesXReps = contenido[1];
            String peso = contenido[2];

            LocalDateTime date = LocalDateTime.now();
            String dia = date.getDayOfMonth() +"/" + date.getMonthValue() +"/" + date.getYear();

            List<List<Object>> values = java.util.List.of(
                    java.util.List.of(dia.toString(), ejercicio, seriesXReps, peso)
            );

            sheets.append(
                    SPREADSHEET_ID,
                    "Ejercicios!A1",
                    "USER_ENTERED",
                    values
            );

            sendMessage(chatId, "✅ Registrado: " + ejercicio + " " + seriesXReps + " " + peso);
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(chatId, "❌ Error al registrar: " + e.getMessage());
        }
    }

    public void sendMenu(Long chatId, String text, InlineKeyboardMarkup kb) {
        SendMessage sm = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(kb).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void buttonTap(Long chatId, String queryId, String data, int msgIg) throws TelegramApiException {
        EditMessageText newText = EditMessageText.builder()
                .chatId(chatId)
                .messageId(msgIg)
                .text("")
                .build();

        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .chatId(chatId)
                .messageId(msgIg)
                .build();

        if(data.equals("VOLVER")) {
            newText.setText("¿Que rutina estas haciendo? 🏋️‍♂️");
            newKb.setReplyMarkup(menu);
        }

        if(data.equals("PECHO")){
            newText.setText("EJERCICIOS DE PECHO");
            newKb.setReplyMarkup(ejercicios_pecho);
        }

        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId).build();

        execute(close);
        execute(newText);
        execute(newKb);
    }
}
