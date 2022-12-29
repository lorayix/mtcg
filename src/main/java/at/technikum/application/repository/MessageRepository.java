package at.technikum.application.repository;

import at.technikum.application.model.Message;
import at.technikum.application.model.PlainMessage;

import java.util.List;

public interface MessageRepository {

    int addMessage(PlainMessage message);
    List<Message> getAllMessages();
    Message getMessage(int id);
    Message editMessage(Message message);
    boolean deleteMessage(int id);

}
