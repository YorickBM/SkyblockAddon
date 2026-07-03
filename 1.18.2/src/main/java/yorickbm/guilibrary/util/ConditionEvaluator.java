package yorickbm.guilibrary.util;

import net.minecraftforge.fml.ModList;

import java.util.List;

public class ConditionEvaluator {

    public interface Context {
        boolean isAdmin();

        boolean isOp();

        boolean isPart();
    }

    public static boolean matches(List<String> conditions, Context context) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        for (String condition : conditions) {
            if (!matchesSingle(condition, context)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesSingle(String condition, Context context) {
        boolean negate = condition.startsWith("!");
        String key = negate ? condition.substring(1) : condition;
        boolean result = evaluate(key, context);
        return negate != result;
    }

    private static boolean evaluate(String key, Context context) {
        if (key.startsWith("mod:")) {
            return ModList.get().isLoaded(key.substring(4));
        }
        if (context == null) {
            return false;
        }
        switch (key) {
            case "is_admin":
                return context.isAdmin();
            case "is_op":
                return context.isOp();
            case "is_part":
                return context.isPart();
            default:
                return false;
        }
    }
}
