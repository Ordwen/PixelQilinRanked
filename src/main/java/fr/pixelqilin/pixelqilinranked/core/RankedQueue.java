package fr.pixelqilin.pixelqilinranked.core;

import fr.pixelqilin.pixelqilinranked.PixelQilinRanked;
import fr.pixelqilin.pixelqilinranked.core.ranks.RanksManager;
import fr.pixelqilin.pixelqilinranked.database.SQLManager;
import fr.pixelqilin.pixelqilinranked.utils.PluginLogger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class RankedQueue {

    private final SQLManager sqlManager;
    private final RanksManager ranksManager;

    public RankedQueue(PixelQilinRanked plugin) {
        this.sqlManager = plugin.getSqlManager();
        this.ranksManager = new RanksManager(plugin);
        ranksManager.loadRanks();
    }

    private final Queue<Player> rankedQueue = new LinkedList<>();
    private final Map<Player, RankedPlayer> players = new HashMap<>();

    /**
     * Add a player to the ranked queue.
     * @param player player to add
     */
    public void addToQueue(Player player) {
        if (rankedQueue.contains(player)) {
            player.sendMessage("§cVous êtes déjà dans la file d'attente.");
            return;
        }

        final RankedPlayer rankedPlayer = sqlManager.getSqlLoader().load(player.getUniqueId().toString());

        if (rankedPlayer == null) {
            players.put(player, new RankedPlayer(player.getUniqueId().toString(), ranksManager.getRank(0).getName(), 0));
            PluginLogger.info("New ranked player detected, creating new entry.");
        } else {
            players.put(player, rankedPlayer);
            PluginLogger.info("Ranked player detected, loading entry.");
        }

        rankedQueue.add(player);
        player.sendMessage("§eVous avez rejoint la file d'attente.");
    }

    /**
     * Remove a player from the ranked queue.
     * @param player player to remove
     */
    public void removeFromQueue(Player player) {
        if (!rankedQueue.contains(player)) {
            player.sendMessage("§cVous n'êtes pas dans la file d'attente.");
            return;
        }

        rankedQueue.remove(player);
        players.remove(player);

        player.sendMessage("§eVous avez quitté la file d'attente.");
    }

    /**
     * Display the current rank and its elo to a player.
     * @param player player to display the rank
     */
    public void checkEloSelf(Player player) {
        RankedPlayer rankedPlayer = getRankedPlayer(player, player, true);
        if (rankedPlayer == null) return;

        player.sendMessage("§aVous êtes classé " + rankedPlayer.getRank() + " avec " + rankedPlayer.getElo() + " elo.");
    }

    /**
     * Display the current rank and its elo to a player.
     * @param sender sender to display the rank
     */
    public void checkEloOther(CommandSender sender, Player target) {
        RankedPlayer rankedPlayer = getRankedPlayer(sender, target, false);
        if (rankedPlayer == null) return;

        sender.sendMessage("§aLe joueur " + target + " est classé " + rankedPlayer.getRank() + " avec " + rankedPlayer.getElo() + " elo.");
    }

    /**
     * Add elo to a player.
     * @param sender sender of the command
     * @param target target to add elo
     * @param amount amount of elo to add
     */
    public void addElo(CommandSender sender, Player target, int amount) {
        RankedPlayer rankedPlayer = getRankedPlayer(sender, target, false);
        if (rankedPlayer == null) return;

        int newElo = rankedPlayer.getElo() + amount;
        if (newElo < 0) newElo = 0;
        rankedPlayer.setElo(newElo);

        target.sendMessage("§aVous avez gagné " + amount + " elo.");
        sqlManager.getSqlSaver().save(target.getName(), rankedPlayer);
    }

    /**
     * Remove elo to a player.
     * @param sender sender of the command
     * @param target target to remove elo
     * @param amount amount of elo to remove
     */
    public void removeElo(CommandSender sender, Player target, int amount) {
        RankedPlayer rankedPlayer = getRankedPlayer(sender, target, false);
        if (rankedPlayer == null) return;

        int newElo = rankedPlayer.getElo() - amount;
        if (newElo < 0) newElo = 0;
        rankedPlayer.setElo(newElo);

        target.sendMessage("§cVous avez perdu " + amount + " elo.");
        sqlManager.getSqlSaver().save(target.getName(), rankedPlayer);
    }

    /**
     * Set elo to a player.
     * @param sender sender of the command
     * @param target target to set elo
     * @param amount amount of elo to set
     */
    public void setElo(CommandSender sender, Player target, int amount) {
        RankedPlayer rankedPlayer = getRankedPlayer(sender, target, false);
        if (rankedPlayer == null) return;

        rankedPlayer.setElo(amount);

        target.sendMessage("§aVous avez maintenant " + amount + " elo.");
        sqlManager.getSqlSaver().save(target.getName(), rankedPlayer);
    }

    /**
     * Load data of a player.
     * @param sender sender of the command
     * @param target target to load data
     * @return RankedPlayer of the target
     */
    private RankedPlayer getRankedPlayer(CommandSender sender, Player target, boolean self) {

        RankedPlayer rankedPlayer = players.get(target);
        if (rankedPlayer == null) rankedPlayer = sqlManager.getSqlLoader().load(target.getUniqueId().toString());
        if (rankedPlayer == null) {
            if (!self) {
                sender.sendMessage("§cLe joueur " + target.getName() + " n'a pas encore de classement !");
                sender.sendMessage("§cIl doit rejoindre la file d'attente pour créer son profil.");
            }
            else {
                target.sendMessage("§cVous n'avez pas encore de classement !");
                target.sendMessage("§cVous devez rejoindre la file d'attente pour créer votre profil.");
            }
        }

        return rankedPlayer;
    }
}
