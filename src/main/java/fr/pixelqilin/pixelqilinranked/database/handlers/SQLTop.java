package fr.pixelqilin.pixelqilinranked.database.handlers;

import fr.pixelqilin.pixelqilinranked.PixelQilinRanked;
import fr.pixelqilin.pixelqilinranked.core.RankedPlayer;
import fr.pixelqilin.pixelqilinranked.database.SQLManager;
import fr.pixelqilin.pixelqilinranked.utils.PluginLogger;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLTop {

    private final SQLManager sqlManager;

    /**
     * Constructor.
     *
     * @param sqlManager SQLManager instance.
     */
    public SQLTop(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    /**
     * Get top players.
     *
     * @return a list of the 12 top players.
     */
    public List<RankedPlayer> getTopPlayers() {
        final List<RankedPlayer> topPlayers = new ArrayList<>();
        final String query = "SELECT PLAYER_UUID,PLAYER_RANK,PLAYER_ELO,PLAYER_WINS,PLAYER_LOSSES FROM RANKED_PLAYERS ORDER BY PLAYER_ELO DESC LIMIT 12";

        try {
            final var connection = sqlManager.getConnection();
            final var preparedStatement = connection.prepareStatement(query);
            final var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final var uuid = resultSet.getString("PLAYER_UUID");
                final var rank = resultSet.getString("PLAYER_RANK");
                final var elo = resultSet.getInt("PLAYER_ELO");
                final var wins = resultSet.getInt("PLAYER_WINS");
                final var losses = resultSet.getInt("PLAYER_LOSSES");

                topPlayers.add(new RankedPlayer(uuid, rank, elo, wins, losses));
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (Exception e) {
            PluginLogger.error("An error occurred while getting top players.");
            PluginLogger.error(e.getMessage());
        }

        final Map<Player, RankedPlayer> players = PixelQilinRanked.INSTANCE.getRankedQueue().getMap();

        // replace the top players with the players in the queue to get the most up-to-date data
        for (Map.Entry<Player, RankedPlayer> entry : players.entrySet()) {
            for (int i = 0; i < topPlayers.size(); i++) {
                if (entry.getValue().getElo() > topPlayers.get(i).getElo()) {
                    topPlayers.add(i, entry.getValue());

                    if (topPlayers.size() > 12) {
                        topPlayers.remove(12);
                    }

                    break;
                }
            }
        }

        return topPlayers;
    }
}
