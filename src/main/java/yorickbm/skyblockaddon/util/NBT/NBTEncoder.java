package yorickbm.skyblockaddon.util.NBT;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NBTEncoder {

    /**
     * Load NBT files into a collection of clazz.
     *
     * @param folderPath - Folder to load NBT files from
     * @param clazz      - Class instance in which to load the NBT files
     */
    public static <T extends IsUnique & NBTSerializable> Collection<T> loadFromFolder(Path folderPath, Class<T> clazz) throws RuntimeException {
        Collection<T> objects = new ArrayList<>();

        //Create folder if it doesn't exist
        if (!folderPath.toFile().exists()) {
            boolean rslt = folderPath.toFile().mkdirs();
            if (!rslt) {
                throw new RuntimeException("Failed to create container at '" + folderPath.toFile().getAbsolutePath() + "'.");
            }
        }

        //List files into list
        List<Path> nbtFiles = new ArrayList<>();
        try {
            Files.list(folderPath).forEach(nbtFiles::add);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Path path : nbtFiles) {
            try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
                CompoundTag NBT = NbtIo.readCompressed(fileInputStream);
                T instance = clazz.getDeclaredConstructor().newInstance();
                instance.deserializeNBT(NBT);
                objects.add(instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return objects;
    }

    /**
     * Save a collection of clazz to nbt files.
     *
     * @param collection - Collection of clazz
     */
    public static <T extends IsUnique & NBTSerializable> void saveToFile(Collection<T> collection, Path filePath) throws RuntimeException {
        for (T data : collection) {
            try {
                Path path = filePath.resolve(data.getId().toString() + ".nbt");

                //Create folder if it doesn't exist
                if (!filePath.toFile().exists()) {
                    boolean rslt = filePath.toFile().mkdirs();
                    if (!rslt) {
                        throw new RuntimeException("Failed to create container at '" + filePath.toFile().getAbsolutePath() + "'.");
                    }
                }

                //Create file if it does not exist
                if (!Files.exists(path)) {
                    Files.createFile(path);
                }

                //Write NBT to file
                FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
                NbtIo.writeCompressed(data.serializeNBT(), fileOutputStream);
                fileOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
