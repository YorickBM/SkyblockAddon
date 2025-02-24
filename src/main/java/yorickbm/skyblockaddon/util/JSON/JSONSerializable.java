package yorickbm.skyblockaddon.util.JSON;

public interface JSONSerializable {
    String toJSON();
    void fromJSON(String json);
}
