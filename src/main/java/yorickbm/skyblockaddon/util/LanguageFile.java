package yorickbm.skyblockaddon.util;

import java.util.HashMap;

public class LanguageFile {
    private static final HashMap<String, String> items = new HashMap<>();

    public static void init() {
        //Toolbar overlay
        items.put("toolbar.overlay.nothere", "Sorry, you cannot do this here.");

        //Chat language configurations
        items.put("chat.hover.copy", "Click to copy!");
        items.put("chat.hover.run.invite", "Click to accept invite (Valid for 60 minutes)!");
        items.put("chat.hover.run.teleport", "Click to accept teleport request (Valid for 60 seconds)!");
        items.put("chat.hover.run.rejoin", "Click to rejoin your island (Valid for 60 minutes)!");

        //Command response language configurations
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
        items.put("commands.island.create.generating", "We are generating your island, this may take some time. Please be patient.");
        items.put("commands.island.create.hasone", "You already have an island, use /island tp to teleport to your island!");
        items.put("commands.island.create.fail", "We could not create your island! Try again later.");
        items.put("commands.island.create.delay", "Please wait %s seconds before executing this command again.");
        items.put("commands.island.create.generating", "We are generating your island, please be patient...");

        items.put("commands.island.leave.hasnone", "You do not have an island yet!");
        items.put("commands.island.leave.success", "You have left your island...");
        items.put("commands.island.leave.undo", "If this is a mistake, you may rejoin your island by clicking this message.");

        items.put("commands.island.invite.hasone", "The player you are trying to invite already has an island!");
        items.put("commands.island.invite.hasnone", "You need to have an island to be able to invite someone.");
        items.put("commands.island.invite.nopermission", "You are not permitted to take this action, sorry.");
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
        items.put("commands.island.teleport.user.request.nopermission", "You are not permitted to take this action, sorry.");
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

        //GUI Actions
        items.put("island.member.kick", "Sorry, you have been kicked of the island.");

        //GUI language configuration
        items.put("guis.island.join", "You have joined %s's island!");

        items.put("guis.island.setspawn.notallowed", "Sorry you are not allowed to set your spawn outside of your own island.");

        items.put("guis.permissions.Teleport.title", "Teleport Requests");
        items.put("guis.permissions.Teleport.desc", "\u2666 Whom may accept teleport requests to your island\n\u2666 Setting this to everyone, will allow teleporting without request.");

        items.put("guis.permissions.Invite.title", "Invite New Members");
        items.put("guis.permissions.Invite.desc", "\u2666 Allow to invite new members to your island\n\u2666 Setting this to everyone, will allow joining without invite.");

        items.put("guis.permissions.PlaceBlocks.title", "Place Blocks");
        items.put("guis.permissions.PlaceBlocks.desc", "\u2666 Place any form of block on your island");

        items.put("guis.permissions.BreakBlocks.title", "Break Blocks");
        items.put("guis.permissions.BreakBlocks.desc", "\u2666 Break any form of block on your island");

        items.put("guis.permissions.TrampleFarmland.title", "Trample Farmland");
        items.put("guis.permissions.TrampleFarmland.desc", "\u2666 Trample farmland by jumping on top of it");


        items.put("guis.permissions.OpenBlocks.title", "Interact With Blocks");
        items.put("guis.permissions.OpenBlocks.desc", "\u2666 Interact with blocks on your island\n\u2666 I.E Chests, Storage Interfaces");

        items.put("guis.permissions.UseRedstone.title", "Interact With Redstone");
        items.put("guis.permissions.UseRedstone.desc", "\u2666 Interact with redstone on your island\n\u2666 I.E Buttons, Levers, Repeaters");

        items.put("guis.permissions.EnderPearl.title", "Use EnderPearl");
        items.put("guis.permissions.EnderPearl.desc", "\u2666 Use an enderpearl on your island to teleport");

        items.put("guis.permissions.ChorusFruit.title", "Use ChorusFruit");
        items.put("guis.permissions.ChorusFruit.desc", "\u2666 Use chorusfruit on your island to teleport");

        items.put("guis.permissions.InteractWithXP.title", "Collect XP Orbs");
        items.put("guis.permissions.InteractWithXP.desc", "\u2666 Gain XP levels from XP orbs on your island");

        items.put("guis.permissions.InteractWithGroundItems.title", "Interact With Ground Items");
        items.put("guis.permissions.InteractWithGroundItems.desc", "\u2666 Pickup and drop items on your island");

        items.put("guis.permissions.UseBucket.title", "Use Bucket");
        items.put("guis.permissions.UseBucket.desc", "\u2666 Empty and or fill a bucket with fluids on your island\n\u2666 I.E. Water, Lava");

        items.put("guis.permissions.UseBed.title", "Sleep in Bed");
        items.put("guis.permissions.UseBed.desc", "\u2666 Sleep within a bed on your island\n\u2666 Will also set their respawn point");

        items.put("guis.permissions.UseBonemeal.title", "Use Bonemeal");
        items.put("guis.permissions.UseBonemeal.desc", "\u2666 Use bonemeal, this includes all possible usages of bonemeal\n\u2666 I.E. Crops, Moss blocks");
    }

    public static String getForKey(String key) {
        if(!items.containsKey(key)) return key;
        return items.get(key);
    }

}
