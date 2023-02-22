package at.technikum.application.repository;

import at.technikum.application.config.DbConnector;
import at.technikum.application.model.Card;
import at.technikum.application.model.TradingDeal;

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
                inTrade boolean default FALSE
            );
            CREATE TABLE IF NOT EXISTS trades(
                tradeID varchar PRIMARY KEY,
                cardID varchar NOT NULL,
                type varchar NOT NULL,
                minDamage float NOT NULL
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
            throw new IllegalStateException("DB query 'show deck' failed", e);
        }
    }
    public static final String QUERY_SET_DECK_ZERO = """
                UPDATE cards SET deck = FALSE WHERE usertoken = ? AND (cardID != ? OR cardID = ? OR cardID = ? or cardID = ?)
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
    public static final String QUERY_SET_DECK = """
                UPDATE cards SET deck = TRUE WHERE (cardID = ? OR cardID = ? or cardID = ? or cardID = ?) AND usertoken = ?
            """;
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
                return success == 4;
            }
        } catch(SQLException e){
            throw new IllegalStateException("Query ' configure deck' failed", e);
        }
    }

    public static final String QUERY_CREATE_DEAL = """
                INSERT INTO trades(tradeID, cardID, type, minDamage) VALUES (?, ?, ?, ?);
            """;
    public static final String QUERY_UPDATE_CARD_IN_DEAL = """
                UPDATE cards SET inTrade = TRUE WHERE inTrade = FALSE and deck = FALSE and cardID = ? and usertoken = ?
            """;
    @Override
    public boolean createTradingDeal(TradingDeal deal, String token) {
        try(Connection c = dataSource.getConnection()){
            try (PreparedStatement ps = c.prepareStatement(QUERY_UPDATE_CARD_IN_DEAL)){
                String dealID = deal.getDealID().toString();
                String cardID = deal.getCardID().toString();
                ps.setString(1, cardID);
                ps.setString(2, token);
                int success = ps.executeUpdate();
                if(success == 1){
                    try(PreparedStatement ps2 = c.prepareStatement(QUERY_CREATE_DEAL)) {
                        ps2.setString(1, dealID);
                        ps2.setString(2, cardID);
                        ps2.setString(3, deal.getType());
                        ps2.setFloat(4, deal.getMinDamage());
                        success = ps2.executeUpdate();
                        if (success == 1) {
                            return true;
                        }
                    }
                }
            }
        } catch(SQLException e){
            throw new IllegalStateException("Query 'createTradingDeal' failed",e);
        }
        return false;
    }
    public static final String QUERY_GET_TRADING_DEALS = """
            Select * FROM trades
            """;
    @Override
    public List<TradingDeal> getTradingDeals() {
        List<TradingDeal> deal = new ArrayList<>();
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_GET_TRADING_DEALS)){
                ps.execute();
                final ResultSet resultSet = ps.getResultSet();
                while(resultSet.next()){
                    deal.add(resultToDeal(resultSet));
                }
                if(deal.isEmpty()){
                    return null;
                }
            }
        } catch (SQLException e){
            throw new IllegalStateException("Query 'getTradingDeals' failed", e);
        }
        return deal;
    }
    private TradingDeal resultToDeal(ResultSet resultSet) throws SQLException {
        UUID did = UUID.fromString(resultSet.getString("tradeID"));
        UUID cid = UUID.fromString(resultSet.getString("cardID"));
        String type = resultSet.getString("type");
        float minDamage = resultSet.getFloat("minDamage");
        return new TradingDeal(did, cid, type, minDamage);
    }

    public static final String QUERY_SELECT_CARD_FROM_DEAL = """
            SELECT cardID from trades WHERE tradeID = ?
            """;
    public static final String QUERY_CHANGE_CARD_STATUS = """
            UPDATE cards SET inTrade = FALSE WHERE cardID = ? AND usertoken = ? AND inTrade = TRUE;
            """;
    public static final String QUERY_DELETE_DEAL = """
            DELETE from trades WHERE tradeID = ?;
            """;
    @Override
    public String deleteDeal(String token, UUID tradingID){
        try(Connection c = dataSource.getConnection()){
            String returnString;
            try(PreparedStatement ps1 = c.prepareStatement(QUERY_SELECT_CARD_FROM_DEAL)){
                ps1.setString(1, tradingID.toString());
                ps1.execute();
                final ResultSet resultSet = ps1.getResultSet();
                resultSet.next();
                String cardId = resultSet.getString(1);
                if(cardId == null){
                    returnString = "No trade with that ID";
                } else {
                    try (PreparedStatement ps2 = c.prepareStatement(QUERY_CHANGE_CARD_STATUS)) {
                        ps2.setString(1, cardId);
                        ps2.setString(2, token);
                        int success = ps2.executeUpdate();
                        if (success == 1) {
                            try (PreparedStatement ps3 = c.prepareStatement(QUERY_DELETE_DEAL)) {
                                ps3.setString(1, tradingID.toString());
                                success = ps3.executeUpdate();
                                if (success == 1) {
                                    returnString = "Trade deletion worked";
                                } else {
                                    returnString = "Trade deletion didn't work";
                                }
                            }
                        } else {
                            returnString = "Card doesn't belong to User, or isn't in trade, or card doesn't exist";
                        }
                    }
                }
                return returnString;
            }
        } catch (SQLException e){
            throw new IllegalStateException("Query 'deleteDeal' failed", e);
        }
    }

    public static final String QUERY_GET_CARD_FROM_ID = """
            SELECT * from cards WHERE cardID = ?
            """;

    @Override
    public Card getCardFromID(UUID cardID) {
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_GET_CARD_FROM_ID)){
                ps.setString(1, cardID.toString());
                ps.execute();
                final ResultSet resultSet = ps.getResultSet();
                resultSet.next();
                return convertResultSetToCard(resultSet);
            }
        } catch (SQLException e){
            throw new IllegalStateException("Card from User does not belong to him or exists");
        }
    }

    public static final String QUERY_GET_DEAL_FROM_ID = """
            SELECT * from trades WHERE tradeID = ?
            """;

    @Override
    public TradingDeal getDealFromID(UUID tradingID) {
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_GET_DEAL_FROM_ID)){
                ps.setString(1, tradingID.toString());
                ps.execute();
                final ResultSet resultSet = ps.getResultSet();
                resultSet.next();
                UUID cardID = UUID.fromString(resultSet.getString("cardID"));
                String type = resultSet.getString("type");
                float minDamage = resultSet.getFloat("minDamage");
                return new TradingDeal(tradingID, cardID, type, minDamage);
            }
        } catch (SQLException e){
            throw new IllegalStateException("No deal with tradeID");
        }
    }
    public static final String QUERY_GET_OWNER_FROM_ID = """
            SELECT usertoken FROM cards WHERE cardID = ?
            """;

    @Override
    public String getOwner(String cardID) {
        try(Connection c = dataSource.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(QUERY_GET_OWNER_FROM_ID)){
                ps.setString(1, cardID);
                ps.execute();
                final ResultSet resultSet = ps.getResultSet();
                resultSet.next();
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No card with that ID");
        }
    }
}
