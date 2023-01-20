package at.technikum.application.repository;

import at.technikum.application.config.DbConnector;
import at.technikum.application.model.Card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresCardRepository implements CardRepository {

    public static final String SETUP_TABLE = """
            DROP TABLE IF EXISTS cards;
            
            CREATE TABLE IF NOT EXISTS cards(
                cardID varchar PRIMARY KEY,
                name varchar NOT NULl,
                damage float NOT NULL,
                package_id varchar NOT NULL,
                user_id varchar default '',
                deck boolean default FALSE,
                inTrade boolean default FALSE,
                fk_trade UUID UNIQUE
            );
            """;
    private final DbConnector dataSource;

    public PostgresCardRepository(DbConnector dataSource){
        this.dataSource = dataSource;
        try (Connection c = dataSource.getConnection()){
            try (PreparedStatement ps = c.prepareStatement(SETUP_TABLE)) {
                ps.execute();
            }
        } catch (SQLException e){
            throw new IllegalStateException("Failed to setup table for Cards", e);
        }
    }
    public static final String QUERY_CREATE_CARD = """
                INSERT INTO cards (cardID, name, damage, package_id)
                VALUES (?, ?, ?, ?)
            """;

    @Override
    public void createCard(Card card, UUID packageID) {
        try(Connection c = dataSource.getConnection()) {
            try(PreparedStatement ps = c.prepareStatement(QUERY_CREATE_CARD)) {
                ps.setString(1, card.getCardId().toString());
                ps.setString(2, card.getName());
                ps.setFloat(3, card.getDamage());
                ps.setString(4, packageID.toString());
                System.out.println(ps);
                ps.execute();
            }
        } catch (SQLException e){
            throw new IllegalStateException("DB query 'addCard' failed", e);
        }

    }
    public static final String QUERY_GET_PACKAGE_ID_WITHOUT_USERS = """
                SELECT package_id FROM cards WHERE user_id = '' LIMIT 1;
            """;

    public String getUnusedPackage(){
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_GET_PACKAGE_ID_WITHOUT_USERS)){
                ps.execute();
                final ResultSet resultSet = ps.getResultSet();
                if(!resultSet.next()){
                    return null;
                } else {
                    return resultSet.getString(1);
                }
            }
        }catch(SQLException e){
            throw new IllegalStateException("DB query 'getUnusedPackageID' failed", e);
        }
    }
    public static final String QUERY_CHANGE_CARDOWNER = """
                UPDATE cards SET user_id = ? WHERE cardID = ?
            """;
    @Override
    public void changeCardOwner(String cardId, String uid) {
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_CHANGE_CARDOWNER)){
                ps.setString(1, uid);
                ps.setString(2, cardId);
                ps.executeUpdate();
            }
        }catch (SQLException e){
            throw new IllegalStateException("DB query 'changeCardOwner' failed", e);
        }
    }

    public static final String QUERY_BUY_PACKAGE = """
                SELECT cardID, name, damage FROM cards WHERE package_id = ?;
            """;

    @Override
    public List<Card> buyPackage(String token, String pid) {
        List<Card> cards = new ArrayList<>();
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_BUY_PACKAGE)){
                ps.setString(1, pid);
                ps.execute();
                final ResultSet resultSet = ps.getResultSet();
                while(resultSet.next()){
                    cards.add(convertResultSetToCard(resultSet));
                }
            }
        }catch (SQLException e){
            throw new IllegalStateException("DB query 'buy Package' failed", e);
        }
        return cards;
    }
    private static Card convertResultSetToCard (ResultSet resultSet) throws SQLException {
        return new Card(
                UUID.fromString(resultSet.getString("cardID")),
                resultSet.getString("name"),
                resultSet.getFloat("damage")
        );
    }
    public static final String QUERY_GET_ALL_CARDS = """
            SELECT cardID, name, damage FROM cards WHERE fk_user = ?
            """;
    @Override
    public List<Card> showCards(String token) {
        return null;
    }
    @Override
    public List<Card> showDeck(String token) {
        return null;
    }
    @Override
    public int configureDeck(String token, List<Card> cards) {
        return 0;
    }


}
