import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TetrisServerImpl extends UnicastRemoteObject implements TetrisServer {
    private static final String URL = "jdbc:postgresql://localhost:5432/tetris_users_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "33585900";

    protected TetrisServerImpl() throws RemoteException {
        super();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public boolean register(String login, String password) throws RemoteException {
        String sql = "INSERT INTO tetris_players (login, password_tetris, best_score) VALUES (?, ?, 0)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка при регистрации: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean login(String login, String password) throws RemoteException {
        String sql = "SELECT * FROM tetris_players WHERE login = ? AND password_tetris = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Ошибка при входе: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int getBestScore(String login) throws RemoteException {
        String sql = "SELECT best_score FROM tetris_players WHERE login = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("best_score");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении лучшего счета: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public void updateBestScore(String login, int score) throws RemoteException {
        String sql = "UPDATE tetris_players SET best_score = ? WHERE login = ? AND best_score < ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, score);
            stmt.setString(2, login);
            stmt.setInt(3, score);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении счета: " + e.getMessage());
        }
    }

    @Override
    public List<String> getLeaderboard() throws RemoteException {
        List<String> leaderboard = new ArrayList<>();
        String sql = "SELECT login, best_score FROM tetris_players ORDER BY best_score DESC";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                leaderboard.add(rs.getString("login") + " - " + rs.getInt("best_score"));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении рейтинга: " + e.getMessage());
        }
        return leaderboard;
    }
}
