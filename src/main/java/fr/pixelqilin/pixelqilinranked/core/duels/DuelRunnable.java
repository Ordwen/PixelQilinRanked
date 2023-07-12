package fr.pixelqilin.pixelqilinranked.core.duels;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DuelRunnable extends BukkitRunnable {

    private final Player initiator;
    private final Player target;
    private int cooldown;

    public DuelRunnable(Duel duel, int cooldown) {
        this.initiator = duel.getInitiator();
        this.target = duel.getTarget();
        this.cooldown = cooldown;
    }

    @Override
    public void run() {
        if (cooldown == 0) {
            initiator.sendTitle("§aLe duel commence !", "", 10, 70, 20);
            target.sendTitle("§aLe duel commence !", "", 10, 70, 20);

            DuelsManager.addBattle(initiator, target);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pokebattle " + initiator.getName() + " " + target.getName());
            cancel();
        }

        initiator.sendTitle("§cDuel dans", String.valueOf(cooldown), 10, 70, 20);
        target.sendTitle("§cDuel dans", String.valueOf(cooldown), 10, 70, 20);

        cooldown--;
    }
}
