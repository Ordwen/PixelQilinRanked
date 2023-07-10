package fr.pixelqilin.pixelqilinranked.core.ranks;

import fr.pixelqilin.pixelqilinranked.PixelQilinRanked;
import fr.pixelqilin.pixelqilinranked.utils.PluginLogger;

import java.util.LinkedHashMap;
import java.util.Map;

public class RanksManager {

    private final PixelQilinRanked plugin;

    private final Map<Integer, Rank> ranks = new LinkedHashMap<>();

    /**
     * Constructor.
     * @param plugin instance of the main class
     */
    public RanksManager(PixelQilinRanked plugin) {
        this.plugin = plugin;
    }

    /**
     * Load ranks from the config.
     */
    public void loadRanks() {
        ranks.clear();

        plugin.getConfig().getConfigurationSection("ranks").getKeys(false).forEach(rankName -> {
            int elo = plugin.getConfig().getInt("ranks." + rankName + ".elo");
            ranks.put(elo, new Rank(rankName, elo));
            PluginLogger.info("Rank " + rankName + " loaded with " + elo + " elo.");
        });
    }

    public Rank getRank(int elo) {
        return ranks.get(elo);
    }
}
