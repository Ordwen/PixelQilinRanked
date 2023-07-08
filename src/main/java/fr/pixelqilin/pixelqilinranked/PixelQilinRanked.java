package fr.pixelqilin.pixelqilinranked;

import com.pixelmonmod.pixelmon.Pixelmon;
import fr.pixelqilin.pixelqilinranked.events.CaptureEventListener;
import io.izzel.arclight.api.Arclight;
import org.bukkit.plugin.java.JavaPlugin;

public final class PixelQilinRanked extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Arclight.registerForgeEvent(this, Pixelmon.EVENT_BUS, new CaptureEventListener());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
