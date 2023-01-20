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
    private final DbConnector dataSource;
    private static Card convertResultSetToCard (ResultSet resultSet) throws SQLException {
        return new Card(
                UUID.fromString(resultSet.getString("cardID")),
                resultSet.getString("name"),
                resultSet.getFloat("damage")
        );
    }
    public static final String SETUP_TABLE = """
            CREATE TABLE IF NOT EXISTS cards(
                cardID varchar PRIMARY KEY,
                name varchar NOT NULl,
                damage float NOT NULL,
                package_id varchar NOT NULL,
                usertoken varchar default '',
                deck boolean default FALSE,
                inTrade boolean default FALSE,
                fk_trade UUID UNIQUE
            );
            """;
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
                SELECT package_id FROM cards WHERE usertoken = '' LIMIT 1;
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
                UPDATE cards SET usertoken = ? WHERE cardID = ?
            """;
    @Override
    public void changeCardOwner(String cardId, String token) {
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_CHANGE_CARDOWNER)){
                ps.setString(1, token);
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
    public static final String QUERY_GET_ALL_CARDS = """
            SELECT cardID, name, damage FROM cards WHERE usertoken = ?
            """;
    @Override
    public List<Card> showCards(String token) {
        List<Card> cards = new ArrayList<>();
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_GET_ALL_CARDS)){
                ps.setString(1, token);
                ps.execute();
                final ResultSet resultSet = ps.getResultSet();
                while(resultSet.next()) {
                    cards.add(convertResultSetToCard(resultSet));
                }
            }
        } catch (SQLException e){
            throw new IllegalStateException(("Db query 'getAllCards' failed"));
        }
        return cards;
    }

    public static final String QUERY_SHOW_DECK = """
                SELECT cardID, name, damage FROM cards WHERE usertoken = ? AND deck = TRUE;
            """;
    @Override
    public List<Card> showDeck(String token) {
        List<Card> cards = new ArrayList<>();
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_SHOW_DECK)){
                ps.setString(1, token);
                ps.execute();
                ResultSet resultSet = ps.getResultSet();
                while(resultSet.next()){
                    cards.add(convertResultSetToCard(resultSet));
                }
                if(cards.isEmpty()){
                    return null;
                }
                return cards;
            }
        } catch (SQLException e){
            throw new IllegalStateException("DB query 'ps' failed", e);
        }
    }
    public static final String QUERY_SET_DECK_ZERO = """
                UPDATE cards SET deck = FALSE WHERE usertoken = ? AND (cardID != ? OR cardID = ? OR cardID = ? or cardID = ?)
            """;

    public static final String QUERY_SET_DECK = """
                UPDATE cards SET deck = TRUE WHERE (cardID = ? OR cardID = ? or cardID = ? or cardID = ?) AND usertoken = ?
            """;
    @Override
    public void resetDeck(String token, List<String> cid){
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_SET_DECK_ZERO)){
                ps.setString(1, token);
                ps.setString(2, cid.get(0));
                ps.setString(3, cid.get(1));
                ps.setString(4, cid.get(2));
                ps.setString(5, cid.get(3));
                ps.execute();
            }
        } catch (SQLException e){
            throw new IllegalStateException("Query 'reset deck' failed", e);
        }
    }
    @Override
    public boolean configureDeck(List<String> cards, String uid) {
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_SET_DECK)){
                ps.setString(1, cards.get(0));
                ps.setString(2, cards.get(1));
                ps.setString(3, cards.get(2));
                ps.setString(4, cards.get(3));
                ps.setString(5, uid);
                int success = ps.executeUpdate();
                if(success == 4){
                    return true;
                } else {
                    return false;
                }
            }
        } catch(SQLException e){
            throw new IllegalStateException("Query ' configure deck' failed", e);
        }
    }
}
