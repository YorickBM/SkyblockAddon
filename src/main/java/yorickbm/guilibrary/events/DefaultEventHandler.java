package yorickbm.guilibrary.events;

import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.guilibrary.interfaces.GuiClickItemEvent;
import yorickbm.guilibrary.util.Helper;

public class DefaultEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void itemRenderer(GuiDrawItemEvent event) {
        if(event.isCanceled()) return; //Skip if canceled

        event.drawItem(); //Add item into inventory
        event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void fillerRenderer(GuiDrawFillerEvent event) {
        if(event.isCanceled()) return; //Skip if canceled

        event.drawItems(); //Add item into inventory
        event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void openGui(OpenMenuEvent event) {
        if(event.isCanceled()) return; //Skip if canceled

        event.setResult(event.getTarget().openMenu(event.getProvider()).isPresent() ? Event.Result.ALLOW : Event.Result.DENY);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void closeGui(ItemCloseMenuEvent event) {
        if(event.isCanceled()) return; //Skip if canceled

        event.getHolder().close();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemClick(GuiClickItemEvent event)
    {
        if(!event.isCanceled()) Helper.playSongToPlayer(event.getTarget(), SoundEvents.NOTE_BLOCK_CHIME, Helper.UI_SUCCESS_VOL, 1f); //Send success sound notification of click
        else Helper.playSongToPlayer(event.getTarget(), SoundEvents.NOTE_BLOCK_BASS, Helper.UI_SUCCESS_VOL, 1f); //Send failure sound notification of click

    }

}
