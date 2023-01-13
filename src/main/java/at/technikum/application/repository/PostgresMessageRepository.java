package at.technikum.application.repository;

import at.technikum.application.config.DataSource;
import at.technikum.application.config.DbConnector;
import at.technikum.application.model.Message;
import at.technikum.application.model.PlainMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresMessageRepository implements MessageRepository{

    public static final String SETUP_TABLE = """
                CREATE TABLE IF NOT EXISTS messages(
                    id int primary key
                    content varchar(500)
                );
            """;

    private static final String QUERY_ALL_MESSAGES = """
               SELECT id, content from messages
            """;

    private final DbConnector dataSource;

    public PostgresMessageRepository(DbConnector dataSource) {
        this.dataSource = dataSource;
        try (PreparedStatement ps = dataSource.getConnection()
                .prepareStatement(SETUP_TABLE)){
            ps.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to setup table", e);
        }
    }

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
                    messages.add(convertResultSetToMessage(resultSet));
                }
            }
        } catch (SQLException e){
            throw new IllegalStateException("DB query failed", e);
        }
        return messages;
    }

    private static Message convertResultSetToMessage(ResultSet resultSet) throws SQLException {
        return new Message(
                resultSet.getInt(1),
                resultSet.getString(2)
        );
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
