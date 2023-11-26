package ua.edu.ucu.apps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CacheDocument implements Document {
    private Document doc;

    public CacheDocument(Document document) {
        this.doc = document;
    }

    private Connection connect() {
        String url = "jdbc:sqlite:my_database.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return conn;
    }

    private void handleSQLException(SQLException e) {
        System.err.println("SQL Exception: " + e.getMessage());
    }

    public void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS documents ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "path TEXT NOT NULL,"
                + "document TEXT NOT NULL);";

        try (Connection conn = this.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(createTableSQL)) {
            preparedStatement.executeUpdate();
            System.out.println("Database created");
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    public boolean checkIfExists(String path) {
        String sql = "SELECT count(*) FROM documents WHERE path = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, path);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return false;
    }

    public void addData(String text, String path) {
        String sql = "INSERT INTO documents (path, document) VALUES (?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, path);
            pstmt.setString(2, text);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    @Override
    public String parse() {
        createTable();
        String path = "my_database.db";
        if (checkIfExists(path)) {
            System.out.println("This document exists in database.");
        } else {
            String text = this.doc.parse();
            System.out.println("This document doesn't exist in database.");
            this.addData(text,path);
        }

        return "Parsing result";
    }
}
