package fr.pixelqilin.pixelqilinranked.core;

import org.bukkit.entity.Player;

import java.util.HashSet;

public class RankedPlayer {

    private final HashSet<Player> deniedPlayers = new HashSet<>();
    private final String uuid;
    private String rank;
    private int elo;

    /**
     * Constructor of the RankedPlayer class.
     * @param uuid uuid of the player
     * @param rank rank of the player
     * @param elo elo of the player
     */
    public RankedPlayer(String uuid, String rank, int elo) {
        this.uuid = uuid;
        this.rank = rank;
        this.elo = elo;
    }

    /**
     * Get the uuid of the player.
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Get the rank of the player.
     * @return rank name
     */
    public String getRank() {
        return rank;
    }

    /**
     * Get the elo of the player.
     * @return elo amount
     */
    public int getElo() {
        return elo;
    }

    /**
     * Set the rank of the player.
     * @param rank rank name
     */
    public void setRank(String rank) {
        this.rank = rank;
    }

    /**
     * Set the elo of the player.
     * @param elo elo amount
     */
    public void setElo(int elo) {
        this.elo = elo;
    }

    /**
     * Get the players against whom the player don't want to play.
     * @return denied players
     */
    public HashSet<Player> getDeniedPlayers() {
        return deniedPlayers;
    }

    /**
     * Add a player to the denied players list.
     * @param player player to add
     */
    public void addDeniedPlayer(Player player) {
        deniedPlayers.add(player);
    }

    /**
     * Remove a player from the denied players list.
     * @param player player to remove
     */
    public void removeDeniedPlayer(Player player) {
        deniedPlayers.remove(player);
    }
}
