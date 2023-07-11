package fr.pixelqilin.pixelqilinranked;

import com.pixelmonmod.pixelmon.Pixelmon;
import fr.pixelqilin.pixelqilinranked.commands.RankedCommand;
import fr.pixelqilin.pixelqilinranked.commands.RankedCompleter;
import fr.pixelqilin.pixelqilinranked.core.RankedQueue;
import fr.pixelqilin.pixelqilinranked.database.MySQLManager;
import fr.pixelqilin.pixelqilinranked.database.SQLManager;
import fr.pixelqilin.pixelqilinranked.events.BattleEndedListener;
import io.izzel.arclight.api.Arclight;
import org.bukkit.plugin.java.JavaPlugin;

public final class PixelQilinRanked extends JavaPlugin {

    public static PixelQilinRanked INSTANCE;

    private SQLManager sqlManager;
    private RankedQueue rankedQueue;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        this.saveDefaultConfig();

        sqlManager = new MySQLManager(this);
        rankedQueue = new RankedQueue(this);

        getCommand("ranked").setExecutor(new RankedCommand(this));
        getCommand("ranked").setTabCompleter(new RankedCompleter());

        Arclight.registerForgeEvent(this, Pixelmon.EVENT_BUS, new BattleEndedListener(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Get the instance of sql manager.
     * @return SQLManager instance.
     */
    public SQLManager getSqlManager() {
        return sqlManager;
    }

    /**
     * Get the instance of the ranked queue.
     * @return RankedQueue instance.
     */
    public RankedQueue getRankedQueue() {
        return rankedQueue;
    }
}
