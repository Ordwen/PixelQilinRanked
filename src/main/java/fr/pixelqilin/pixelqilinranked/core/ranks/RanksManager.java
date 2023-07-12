package fr.pixelqilin.pixelqilinranked.core.ranks;

import fr.pixelqilin.pixelqilinranked.PixelQilinRanked;
import fr.pixelqilin.pixelqilinranked.utils.ColorConvert;
import fr.pixelqilin.pixelqilinranked.utils.PluginLogger;
import org.bukkit.Location;

import java.util.LinkedHashMap;
import java.util.Map;

public class RanksManager {

    private final PixelQilinRanked plugin;

    private final Map<Integer, Rank> ranks = new LinkedHashMap<>();

    private int eloOnWin = -1;
    private int eloOnLose = -1;
    private int maximumEloDifference = -1;
    private int timeToAccept = -1;
    private int timeBeforeBattle = -1;

    private Location fightLocation = null;

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
            final String displayName = ColorConvert.convertColorCode(plugin.getConfig().getString("ranks." + rankName + ".name"));
            final int elo = plugin.getConfig().getInt("ranks." + rankName + ".elo");

            ranks.put(elo, new Rank(displayName, elo));
            PluginLogger.info("Rank " + rankName + " loaded with " + elo + " elo.");
        });

        eloOnWin = plugin.getConfig().getInt("elo-on-win");
        eloOnLose = plugin.getConfig().getInt("elo-on-lose");

        maximumEloDifference = plugin.getConfig().getInt("maximum-elo-difference");
        timeToAccept = plugin.getConfig().getInt("time-to-accept");
        timeBeforeBattle = plugin.getConfig().getInt("time-before-battle");

        fightLocation = plugin.getConfig().getLocation("fight-location");
    }

    /**
     * Get the rank the player should have according to his elo.
     * @param elo player elo
     * @return rank
     */
    public Rank getRank(int elo) {
        Rank rank = null;

        System.out.println("elo: " + elo);

        for (Map.Entry<Integer, Rank> entry : ranks.entrySet()) {
            System.out.println("rank: " + entry.getValue().getName());
            System.out.println("rank elo: " + entry.getKey());
            if (elo >= entry.getKey()) {
                rank = entry.getValue();
            }
        }

        return rank;
    }

    /**
     * Get the amount of elo the player should win on win.
     * @return elo on win
     */
    public int getEloOnWin() {
        return eloOnWin;
    }

    /**
     * Get the amount of elo the player should lose on lose.
     * @return elo on lose
     */
    public int getEloOnLose() {
        return eloOnLose;
    }

    /**
     * Get the maximum elo difference between two players.
     * @return maximum elo difference
     */
    public int getMaximumEloDifference() {
        return maximumEloDifference;
    }

    /**
     * Get the time to accept a battle.
     * @return time to accept
     */
    public int getTimeToAccept() {
        return timeToAccept;
    }

    /**
     * Get the time before a battle.
     * @return time before battle
     */
    public int getTimeBeforeBattle() {
        return timeBeforeBattle;
    }

    /**
     * Get the fight location.
     * @return fight location
     */
    public Location getFightLocation() {
        return fightLocation;
    }

}
