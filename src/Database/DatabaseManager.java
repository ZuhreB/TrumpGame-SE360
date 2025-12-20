package src.Database;

import src.Model.Card;
import src.Model.GameState;
import src.Model.PLAY_FLOW;

import java.sql.*;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private final String DB_URL = "jdbc:sqlite:trumpgame.db";

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            System.out.println("Veritabanı bağlantısı başarılı: " + DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        String usersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nickname TEXT NOT NULL UNIQUE, " +
                "wins INTEGER DEFAULT 0, " +
                "losses INTEGER DEFAULT 0, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";

        String gamesTable = "CREATE TABLE IF NOT EXISTS Games (" +
                "game_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "host_user_id INTEGER NOT NULL, " +
                "guest_user_id INTEGER, " +
                "trump_suit TEXT, " +
                "host_score INTEGER DEFAULT 0, " +
                "guest_score INTEGER DEFAULT 0, " +
                "winner_user_id INTEGER, " +
                "game_status TEXT DEFAULT 'ONGOING', " +
                "game_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (host_user_id) REFERENCES Users(user_id), " +
                "FOREIGN KEY (guest_user_id) REFERENCES Users(user_id), " +
                "FOREIGN KEY (winner_user_id) REFERENCES Users(user_id))";

        String movesTable = "CREATE TABLE IF NOT EXISTS Moves (" +
                "move_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "game_id INTEGER NOT NULL, " +
                "user_id INTEGER NOT NULL, " +
                "card_type TEXT NOT NULL, " +
                "card_number TEXT NOT NULL, " +
                "card_power INTEGER, " +
                "turn_step INTEGER NOT NULL, " +
                "play_flow_type TEXT, " +
                "move_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (game_id) REFERENCES Games(game_id), " +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id))";

        stmt.execute(usersTable);
        stmt.execute(gamesTable);
        stmt.execute(movesTable);
    }

    public int getOrCreateUser(String nickname) {
        try {
            if(nickname==null){
                nickname="Player"+ System.currentTimeMillis();
            }
            PreparedStatement ps = connection.prepareStatement("SELECT user_id FROM Users WHERE nickname = ?");
            ps.setString(1, nickname);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

            ps = connection.prepareStatement("INSERT INTO Users (nickname) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nickname);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public int createGame(int hostId, int guestId) {
        String trump=GameState.getInstance().getSecilen_trump();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Games " +
                    "(host_user_id, guest_user_id, trump_suit) " +
                    "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, hostId);
            ps.setInt(2, guestId);
            ps.setString(3, trump);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public void saveMove(int userId, Card card, PLAY_FLOW flow) {
        int turnStep = GameState.getInstance().getTurnStep();
        int gameId = GameState.getInstance().getDbGameId();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Moves " +
                    "(game_id, user_id, card_type, card_number, card_power, turn_step, play_flow_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, gameId);
            ps.setInt(2, userId);
            ps.setString(3, card.getType());
            ps.setString(4, card.getNumber());
            ps.setInt(5, card.getNumberPower());
            ps.setInt(6, turnStep);
            ps.setString(7, flow.toString());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        GameState.getInstance().setTurnStep(GameState.getInstance().getTurnStep()+1);
    }

    public void updateGameScore(int hostScore, int guestScore) {
        int gameId = GameState.getInstance().getDbGameId();
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE Games SET host_score = ?, guest_score = ? " +
                    "WHERE game_id = ?");
            ps.setInt(1, hostScore);
            ps.setInt(2, guestScore);
            ps.setInt(3, gameId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void finishGame(int winnerUserId) {
        int gameId= GameState.getInstance().getDbGameId();
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE Games " +
                    "SET winner_user_id = ?, game_status = 'FINISHED' WHERE game_id = ?");
            ps.setInt(1, winnerUserId);
            ps.setInt(2, gameId);
            ps.executeUpdate();

            PreparedStatement psUser = connection.prepareStatement("UPDATE Users " +
                    "SET wins = wins + 1 WHERE user_id = ?");
            psUser.setInt(1, winnerUserId);
            psUser.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int getWinnerId() {
        int winnerId = -1;
        int gameId=GameState.getInstance().getDbGameId();
        String query = "SELECT " +
                "CASE " +
                "    WHEN host_score > guest_score THEN host_user_id " +
                "    WHEN guest_score > host_score THEN guest_user_id " +
                "    ELSE NULL " +
                "END AS kazanan_id " +
                "FROM Games WHERE game_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, gameId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    winnerId = rs.getInt("kazanan_id");

                    if (rs.wasNull()) {
                        return -1;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return winnerId;
    }
}