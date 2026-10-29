import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:bank.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS accounts (" +
                "account_number TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "balance REAL DEFAULT 0.0" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    public static boolean createAccount(String accNum, String name, String password) {
        String sql = "INSERT INTO accounts(account_number, name, password, balance) VALUES(?,?,?,0.0)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accNum);
            pstmt.setString(2, name);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("❌ Account number already exists.");
            } else {
                System.err.println("Error creating account: " + e.getMessage());
            }
            return false;
        }
    }

    public static boolean authenticate(String accNum, String password) {
        String sql = "SELECT password FROM accounts WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password").equals(password);
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return false;
    }

    public static double getBalance(String accNum) {
        String sql = "SELECT balance FROM accounts WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching balance: " + e.getMessage());
        }
        return -1;
    }

    public static void updateBalance(String accNum, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accNum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Transaction failed: " + e.getMessage());
        }
    }

    public static boolean deleteAccount(String accNum) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accNum);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
            return false;
        }
    }
}
