package fr.pixelqilin.pixelqilinranked.core;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import fr.pixelqilin.pixelqilinranked.PixelQilinRanked;
import fr.pixelqilin.pixelqilinranked.core.duels.Duel;
import fr.pixelqilin.pixelqilinranked.core.duels.DuelRunnable;
import fr.pixelqilin.pixelqilinranked.core.duels.DuelsManager;
import fr.pixelqilin.pixelqilinranked.core.ranks.Rank;
import fr.pixelqilin.pixelqilinranked.core.ranks.RanksManager;
import fr.pixelqilin.pixelqilinranked.database.SQLManager;
import fr.pixelqilin.pixelqilinranked.utils.PluginLogger;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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

    private final Map<Player, Duel> waitingAnswers = new HashMap<>();

    /**
     * Update the player rank when he won a battle.
     *
     * @param player player who won the battle
     */
    public void battleWon(Player player) {
        final RankedPlayer rankedPlayer = players.get(player);

        final int elo = rankedPlayer.getElo() + ranksManager.getEloOnWin();
        final Rank newRank = ranksManager.getRank(elo);

        rankedPlayer.setElo(elo);
        rankedPlayer.setRank(newRank.getName());
        rankedPlayer.increaseWins();

        sqlManager.getSqlSaver().save(player.getUniqueId().toString(), rankedPlayer);

        player.sendMessage("§aVous avez gagné §6" + ranksManager.getEloOnWin() + " §apoints d'elo.");
        player.sendMessage("§aVous êtes classé §6" + newRank.getName() + " §aavec§6 " + elo + " §apoints d'elo.");

        removeFromQueue(player);
    }

    /**
     * Update the player rank when he lost a battle.
     *
     * @param player player who lost the battle
     */
    public void battleLost(Player player) {
        final RankedPlayer rankedPlayer = players.get(player);

        int elo = rankedPlayer.getElo() - ranksManager.getEloOnLose();
        if (elo < 0) elo = 0;

        final Rank newRank = ranksManager.getRank(elo);

        rankedPlayer.setElo(elo);
        rankedPlayer.setRank(newRank.getName());
        rankedPlayer.increaseLosses();

        sqlManager.getSqlSaver().save(player.getUniqueId().toString(), rankedPlayer);

        player.sendMessage("§cVous avez perdu §6" + ranksManager.getEloOnLose() + " §cpoints d'elo.");
        player.sendMessage("§cVous êtes classé §6" + newRank.getName() + " §cavec§6 " + elo + " §cpoints d'elo.");

        removeFromQueue(player);
    }

    /**
     * Add a player to the ranked queue.
     *
     * @param player player to add
     */
    public void addToQueue(Player player) {
        if (rankedQueue.contains(player)) {
            player.sendMessage("§cVous êtes déjà dans la file d'attente.");
            return;
        }

        if (!haveOnePokemonHealed(player)) {
            player.sendMessage("§cVous n'avez aucun Pokémon en état de combattre !");
            player.sendMessage("§cIl vous en faut au moins un pour rejoindre la file d'attente.");
            removeFromQueue(player);
            return;
        }

        RankedPlayer rankedPlayer = sqlManager.getSqlLoader().load(player.getUniqueId().toString());

        if (rankedPlayer == null) {
            rankedPlayer = new RankedPlayer(player.getUniqueId().toString(), ranksManager.getRank(0).getName(), 0, 0, 0);
            players.put(player, rankedPlayer);
            PluginLogger.info("New ranked player detected, creating new entry.");
        } else {
            players.put(player, rankedPlayer);
            PluginLogger.info("Ranked player detected, loading entry.");
        }

        rankedQueue.add(player);
        player.sendMessage("§eVous avez rejoint la file d'attente.");

        searchForBattle(player, rankedPlayer);
    }

    /**
     * Remove a player from the ranked queue.
     *
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
     *
     * @param player player to display the rank
     */
    public void checkEloSelf(Player player) {
        RankedPlayer rankedPlayer = getRankedPlayer(player, player, true);
        if (rankedPlayer == null) return;

        player.sendMessage("§aVous êtes classé " + rankedPlayer.getRank() + " §aavec " + rankedPlayer.getElo() + " elo.");
        player.sendMessage("§aVous avez gagné " + rankedPlayer.getWins() + " fois et perdu " + rankedPlayer.getLosses() + " fois.");
    }

    /**
     * Display the current rank and its elo to a player.
     *
     * @param sender sender to display the rank
     */
    public void checkEloOther(CommandSender sender, Player target) {
        RankedPlayer rankedPlayer = getRankedPlayer(sender, target, false);
        if (rankedPlayer == null) return;

        sender.sendMessage("§aLe joueur " + target.getName() + " est classé " + rankedPlayer.getRank() + " §aavec " + rankedPlayer.getElo() + " elo.");
        sender.sendMessage("§aIl a gagné " + rankedPlayer.getWins() + " fois et perdu " + rankedPlayer.getLosses() + " fois.");
    }

    /**
     * Update the rank of a player when its elo is updated.
     *
     * @param target       target to update the rank
     * @param rankedPlayer ranked player to update
     * @param regress      if the player has lost elo
     */
    private void updateRank(Player target, RankedPlayer rankedPlayer, boolean regress) {
        final int elo = rankedPlayer.getElo();
        rankedPlayer.setRank(ranksManager.getRank(elo).getName());

        if (regress) target.sendMessage("§cVous êtes classé " + rankedPlayer.getRank() + "§c.");
        else target.sendMessage("§aVous êtes classé " + rankedPlayer.getRank() + " §a!");
    }

    /**
     * Add elo to a player.
     *
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
        updateRank(target, rankedPlayer, false);

        sender.sendMessage("§eVous avez ajouté " + amount + " elo à " + target.getName() + ".");
        target.sendMessage("§aVous avez gagné " + amount + " elo.");

        sqlManager.getSqlSaver().save(target.getName(), rankedPlayer);
    }

    /**
     * Remove elo to a player.
     *
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
        updateRank(target, rankedPlayer, true);

        sender.sendMessage("§eVous avez retiré " + amount + " elo à " + target.getName() + ".");
        target.sendMessage("§cVous avez perdu " + amount + " elo.");

        sqlManager.getSqlSaver().save(target.getName(), rankedPlayer);
    }

    /**
     * Set elo to a player.
     *
     * @param sender sender of the command
     * @param target target to set elo
     * @param amount amount of elo to set
     */
    public void setElo(CommandSender sender, Player target, int amount) {
        RankedPlayer rankedPlayer = getRankedPlayer(sender, target, false);
        if (rankedPlayer == null) return;

        rankedPlayer.setElo(amount);
        updateRank(target, rankedPlayer, false);

        sender.sendMessage("§eVous avez défini l'elo de " + target.getName() + " à " + amount + ".");
        target.sendMessage("§aVous avez maintenant " + amount + " elo.");

        sqlManager.getSqlSaver().save(target.getName(), rankedPlayer);
    }

    /**
     * Load data of a player.
     *
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
            } else {
                target.sendMessage("§cVous n'avez pas encore de classement !");
                target.sendMessage("§cVous devez rejoindre la file d'attente pour créer votre profil.");
            }
        }

        return rankedPlayer;
    }

    /**
     * Check if the player have at least one Pokémon healed.
     *
     * @param player player to check
     * @return true if the player have at least one Pokémon healed
     */
    private boolean haveOnePokemonHealed(Player player) {
        final PlayerPartyStorage partyStorage = StorageProxy.getParty(player.getUniqueId());

        for (Pokemon pokemon : partyStorage.getTeam()) {
            if (pokemon.getHealth() > 0) return true;
        }
        return false;
    }

    /**
     * Search a battle for a player, by checking the elo of the players in the queue.
     *
     * @param player       player to search a battle
     * @param rankedPlayer RankedPlayer of the player
     */
    private void searchForBattle(Player player, RankedPlayer rankedPlayer) {
        if (!haveOnePokemonHealed(player)) {
            player.sendMessage("§cVous n'avez aucun Pokémon soigné !");
            player.sendMessage("§cVous devez en soigner au moins un pour rejoindre la file d'attente.");
            removeFromQueue(player);
            return;
        }

        final int elo = rankedPlayer.getElo();
        final int difference = ranksManager.getMaximumEloDifference();

        final int minElo = Math.max(elo - difference, 0);
        final int maxElo = elo + difference;

        for (Player other : rankedQueue) {
            if (other == player) continue;
            if (rankedPlayer.getDeniedPlayers().contains(other)) continue;
            if (waitingAnswers.containsKey(other)) continue;
            if (DuelsManager.isPlayerInBattle(other)) continue;

            final RankedPlayer otherRankedPlayer = players.get(player);
            if (otherRankedPlayer.getDeniedPlayers().contains(player)) continue;

            if (otherRankedPlayer.getElo() >= minElo && otherRankedPlayer.getElo() <= maxElo) {
                proposeBattle(player, other, rankedPlayer, otherRankedPlayer);
                return;
            }
        }

        player.sendMessage("§cAucun adversaire n'a été trouvé. Vous êtes dans la file d'attente.");
    }

    /**
     * Propose a battle to two players.
     *
     * @param player            player who proposed the battle
     * @param other             player who is proposed the battle
     * @param rankedPlayer      stats from player who proposed the battle
     * @param otherRankedPlayer stats from player who is proposed the battle
     */
    private void proposeBattle(Player player, Player other, RankedPlayer rankedPlayer, RankedPlayer otherRankedPlayer) {

        final Duel duel = new Duel(player, other);
        waitingAnswers.put(player, duel);
        waitingAnswers.put(other, duel);

        player.sendMessage("§aVous avez trouvé un adversaire !");
        player.sendMessage("§aVous allez combattre " + other.getName() + " (rang : " + otherRankedPlayer.getRank() + "§a).");
        player.sendMessage("§eVous avez " + ranksManager.getTimeToAccept() + " secondes pour accepter.");

        other.sendMessage("§aVous avez trouvé un adversaire !");
        other.sendMessage("§aVous allez combattre " + player.getName() + " (rang : " + rankedPlayer.getRank() + "§a).");
        other.sendMessage("§eVous avez " + ranksManager.getTimeToAccept() + " secondes pour accepter.");

        final TextComponent accept = new TextComponent("§a[Accepter]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ranked accept"));

        final TextComponent decline = new TextComponent("§c[Décliner]");
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ranked decline"));

        final TextComponent empty = new TextComponent(" ");

        player.spigot().sendMessage(accept, empty, decline);
        other.spigot().sendMessage(accept, empty, decline);

        Bukkit.getScheduler().runTaskLaterAsynchronously(PixelQilinRanked.INSTANCE, () -> {
            if (waitingAnswers.containsKey(player)) decline(player);
            if (waitingAnswers.containsKey(other)) decline(other);
        }, ranksManager.getTimeToAccept() * 20L);
    }

    /**
     * Make a player accept a duel.
     *
     * @param player player who accept the duel
     */
    public void accept(Player player) {
        if (!waitingAnswers.containsKey(player)) {
            player.sendMessage("§cVous n'avez pas de duel en attente.");
            return;
        }

        final Duel duel = waitingAnswers.get(player);
        duel.accept(player);

        if (duel.isAcceptedByBoth()) launchDuel(duel);
    }

    /**
     * Make a player decline a duel.
     *
     * @param player player who decline the duel
     */
    public void decline(Player player) {
        if (!waitingAnswers.containsKey(player)) {
            player.sendMessage("§cVous n'avez pas de duel en attente.");
            return;
        }

        final Duel duel = waitingAnswers.get(player);
        final Player opponent = duel.getOpponent(player);

        waitingAnswers.remove(player);
        waitingAnswers.remove(opponent);

        players.get(player).getDeniedPlayers().add(opponent);
        players.get(opponent).getDeniedPlayers().add(player);

        player.sendMessage("§cVous avez refusé le duel.");
        opponent.sendMessage("§c" + player.getName() + " a refusé le duel.");

        searchForBattle(player, players.get(player));
        searchForBattle(opponent, players.get(opponent));
    }

    /**
     * Launch a duel when both players accepted.
     *
     * @param duel duel to launch
     */
    private void launchDuel(Duel duel) {
        final Player initiator = duel.getInitiator();
        final Player target = duel.getTarget();

        initiator.sendMessage("§aLe duel commence dans " + ranksManager.getTimeBeforeBattle() + " secondes !");
        target.sendMessage("§aLe duel commence dans " + ranksManager.getTimeBeforeBattle() + " secondes !");

        waitingAnswers.remove(initiator);
        waitingAnswers.remove(target);

        initiator.teleport(ranksManager.getFightLocation());
        target.teleport(ranksManager.getFightLocation());

        final DuelRunnable duelRunnable = new DuelRunnable(duel, ranksManager.getTimeBeforeBattle());
        duelRunnable.runTaskTimer(PixelQilinRanked.INSTANCE, 0L, 20L);
    }

    public Map<Player, RankedPlayer> getMap() {
        return players;
    }
}
