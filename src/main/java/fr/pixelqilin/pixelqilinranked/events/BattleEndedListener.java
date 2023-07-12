package fr.pixelqilin.pixelqilinranked.events;

import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import fr.pixelqilin.pixelqilinranked.PixelQilinRanked;
import fr.pixelqilin.pixelqilinranked.core.duels.DuelsManager;
import fr.pixelqilin.pixelqilinranked.core.RankedQueue;
import fr.pixelqilin.pixelqilinranked.utils.PluginLogger;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BattleEndedListener {

    private final RankedQueue rankedQueue;

    public BattleEndedListener(PixelQilinRanked plugin) {
        this.rankedQueue = plugin.getRankedQueue();
    }

    @SubscribeEvent
    public void onBattleEnded(BattleEndEvent event) {
        final BattleController bc = event.getBattleController();
        if (bc.getPlayers().size() != 2) return;

        final String name1 = bc.getPlayers().get(0).getDisplayName();
        final String name2 = bc.getPlayers().get(1).getDisplayName();

        final Player player1 = Bukkit.getPlayer(name1);
        final Player player2 = Bukkit.getPlayer(name2);

        if (!DuelsManager.arePlayersInBattle(player1, player2)) return;

        if (bc.getPlayers().get(0).isDefeated) {
            rankedQueue.battleWon(player2);
            rankedQueue.battleLost(player1);
        }

        else if (bc.getPlayers().get(1).isDefeated) {
            rankedQueue.battleWon(player1);
            rankedQueue.battleLost(player2);
        }

        else {
            PluginLogger.error("Error while getting the winner of the battle.");
        }

        DuelsManager.removeBattle(player1);
        DuelsManager.removeBattle(player2);
    }
}
