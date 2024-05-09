package fr.pixelqilin.pixelqilinranked.database.handlers;

import fr.pixelqilin.pixelqilinranked.PixelQilinRanked;
import fr.pixelqilin.pixelqilinranked.core.RankedPlayer;
import fr.pixelqilin.pixelqilinranked.database.SQLManager;
import fr.pixelqilin.pixelqilinranked.utils.PluginLogger;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLSaver {
    private final String query =
            "INSERT INTO RANKED_PLAYERS (PLAYER_UUID,PLAYER_RANK,PLAYER_ELO,PLAYER_WINS,PLAYER_LOSSES)" +
                    "VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE " +
                    "PLAYER_RANK = VALUES(PLAYER_RANK), " +
                    "PLAYER_ELO = VALUES(PLAYER_ELO), " +
                    "PLAYER_WINS = VALUES(PLAYER_WINS), " +
                    "PLAYER_LOSSES = VALUES(PLAYER_LOSSES)";
    private final SQLManager sqlManager;

    /**
     * Constructor.
     * @param sqlManager SQLManager instance.
     */
    public SQLSaver(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    /**
     * Save player's data into database.
     * @param rankedPlayer RankedPlayer instance.
     */
    public void save(String playerName, RankedPlayer rankedPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(PixelQilinRanked.INSTANCE, () -> {
            try {
                final Connection connection = sqlManager.getConnection();

                final PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, rankedPlayer.getUuid());
                preparedStatement.setString(2, rankedPlayer.getRank());
                preparedStatement.setInt(3, rankedPlayer.getElo());
                preparedStatement.setInt(4, rankedPlayer.getWins());
                preparedStatement.setInt(5, rankedPlayer.getLosses());

                preparedStatement.executeUpdate();

                preparedStatement.close();
                connection.close();

                PluginLogger.info("Player " + playerName + " saved into database.");
            } catch (SQLException e) {
                PluginLogger.error("An error occurred while saving player's data into database.");
                PluginLogger.error(e.getMessage());
            }
        });
    }
}
