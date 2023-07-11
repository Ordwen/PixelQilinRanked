package fr.pixelqilin.pixelqilinranked.core.duels;

import org.bukkit.entity.Player;

public class Duel {

    private final Player initiator;
    private final Player target;
    private boolean initatorAccepted = false;
    private boolean targetAccepted = false;

    public Duel(Player iniator, Player target) {
        this.initiator = iniator;
        this.target = target;
    }

    /**
     * Get the initiator of the duel
     * @return the initiator
     */
    public Player getInitiator() {
        return initiator;
    }

    /**
     * Get the target of the duel
     * @return the target
     */
    public Player getTarget() {
        return target;
    }

    /**
     * Get the opponent of the player
     * @param player the player
     * @return the opponent
     */
    public Player getOpponent(Player player) {
        if (player.equals(initiator))
            return target;
        else
            return initiator;
    }

    /**
     * Accept the duel for one player
     * @param player the player who accept the duel
     */
    public void accept(Player player) {
        if (player.equals(initiator))
            initatorAccepted = true;
        else if (player.equals(target))
            targetAccepted = true;
    }

    /**
     * Check if the duel is accepted by both players
     * @return true if the duel is accepted by both players
     */
    public boolean isAcceptedByBoth() {
        return initatorAccepted && targetAccepted;
    }
}
