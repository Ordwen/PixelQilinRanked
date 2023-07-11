package fr.pixelqilin.pixelqilinranked.core.duels;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DuelsManager {

    private static final Map<Player, Player> battles = new HashMap<>();

    public static void addBattle(Player player1, Player player2) {
        battles.put(player1, player2);
        battles.put(player2, player1);
    }

    public static void removeBattle(Player player) {
        battles.remove(player);
    }

    public static boolean arePlayersInBattle(Player player1, Player player2) {

        return (battles.containsKey(player1) && battles.get(player1).equals(player2)) ||
                (battles.containsKey(player2) && battles.get(player2).equals(player1));
    }

    public static boolean isPlayerInBattle(Player player) {
        return battles.containsKey(player);
    }
}
