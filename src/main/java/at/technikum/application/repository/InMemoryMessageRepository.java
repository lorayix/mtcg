package at.technikum.application.repository;

import at.technikum.application.model.Message;
import at.technikum.application.model.PlainMessage;

import java.util.ArrayList;
import java.util.List;

public class InMemoryMessageRepository implements MessageRepository {

    private final List<Message> messages = new ArrayList<>();

    @Override
    public int addMessage(PlainMessage message) {
        return messages.size();
    }

    @Override
    public List<Message> getAllMessages() {
        return messages;
    }

    @Override
    public Message getMessage(int id) {
        return messages.get(id);
    }

    @Override
    public Message editMessage(Message message) {
        final Message storedMessage = messages.get(message.getId());
        storedMessage.setMessage(message.getMessage());
        return storedMessage;
    }

    @Override
    public boolean deleteMessage(int id) {
        messages.remove(id);
        return true;
    }
}
