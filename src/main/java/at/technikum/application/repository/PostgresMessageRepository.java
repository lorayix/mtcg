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

    public static final String QUERY_ADD_MESSAGE = """
                INSERT INTO messages (content)
                VALUES ?
            """;

    public static final String QUERY_GET_ID = """
                SELECT id from messages
                WHERE content = ?
            """;

    public static final String QUERY_GET_MESSAGE = """
                SELECT id, content from messages
                WHERE id = ?
            """;

    public static final String QUERY_EDIT_MESSAGE = """
                UPDATE messages
                SET content = ?
                WHERE id = ?
            """;

    public static final String QUERY_DELETE_MESSAGE = """
                DELETE FROM messages
                WHERE id = ?
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
        int id;
        try(Connection c = dataSource.getConnection()){
            try (PreparedStatement ps = c.prepareStatement(QUERY_ADD_MESSAGE)){
                ps.setString(1, message.getContent());
                ps.execute();
                try(PreparedStatement ps2 = c.prepareStatement(QUERY_GET_ID)){
                    ps2.setString(1, message.getContent());
                    ps.execute();
                    id = ps.getResultSet().getInt(1);
                }
            }
        } catch (SQLException e){
            throw new IllegalStateException("DB query failed", e);
        }
        return id;
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
        Message message;
        try(Connection c = dataSource.getConnection()){
            try (PreparedStatement ps = c.prepareStatement(QUERY_GET_MESSAGE)){
                ps.setInt(1, id);
                ps.execute();
                message = convertResultSetToMessage(ps.getResultSet());
            }
        } catch (SQLException e){
            throw new IllegalStateException("DB query failed", e);
        }
        return message;
    }

    @Override
    public Message editMessage(Message message) {
        int id = message.getId();
        String base = message.getMessage();
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_EDIT_MESSAGE)){
                ps.setString(1, base);
                ps.setInt(2, id);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("DB query failed", e);
        }
        Message storedMessage = getMessage(id);
        return storedMessage;
    }

    @Override
    public boolean deleteMessage(int id) {
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_DELETE_MESSAGE)){
                ps.setInt(1, id);
                ps.execute();
            }
        } catch (SQLException e){
            throw new IllegalStateException("DB query failed", e);
        }
        return true;
    }
}
