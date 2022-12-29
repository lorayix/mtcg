package at.technikum.application.repository;

import at.technikum.application.config.DataSource;
import at.technikum.application.model.Message;
import at.technikum.application.model.PlainMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresMessageRepository implements MessageRepository{
    private static DataSource dataSource;


    public PostgresMessageRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }



    public static final String QUERY_ALL_MESSAGES = """
            SELECT id, content from messages
            """;
    @Override
    public int addMessage(PlainMessage message) {
        return 0;
    }

    @Override
    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        try(Connection c = dataSource.getConnection()){
            try (PreparedStatement ps = c.prepareStatement(QUERY_ALL_MESSAGES)){
                ps.execute();
                final ResultSet resultSet = ps.getResultSet();
                while(resultSet.next()){
                    messages.add(
                            new Message(
                                resultSet.getInt(1),
                                resultSet.getString(2)
                            )
                    );
                }
            }
        } catch (SQLException e){
            throw new IllegalStateException("DB query failed", e);
        }
        return messages;
    }

    @Override
    public Message getMessage(int id) {
        return null;
    }

    @Override
    public Message editMessage(Message message) {
        return null;
    }

    @Override
    public boolean deleteMessage(int id) {
        return false;
    }
}
