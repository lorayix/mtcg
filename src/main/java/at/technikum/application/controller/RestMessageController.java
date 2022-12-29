package at.technikum.application.controller;

import at.technikum.application.exception.ClientException;
import at.technikum.application.model.Message;
import at.technikum.application.repository.MessageRepository;
import at.technikum.application.model.PlainMessage;
import at.technikum.httpserver.HttpStatus;
import at.technikum.httpserver.Response;

import java.util.List;

public class RestMessageController implements MessageController {

    private final MessageRepository messageRepository;

    public RestMessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Response getMessages() {
        List<Message> messages = this.messageRepository.getAllMessages();
        Response response = new Response();
        response.setBody(messages.toString());
        response.setHttpStatus(HttpStatus.OK);
        return response;
    }

    @Override
    public Response addMessages(PlainMessage message) {
        int id = this.messageRepository.addMessage(message);
        Response response = new Response();
        response.setBody("" + id);
        response.setHttpStatus(HttpStatus.CREATED);
        return response;
    }

    @Override
    public Response getMessage(int id) {
        Message message = this.messageRepository.getMessage(id);
        Response response = new Response();
        response.setBody(message.toString());
        response.setHttpStatus(HttpStatus.OK);
        return response;
    }

    @Override
    public Response editMessage(int id, PlainMessage message) {
        final Message targetMessage = new Message();
        targetMessage.setId(id);
        targetMessage.setMessage(message.getContent());
        this.messageRepository.editMessage(targetMessage);
        Response response = new Response();
        response.setHttpStatus(HttpStatus.NO_CONTENT);
        return response;
    }

    @Override
    public Response deleteMessage(int id) {
        try {
            this.messageRepository.deleteMessage(id);
        } catch (ClientException ce) {
            return wrapClientExceptionInResponse(ce);
        } catch (Exception e) {
            return wrapExceptionInResponse(e);
        }
        Response response = new Response();
        response.setHttpStatus(HttpStatus.NO_CONTENT);
        return response;
    }

    private Response wrapClientExceptionInResponse(ClientException ce) {
        Response response = new Response();
        response.setHttpStatus(HttpStatus.BAD_REQUEST);
        response.setBody(ce.getMessage());
        return response;
    }

    private Response wrapExceptionInResponse(Exception exception) {
        Response response = new Response();
        response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        response.setBody(exception.getMessage());
        return response;
    }
}
