package fr.pixelqilin.pixelqilinranked.database.handlers;

import fr.pixelqilin.pixelqilinranked.core.RankedPlayer;
import fr.pixelqilin.pixelqilinranked.database.SQLManager;
import fr.pixelqilin.pixelqilinranked.utils.PluginLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SQLLoader {

    private final SQLManager sqlManager;

    /**
     * Constructor.
     * @param sqlManager SQLManager instance.
     */
    public SQLLoader(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    /**
     * Load player's data from database.
     * @param uuid player's uuid.
     * @return RankedPlayer instance.
     */
    public RankedPlayer load(String uuid) {

        AtomicReference<String> rank = new AtomicReference<>("");
        AtomicInteger elo = new AtomicInteger(-1);
        AtomicInteger wins = new AtomicInteger(-1);
        AtomicInteger losses = new AtomicInteger(-1);

        try {
            final Connection connection = sqlManager.getConnection();

            final String query = "SELECT PLAYER_RANK,PLAYER_ELO,PLAYER_WINS,PLAYER_LOSSES FROM RANKED_PLAYERS WHERE PLAYER_UUID = ?";
            final PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid);

            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                rank.set(resultSet.getString("PLAYER_RANK"));
                elo.set(resultSet.getInt("PLAYER_ELO"));
                wins.set(resultSet.getInt("PLAYER_WINS"));
                losses.set(resultSet.getInt("PLAYER_LOSSES"));
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            PluginLogger.error("An error occurred while loading player's data from database.");
            PluginLogger.error(e.getMessage());
        }

        if (elo.get() == -1) return null;
        else return new RankedPlayer(uuid, rank.get(), elo.get(), wins.get(), losses.get());
    }
}
