package fr.pixelqilin.pixelqilinranked.events;

import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CaptureEventListener {

    @SubscribeEvent
    public void onCapture(CaptureEvent.SuccessfulCapture event) {
        System.out.println("CaptureEventListener.onCapture");
    }
}
