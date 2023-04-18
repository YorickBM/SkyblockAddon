package yorickbm.skyblockaddon.util;

import java.util.HashMap;

public class LanguageFile {
    private static final HashMap<String, String> items = new HashMap<>();

    public static void init() {
        items.put("commands.island.nonplayer", "This command may only be executed by a player.");
        items.put("commands.island.notoverworld", "You can only execute this command in the overworld.");
        items.put("commands.island.hasnone", "For this command to be executed, you require to be part of an island.");

        items.put("commands.island.admin.offline", "Sorry, this player is offline...");
        items.put("commands.island.admin.getId.hasnone", "This player is not part of an island, sorry.");
        items.put("commands.island.admin.getId.success", "You can copy to clipboard the island id from %s by clicking on this message.");
        items.put("commands.island.admin.setId.hasone", "Beware, %s was already part of island %s. This is has been overridden!");
        items.put("commands.island.admin.setId.success", "%s new island has been set to %s");
        items.put("commands.island.admin.setId.teleport", "We are teleporting you to your new island...");

        items.put("commands.island.create.success", "Created your skyblock island!");
        items.put("commands.island.create.hasone", "You already have an island, use /island tp to teleport to your island!");
        items.put("commands.island.create.fail", "We could not create your island! Try again later.");

        items.put("commands.island.leave.hasnone", "You do not have an island yet!");
        items.put("commands.island.leave.success", "You have left your island...");
        items.put("commands.island.leave.undo", "If this is a mistake, you may rejoin your island by clicking this message.");

        items.put("commands.island.invite.hasone", "The player you are trying to invite already has an island!");
        items.put("commands.island.invite.hasnone", "You need to have an island to be able to invite someone.");
        items.put("commands.island.invite.offline", "The player you are trying to invite went offline!");
        items.put("commands.island.invite.success", "You have invited %s to your island.");
        items.put("commands.island.invite.invitation", "%s has invited you to join their island. Click this message, to join their island!");
        items.put("commands.island.accept.invalid", "Whoops, seems like your invitation has expired!");

        items.put("commands.island.accept.hasone", "You already have an island!");
        items.put("commands.island.accept.success", "You have joined %s's island!");

        items.put("commands.island.teleport.success", "We have teleported you to your island.");
        items.put("commands.island.teleport.hasnone", "You do not have an island yet!");
        items.put("commands.island.teleport.user.notfound", "We could not find the players island you are trying to teleport to.");
        items.put("commands.island.teleport.user.hasnone", "%s does not have an island to teleport too!");
        items.put("commands.island.teleport.user.success", "You have teleported to %s's island.");
        items.put("commands.island.teleport.user.request", "%s wants to teleport to your island. Click this message, to accept teleport request.");
        items.put("commands.island.teleport.user.request.send", "You have requested to teleport to %s's island!");
        items.put("commands.island.teleport.user.request.success", "We have teleported %s to your island.");
        items.put("commands.island.teleport.user.request.notoverworld", "%s is not in the overworld, and cannot be teleported to you!");
        items.put("commands.island.teleport.user.offline", "The player who's teleport request you accepted is not online.");
        items.put("commands.island.teleport.user.expired", "The time to accept this teleportation request has expired.");

        items.put("commands.island.kick.notallowed", "You are not allowed to kick this member!");
        items.put("commands.island.kick.notpart", "The player you are trying to kick is not part of your island.");
        items.put("commands.island.kick.kicked", "%s has kicked you of their island!");
        items.put("commands.island.kick.success", "You have kicked %s from your island!");
        items.put("commands.island.kick.hasnone", "The player you are trying to kick is not part of your island.");

        items.put("commands.island.biome.changed", "Your islands biome had changed too %s.");

        items.put("commands.island.undo.hasnone", "You are required to leave an island before we can undo it.");
        items.put("commands.island.undo.expired", "Your time to rejoin the island you left expired.");
        items.put("commands.island.undo.hasone", "We have left your current island for you, to rejoin your previous one!");
        items.put("commands.island.undo.success", "You have rejoined your previous island!");
    }

    public static String getForKey(String key) {
        return items.get(key);
    }

}
