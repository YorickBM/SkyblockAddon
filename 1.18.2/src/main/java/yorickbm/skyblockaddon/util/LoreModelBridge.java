package yorickbm.skyblockaddon.util;

import yorickbm.guilibrary.util.ConditionEvaluator;
import yorickbm.guilibrary.util.LoreRenderer;

import java.util.ArrayList;
import java.util.List;

public class LoreModelBridge {

    public static List<yorickbm.guilibrary.JSON.LoreLineJson> toGuiLibrary(
            List<yorickbm.skyblockaddon.core.JSON.LoreLineJson> coreLines) {
        List<yorickbm.guilibrary.JSON.LoreLineJson> result = new ArrayList<>();
        if (coreLines == null) {
            return result;
        }
        for (yorickbm.skyblockaddon.core.JSON.LoreLineJson coreLine : coreLines) {
            result.add(toGuiLibrary(coreLine));
        }
        return result;
    }

    public static yorickbm.guilibrary.JSON.LoreLineJson toGuiLibrary(
            yorickbm.skyblockaddon.core.JSON.LoreLineJson coreLine) {
        yorickbm.guilibrary.JSON.LoreLineJson result = new yorickbm.guilibrary.JSON.LoreLineJson();

        if (coreLine.isJoin()) {
            result.setJoin(toGuiLibrary(coreLine.getJoin()));
        } else {
            List<yorickbm.guilibrary.JSON.LoreSegmentJson> segments = new ArrayList<>();
            if (coreLine.getSegments() != null) {
                for (yorickbm.skyblockaddon.core.JSON.LoreSegmentJson segment : coreLine.getSegments()) {
                    segments.add(toGuiLibrary(segment));
                }
            }
            result.setSegments(segments);
        }
        return result;
    }

    public static yorickbm.guilibrary.JSON.LoreJoinJson toGuiLibrary(
            yorickbm.skyblockaddon.core.JSON.LoreJoinJson coreJoin) {
        yorickbm.guilibrary.JSON.LoreJoinJson result = new yorickbm.guilibrary.JSON.LoreJoinJson();
        result.prefix = coreJoin.prefix != null ? toGuiLibrary(coreJoin.prefix) : null;
        result.separator = coreJoin.separator != null ? toGuiLibrary(coreJoin.separator) : null;
        result.conditions = coreJoin.conditions;

        List<yorickbm.guilibrary.JSON.LoreSegmentJson> entries = new ArrayList<>();
        if (coreJoin.entries != null) {
            for (yorickbm.skyblockaddon.core.JSON.LoreSegmentJson entry : coreJoin.entries) {
                entries.add(toGuiLibrary(entry));
            }
        }
        result.entries = entries;
        return result;
    }

    public static yorickbm.guilibrary.JSON.LoreSegmentJson toGuiLibrary(
            yorickbm.skyblockaddon.core.JSON.LoreSegmentJson coreSegment) {
        yorickbm.guilibrary.JSON.LoreSegmentJson result = new yorickbm.guilibrary.JSON.LoreSegmentJson();
        result.text = coreSegment.text;
        result.color = coreSegment.color;
        result.bold = coreSegment.bold;
        result.italic = coreSegment.italic;
        result.conditions = coreSegment.conditions;
        return result;
    }

    /** Convenience: convert + render in one call. */
    public static List<List<String>> render(
            List<yorickbm.skyblockaddon.core.JSON.LoreLineJson> coreLines,
            ConditionEvaluator.Context context) {
        return LoreRenderer.render(toGuiLibrary(coreLines), context);
    }
}
