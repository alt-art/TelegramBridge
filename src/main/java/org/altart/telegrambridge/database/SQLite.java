package org.altart.telegrambridge.database;

import org.altart.telegrambridge.TelegramBridge;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Arrays;
import java.util.UUID;

public class SQLite {
    private Connection connection;
    private Statement statement;

    public SQLite() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + TelegramBridge.plugin.getDataFolder().getAbsolutePath() + "/database.db");
            statement = connection.createStatement();
            init();
        } catch (SQLException e) {
            Arrays.stream(e.getStackTrace()).sequential().forEach(line -> TelegramBridge.log.severe(line.toString()));
        }
    }

    private void init() {
        try {
            statement.execute("CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY, lang TEXT)");
        } catch (SQLException e) {
            TelegramBridge.log.severe(e.getMessage());
            Arrays.stream(e.getStackTrace()).sequential().forEach(line -> TelegramBridge.log.severe(line.toString()));
        }
    }

    @Nullable
    public String getLang(UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lang FROM players WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            return preparedStatement.executeQuery().getString("lang");
        } catch (SQLException e) {
            TelegramBridge.log.severe(e.getMessage());
            Arrays.stream(e.getStackTrace()).sequential().forEach(line -> TelegramBridge.log.severe(line.toString()));
            return null;
        }
    }

    public void setLang(UUID uuid, String lang) {
        try {
            if (!lang.matches("[a-z]{2}")) {
                throw new IllegalArgumentException("Invalid language code");
            }
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO players (uuid, lang) VALUES (?, ?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, lang);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            TelegramBridge.log.severe(e.getMessage());
            Arrays.stream(e.getStackTrace()).sequential().forEach(line -> TelegramBridge.log.severe(line.toString()));
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            TelegramBridge.log.severe(e.getMessage());
            Arrays.stream(e.getStackTrace()).sequential().forEach(line -> TelegramBridge.log.severe(line.toString()));
        }
    }
}
