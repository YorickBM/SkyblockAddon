package yorickbm.skyblockaddon.util.JSON;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONEncoder {
    public static <T extends JSONSerializable> T loadFromFile(Path filePath, Class<T> clazz) throws Exception {
        String content = new String(Files.readAllBytes(filePath));
        T instance = clazz.getDeclaredConstructor().newInstance();
        instance.fromJSON(content);
        return instance;
    }
}
