package yorickbm.skyblockaddon.gui.json;

import com.google.gson.Gson;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.gui.GUIManager;
import yorickbm.skyblockaddon.gui.ServerGui;
import yorickbm.skyblockaddon.gui.util.TargetHolder;
import yorickbm.skyblockaddon.gui.util.TargetType;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.util.FunctionRegistry;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;
import yorickbm.skyblockaddon.util.ServerHelper;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class GuiAction implements JSONSerializable {
    private static final Logger LOGGER = LogManager.getLogger();

    private String onClick;
    private String onSecondClick;

    public void onPrimaryClick(ItemStack item,
                               TargetHolder source,
                               Island cSource,
                               ServerGui gui) {
        handleClick(onClick, item, source, cSource, gui);
    }
    public void onSecondaryClick(ItemStack item,
                                 TargetHolder source,
                                 Island cSource,
                                 ServerGui gui) {
        handleClick(onSecondClick, item,  source, cSource, gui);
    }

    private void handleClick(String action, ItemStack item, TargetHolder source, Island cSource, ServerGui gui) {
        ServerHelper.playSongToPlayer((ServerPlayer) source.getEntity(), SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);

        TargetHolder target = null;
        AtomicReference<Island> cTarget = new AtomicReference<>();
        CompoundTag modTag = item.getOrCreateTagElement(SkyblockAddon.MOD_ID);

        if(modTag.contains("islandId")) source.getEntity().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            cTarget.set(cap.getIslandByUUID(modTag.getUUID("islandId")));
        });

        if(modTag.contains("playerId")) {
            UUID playerUUID = modTag.getUUID("playerId");
            ServerPlayer playerEntity = Objects.requireNonNull(source.getEntity().getServer()).getPlayerList().getPlayer(playerUUID);
            target = new TargetHolder(playerEntity, playerUUID);
        }

        switch (action) {
            case "openMenu":
            case "setGroup":
            case "viewPermissions":
            case "openPermissionCategory":
                if(!modTag.contains("gui")) return;

                CompoundTag data = new CompoundTag();
                if(action.equals("setGroup")) data.putUUID("playerId", modTag.getUUID("playerId"));
                if(action.equals("viewPermissions")) {
                    data.putUUID("groupId", modTag.contains("groupId") ? modTag.getUUID("groupId") : gui.getNBT().getUUID("groupId"));
                }
                if(action.equals("openPermissionCategory")) {
                    data.putUUID("groupId", gui.getNBT().getUUID("groupId"));
                    data.putString("categoryId", modTag.getString("categoryId"));
                }

                boolean hasOpened = GUIManager.getInstance().openMenu(
                        modTag.getString("gui"),
                        source.getEntity(),
                        cSource, data);

                if(!hasOpened && source.getEntity() != null) source.getEntity().sendMessage(new TextComponent(String.format(SkyBlockAddonLanguage.getLocalizedString("menu.not.found"), modTag.getString("gui"))).withStyle(ChatFormatting.RED), source.getEntity().getUUID());

                return;

            case "permissionSet":
                String permissionId = modTag.getString("permissionId");
                UUID groupId = modTag.getUUID("groupId");
                cSource.getGroup(groupId).inversePermission(permissionId);

                gui.draw();
                ServerHelper.playSongToPlayer((ServerPlayer) source.getEntity(), SoundEvents.AMETHYST_BLOCK_CHIME, SkyblockAddon.UI_SUCCESS_VOL, 1f);
                return;

            case "teleportSource":
                cSource.teleportTo(source.getEntity());
                gui.close();

                ServerHelper.playSongToPlayer((ServerPlayer) source.getEntity(), SoundEvents.AMETHYST_BLOCK_CHIME, SkyblockAddon.UI_SUCCESS_VOL, 1f);
                source.getEntity().sendMessage(new TextComponent(
                        SkyBlockAddonLanguage.getLocalizedString("island.travel")
                ).withStyle(ChatFormatting.GREEN),source.getUuid());

                return;

            case "teleportTarget":
                cTarget.get().teleportTo(source.getEntity());
                gui.close();

                ServerHelper.playSongToPlayer((ServerPlayer) source.getEntity(), SoundEvents.AMETHYST_BLOCK_CHIME, SkyblockAddon.UI_SUCCESS_VOL, 1f);
                source.getEntity().sendMessage(new TextComponent(
                        SkyBlockAddonLanguage.getLocalizedString("island.travel")
                ).withStyle(ChatFormatting.GREEN),source.getUuid());

                return;

            case "groupSet":
                cSource.addMember(gui.getNBT().getUUID("playerId"), modTag.getUUID("groupId"));
                ServerHelper.playSongToPlayer((ServerPlayer) source.getEntity(), SoundEvents.AMETHYST_BLOCK_CHIME, SkyblockAddon.UI_SUCCESS_VOL, 1f);

                source.getEntity().sendMessage(new TextComponent(
                        SkyBlockAddonLanguage.getLocalizedString("island.member.group.set")
                        .formatted(
                            UsernameCache.getBlocking(gui.getNBT().getUUID("playerId")),
                            cSource.getGroup(modTag.getUUID("groupId")).getItem().getDisplayName().getString()
                        )
                ).withStyle(ChatFormatting.GREEN),source.getUuid());

                gui.close();
                return;

            case "kickMember":
                cSource.kickMember(source.getEntity(), target.getUuid());
                gui.close();
                return;

            case "setSpawnPoint":
                if(source.getEntity() == null) return; //Source is not a valid entity
                cSource.setSpawnPoint(source.getEntity().blockPosition());
                gui.draw(); //Update GUI items
                return;

            case "toggleVisibility":
                cSource.toggleVisibility();
                gui.draw();
                return;

            case "updateBiome":
                if(!modTag.contains("biome")) return;
                String biome = modTag.getString("biome");

                cSource.updateBiome(biome, (ServerLevel) source.getEntity().getLevel());
                gui.close();
                ServerHelper.playSongToPlayer((ServerPlayer) source.getEntity(), SoundEvents.AMETHYST_BLOCK_CHIME, SkyblockAddon.UI_SUCCESS_VOL, 1f);
                source.getEntity().sendMessage(
                        new TextComponent(
                                String.format(SkyBlockAddonLanguage.getLocalizedString("island.biome"), biome))
                                .withStyle(ChatFormatting.GREEN),
                        source.getUuid());
                return;

            case "previousPage":
                gui.previousPage();
                return;

            case "nextPage":
                gui.nextPage();
                return;

            case "createGroup":
                gui.close();

                UUID id = UUID.randomUUID();
                Function<ServerPlayer, Boolean> function = executor -> {
                    if(executor.getMainHandItem().isEmpty()) {
                        executor.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.group.failure").formatted(executor.getMainHandItem().getDisplayName().getString(), Objects.requireNonNull(executor.getMainHandItem().getItem().getRegistryName()).toString().split(":")[1]))
                                        .withStyle(ChatFormatting.RED)
                                ,executor.getUUID());
                        return false; //Keep hash function alive.
                    }

                    ServerHelper.playSongToPlayer(executor, SoundEvents.AMETHYST_BLOCK_CHIME, SkyblockAddon.UI_SUCCESS_VOL, 1f);
                    cSource.addGroup(new IslandGroup(UUID.randomUUID(), executor.getMainHandItem(), false));

                    executor.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.group.created").formatted(executor.getMainHandItem().getDisplayName().getString(), Objects.requireNonNull(executor.getMainHandItem().getItem().getRegistryName()).toString().split(":")[1]))
                                    .withStyle(ChatFormatting.GREEN)
                            ,executor.getUUID());

                    return true;
                };
                FunctionRegistry.registerFunction(id, function, 15);

                source.getEntity().sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.group.create"))
                            .withStyle(ChatFormatting.GREEN)
                            .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, FunctionRegistry.getCommand(id))))
                    ,source.getUuid());

                return;
        }
    }

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        GuiAction temp = gson.fromJson(json, GuiAction.class);
        this.onClick = temp.onClick;
        this.onSecondClick = temp.onSecondClick;
    }

    public boolean notNone() {
        return !this.onClick.equals("NONE") || !this.onSecondClick.equals("NONE");
    }

    public String getPrimaryType() {
        return this.onClick;
    }
}
