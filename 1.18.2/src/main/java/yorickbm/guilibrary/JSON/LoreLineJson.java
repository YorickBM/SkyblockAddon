package yorickbm.guilibrary.JSON;

import java.util.List;

public class LoreLineJson {
    private List<LoreSegmentJson> segments;
    private LoreJoinJson join;

    public boolean isJoin() {
        return join != null;
    }

    public LoreJoinJson getJoin() {
        return join;
    }

    public List<LoreSegmentJson> getSegments() {
        return segments;
    }

    public void setJoin(LoreJoinJson join) {
        this.join = join;
    }

    public void setSegments(List<LoreSegmentJson> segments) {
        this.segments = segments;
    }
}
