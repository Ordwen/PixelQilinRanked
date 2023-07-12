package fr.pixelqilin.pixelqilinranked.core;

import org.bukkit.entity.Player;

import java.util.HashSet;

public class RankedPlayer {

    private final HashSet<Player> deniedPlayers = new HashSet<>();
    private final String uuid;
    private String rank;
    private int elo;
    private int wins;
    private int losses;

    /**
     * Constructor of the RankedPlayer class.
     * @param uuid uuid of the player
     * @param rank rank of the player
     * @param elo elo of the player
     * @param wins wins of the player
     * @param losses losses of the player
     */
    public RankedPlayer(String uuid, String rank, int elo, int wins, int losses) {
        this.uuid = uuid;
        this.rank = rank;
        this.elo = elo;
        this.wins = wins;
        this.losses = losses;
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

    /**
     * Get the number of wins of the player.
     * @return wins amount
     */
    public int getWins() {
        return wins;
    }

    /**
     * Get the number of losses of the player.
     */
    public int getLosses() {
        return losses;
    }

    /**
     * Set the number of wins of the player.
     * @param wins wins amount
     */
    public void setWins(int wins) {
        this.wins = wins;
    }

    /**
     * Set the number of losses of the player.
     * @param losses losses amount
     */
    public void setLosses(int losses) {
        this.losses = losses;
    }

    /**
     * Increase the number of wins of the player.
     */
    public void increaseWins() {
        wins++;
    }

    /**
     * Increase the number of losses of the player.
     */
    public void increaseLosses() {
        losses++;
    }
}
