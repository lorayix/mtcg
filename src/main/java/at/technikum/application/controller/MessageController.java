package at.technikum.application.controller;

import at.technikum.application.model.PlainMessage;
import at.technikum.http.Response;

public interface MessageController {
    Response getMessages();
    Response addMessages(PlainMessage message);
    Response getMessage(int id);
    Response editMessage(int id, PlainMessage message);
    Response deleteMessage(int id);
}
