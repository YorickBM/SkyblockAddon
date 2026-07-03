package yorickbm.guilibrary.util;

import com.google.gson.Gson;
import yorickbm.guilibrary.JSON.LoreJoinJson;
import yorickbm.guilibrary.JSON.LoreLineJson;
import yorickbm.guilibrary.JSON.LoreSegmentJson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoreRenderer {

    private static final Gson GSON = new Gson();

    public static List<List<String>> render(List<LoreLineJson> lines, ConditionEvaluator.Context context) {
        List<List<String>> result = new ArrayList<>();
        if (lines == null) {
            return result;
        }

        for (LoreLineJson line : lines) {
            List<String> rendered = line.isJoin()
                    ? renderJoin(line.getJoin(), context)
                    : renderSegments(line.getSegments(), context);

            if (!rendered.isEmpty()) {
                result.add(rendered);
            }
        }
        return result;
    }

    private static List<String> renderSegments(List<LoreSegmentJson> segments, ConditionEvaluator.Context context) {
        List<String> out = new ArrayList<>();
        if (segments == null) {
            return out;
        }
        for (LoreSegmentJson segment : segments) {
            if (ConditionEvaluator.matches(segment.conditions, context)) {
                out.add(toComponentJson(segment));
            }
        }
        return out;
    }

    private static List<String> renderJoin(LoreJoinJson join, ConditionEvaluator.Context context) {
        if (!ConditionEvaluator.matches(join.conditions, context)) {
            return List.of();
        }

        List<LoreSegmentJson> visible = new ArrayList<>();
        if (join.entries != null) {
            for (LoreSegmentJson entry : join.entries) {
                if (ConditionEvaluator.matches(entry.conditions, context)) {
                    visible.add(entry);
                }
            }
        }
        if (visible.isEmpty()) {
            return List.of();
        }

        List<String> out = new ArrayList<>();
        if (join.prefix != null) {
            out.add(toComponentJson(join.prefix));
        }
        for (int i = 0; i < visible.size(); i++) {
            out.add(toComponentJson(visible.get(i)));
            if (i != visible.size() - 1 && join.separator != null) {
                out.add(toComponentJson(join.separator));
            }
        }
        return out;
    }

    private static String toComponentJson(LoreSegmentJson segment) {
        Map<String, Object> component = new LinkedHashMap<>();
        component.put("text", segment.text != null ? segment.text : "");
        if (segment.color != null) component.put("color", segment.color);
        if (segment.bold != null) component.put("bold", segment.bold.toString());
        if (segment.italic != null) component.put("italic", segment.italic.toString());
        return GSON.toJson(component);
    }
}
