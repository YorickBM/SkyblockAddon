package yorickbm.skyblockaddon.islands;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.eventbus.api.Cancelable;
import yorickbm.guilibrary.GUIItem;
import yorickbm.guilibrary.interfaces.GuiClickItemEvent;
import yorickbm.guilibrary.interfaces.ServerInterface;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;

@Cancelable
public class IslandEvents extends GuiClickItemEvent {
    private Island island;
    public Island getIsland() { return island; }

    public IslandEvents(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
        super(instance, player, slot, item);
        CompoundTag guiData = instance.getData();

        //If GUI does not contain an IslandId we do not add an island Object
        if(!guiData.contains("island_id")) return;

        player.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByUUID(guiData.getUUID("island_id"));

            //Fail action if island not found
            if(island == null) {
                setCanceled(true);
                return;
            }

            this.island = island;
        });
    }

    @Cancelable
    public static class TeleportToIsland extends IslandEvents {
        public TeleportToIsland(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
            super(instance, player, slot, item);
        }
    }

    @Cancelable
    public static class TravelToIsland extends IslandEvents {
        public TravelToIsland(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
            super(instance, player, slot, item);
        }
    }

    @Cancelable
    public static class LeaveIsland extends IslandEvents {
        public LeaveIsland(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
            super(instance, player, slot, item);
        }
    }

    @Cancelable
    public static class SetSpawnPoint extends IslandEvents {
        public SetSpawnPoint(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
            super(instance, player, slot, item);
        }
    }

    @Cancelable
    public static class ChangeVisibility extends IslandEvents {
        public ChangeVisibility(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
            super(instance, player, slot, item);
        }
    }

    @Cancelable
    public static class CreateNewGroup extends IslandEvents {
        public CreateNewGroup(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
            super(instance, player, slot, item);
        }
    }

    @Cancelable
    public static class RemoveGroup extends IslandEvents {
        public RemoveGroup(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
            super(instance, player, slot, item);
        }
    }

    @Cancelable
    public static class SetGroupPermission extends IslandEvents {
        public SetGroupPermission(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
            super(instance, player, slot, item);
        }
    }

    @Cancelable
    public static class SetPlayerGroup extends IslandEvents {
        public SetPlayerGroup(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
            super(instance, player, slot, item);
        }
    }

    @Cancelable
    public static class InviteNewMember extends IslandEvents {
        public InviteNewMember(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
            super(instance, player, slot, item);
        }
    }

    @Cancelable
    public static class KickMember extends IslandEvents {
        public KickMember(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
            super(instance, player, slot, item);
        }
    }

    @Cancelable
    public static class UpdateBiome extends IslandEvents {
        public UpdateBiome(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
            super(instance, player, slot, item);
        }
    }
}
