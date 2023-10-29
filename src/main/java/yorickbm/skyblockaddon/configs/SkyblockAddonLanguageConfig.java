package yorickbm.skyblockaddon.configs;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;

public class SkyblockAddonLanguageConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    private static final HashMap<String, ForgeConfigSpec.ConfigValue<String>> values = new HashMap<>();

    static {

        BUILDER.push("language");


        setupWarningLang();
        setupAdminLang();
        setupDefaultCommandsLang();

        register("biome.message", "Your islands biome had changed too %s.", "");

        values.put(
            "island.member.kick",
            BUILDER.comment("Message displayed for player who is kicked of their island.")
            .define("island.member.kick", "Sorry, you have been kicked of the island.")
        );
        values.put(
            "toolbar.overlay.nothere",
            BUILDER.comment("Message above toolbar when action is not permitted.")
            .define("toolbar.overlay", "Sorry, you cannot do this here.")
        );
        values.put(
            "chat.hover.copy",
            BUILDER.comment("Message displayed on hover for a copyable message.")
            .define("chat.hover.copy", "Click to copy!")
        );
        values.put(
            "chat.hover.run.invite",
            BUILDER.comment("Message displayed on hover for an island invite.")
            .define("chat.hover.run.invite", "Click to accept invite (Valid for 60 minutes)!")
        );
        values.put(
            "chat.hover.run.teleport",
            BUILDER.comment("Message displayed on hover for teleport request.")
            .define("chat.hover.run.teleport", "Sorry, you cannot do this here.")
        );
        values.put(
            "chat.hover.run.rejoin",
            BUILDER.comment("Message displayed on hover for rejoining left island.")
            .define("chat.hover.run.rejoin", "Click to rejoin your island (Valid for 60 minutes)!")
        );

        BUILDER.pop();
        SPEC = BUILDER.build();

    }

    public static String getForKey(String key) {
        if(!values.containsKey(key))
            return key; //Return lookup value if not found
        return values.get(key).get(); //Return value from config
    }

    private static void register(String key, String value, String comment) {
        values.put(
                key,
                BUILDER.comment(comment)
                        .define(key, value)
        );
    }

    private static void setupWarningLang() {
        register("commands.not.player", "This command may only be executed by a player.", "");
        register("commands.not.overworld", "You can only execute this command in the overworld.", "");
        register("commands.not.partofisland", "For this command to be executed, you require to be part of an island.", "");
        register("commands.not.online", "Sorry, this player is offline...", "");
        register("commands.not.has.island", "This player is not part of an island, sorry.", "");
        register("commands.not.has.one", "You do not have an island yet!", "");
        register("commands.not.found", "The island %s has not been found.", "");
        register("commands.not.part", "%s is not a member of the island %s.", "");
        register("commands.not.permitted", "You are not permitted to take this action, sorry.", "");
    }

    private static void setupAdminLang() {
        register("commands.admin.getId.success", "You can copy to clipboard the island id from %s by clicking on this message.", "");

        register("commands.admin.setId.success", "%s new island has been set to %s", "");
        register("commands.admin.setId.teleport", "We are teleporting you to your new island...", "");
        register("commands.admin.setId.ispart", "%s is already part of the island %s.", "");
        register("commands.admin.setId.override", "Beware, %s was already part of island %s. This is has been overridden!", "");

        register("commands.admin.where.none", "We could not find an id for the island you are on.", "");
        register("commands.admin.where.success", "You are currently on the island %s. Click on this message to copy.", "");

        register("commands.admin.promote.owner", "%s has been made the new owner of the island %s, %s has been demoted to admin.", "");
        register("commands.admin.promote.admin", "%s has been promoted to admin on the island %s.", "");
        register("commands.admin.promote.already.owner", "%s is already owner role of the island %s.", "");

        register("commands.admin.demote.admin", "%s has been demoted to admin on the island %s, %s is the new owner.", "");
        register("commands.admin.demote.member", "%s has been demoted to member on the island %s.", "");
        register("commands.admin.demote.already.member", "%s is already a member role of the island %s.", "");
        register("commands.admin.demote.not.possible", "%s cannot be demoted, as there is no one else on the island %s to become owner.", "");

        register("commands.admin.island.kick.success", "%s has been kicked of the island %s.", "");

        register("commands.admin.setOwner.already.owner", "%s is already owner of the island %s.", "");
        register("commands.admin.setOwner.success","%s has been made the new owner of the island %s, %s has been demoted to admin.", "");
    }

    private static void setupDefaultCommandsLang() {
        register("commands.spawn.teleport", "Teleporting you to spawn, this may take a second.", "");

        register("commands.create.success", "Created your skyblock island!", "");
        register("commands.create.has.one", "You already have an island, use /island tp to teleport to your island!", "");
        register("commands.create.fail", "We could not create your island! Try again later.", "");
        register("commands.create.delay", "Please wait %s seconds before executing this command again.", "");
        register("commands.create.generating", "We are generating your island, please be patient...", "");

        register("commands.leave.success", "You have left your island...", "");
        register("commands.leave.undo", "If this is a mistake, you may rejoin your island by clicking this message.", "");

        register("commands.invite.has.one", "The player you are trying to invite already has an island!", "");
        register("commands.invite.offline", "The player you are trying to invite went offline!", "");
        register("commands.invite.success", "You have invited %s to your island.", "");
        register("commands.invite.message", "%s has invited you to join their island. Click this message, to join their island!", "");

        register("commands.accept.has.one", "You already have an island!", "");
        register("commands.accept.invalid", "Whoops, seems like your invitation has expired!", "");
        register("commands.accept.success", "You have joined %s's island!", "");

        register("commands.teleport.success", "We have teleported you to your island.", "");
        register("commands.teleport.user.not.found", "We could not find the players island you are trying to teleport to.", ""); //Not used?
        register("commands.teleport.user.has.none", "%s does not have an island to teleport too!", "");
        register("commands.teleport.user.success", "You have teleported to %s's island.", "");
        register("commands.teleport.request.message", "%s wants to teleport to your island. Click this message, to accept teleport request.", "");
        register("commands.teleport.request.send", "You have requested to teleport to %s's island!", "");
        register("commands.teleport.request.success", "We have teleported %s to your island.", "");
        register("commands.teleport.request.not.overworld", "%s is not in the overworld, and cannot be teleported to you!", "");
        register("commands.teleport.request.offline", "The player who's teleport request you accepted is not online.", "");
        register("commands.teleport.request.expired", "The time to accept this teleportation request has expired.", "");

        register("commands.kick.not.allowed", "You are not allowed to kick this member!", "");
        register("commands.kick.not.part", "The player you are trying to kick is not part of your island.", "");
        register("commands.kick.message", "%s has kicked you of their island!", "");
        register("commands.kick.success", "You have kicked %s from your island!", "");

        register("commands.undo.fail", "You are required to leave an island before we can undo it.", "");
        register("commands.undo.expired", "Your time to rejoin the island you left expired.", "");
        register("commands.undo.success.has", "We have left your current island for you, to rejoin your previous one!", "");
        register("commands.undo.success", "You have rejoined your previous island!", "");
    }



}
