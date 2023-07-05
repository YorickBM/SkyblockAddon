package yorickbm.skyblockaddon.configs;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.HashMap;

public class SkyblockAddonLanguageConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    private static final HashMap<String, ForgeConfigSpec.ConfigValue<String>> values = new HashMap<>();
    private static final String DIAMOND = "";
    private static final String DOT = "";

    static {

        BUILDER.push("general");

        BUILDER.comment(">> Language options for Skyblock Addon GUI");

        BUILDER.comment(">> Language options for Skyblock Addon Chat messages");

        values.put(
                "island.member.kick",
                BUILDER.comment("Message displayed for player who is kicked of their island.")
                        .define("island.member.kick", "Sorry, you have been kicked of the island.")
        );

        values.put(
                "commands.not.overworld",
                BUILDER.comment("Message displayed when command not executed within overworld.")
                        .define("commands.not.overworld", "You can only execute this command in the overworld.")
        );
        values.put(
                "commands.not.player",
                BUILDER.comment("Message displayed when command not executed by a player.")
                        .define("commands.not.player", "This command may only be executed by a player.")
        );

        values.put(
                "commands.spawn.success",
                BUILDER.comment("Message displayed to player when spawn command has been executed successfully.")
                        .define("commands.spawn.success", "Teleporting you to spawn, this may take a second.")
        );

        values.put(
                "commands.island.hasnone",
                BUILDER.comment("Message displayed to player when island related command is executed without being part of one.")
                        .define("commands.island.hasnone", "For this command to be executed, you require to be part of an island.")
        );

        BUILDER.comment(">> Language options for Skyblock Addon Miscellaneous");

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

}
