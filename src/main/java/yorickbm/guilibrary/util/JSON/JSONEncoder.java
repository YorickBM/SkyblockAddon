package yorickbm.guilibrary.util.JSON;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JSONEncoder {
    public static <T extends JSONSerializable> T loadFromFile(Path filePath, Class<T> clazz) throws Exception {
        String content = new String(Files.readAllBytes(filePath));
        T instance = clazz.getDeclaredConstructor().newInstance();
        instance.fromJSON(content);
        return instance;
    }

    public static <T extends JSONSerializable> Collection<T> loadFromFolder(Path folderPath, Class<T> clazz) {
        Collection<T> objects = new ArrayList<>();

        //Create folder if it doesn't exist
        if (!folderPath.toFile().exists()) {
            boolean rslt = folderPath.toFile().mkdirs();
            if (!rslt) {
                throw new RuntimeException("Failed to create container at '" + folderPath.toFile().getAbsolutePath() + "'.");
            }
        }

        //List files into list
        List<Path> files = new ArrayList<>();
        try {
            Files.list(folderPath).forEach(files::add);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Path file : files) {
            try {
                objects.add(loadFromFile(file, clazz));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return objects;
    }

}
