package fun.rich.files;

import com.google.common.collect.Lists;
import fun.rich.files.impl.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class FileManager {

    private static final File directory = new File("C:\\RichClient\\game\\configs");

    public static List<CustomFile> files = Lists.newLinkedList();

    public FileManager() {
        files.add(new FriendConfig("FriendConfig", true));
        files.add(new MacroConfig("MacroConfig", true));
        files.add(new HudConfig("HudConfig", true));
    }

    public void loadFiles() {
        for (CustomFile file : files) {
            try {
                if (file.loadOnStart())
                    file.loadFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFiles() {
        for (CustomFile f : files) {
            try {
                f.saveFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CustomFile getFile(Class<?> clazz) {
        CustomFile file;
        Iterator<CustomFile> customFileIterator = files.iterator();
        do {
            if (!customFileIterator.hasNext())
                return null;
            file = customFileIterator.next();
        } while (file.getClass() != clazz);

        return file;
    }

    public static abstract class CustomFile {

        private final File file;
        private final String name;
        private final boolean load;

        public CustomFile(String name, boolean loadOnStart) {
            this.name = name;
            this.load = loadOnStart;
            this.file = new File(FileManager.directory, name + ".json");
            if (!this.file.exists())
                try {
                    saveFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        public final File getFile() {
            return this.file;
        }

        private boolean loadOnStart() {
            return this.load;
        }

        public final String getName() {
            return this.name;
        }

        public abstract void loadFile() throws IOException;

        public abstract void saveFile() throws IOException;
    }
}
