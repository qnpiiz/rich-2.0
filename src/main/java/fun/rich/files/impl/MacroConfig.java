package fun.rich.files.impl;

import fun.rich.Rich;
import fun.rich.files.FileManager;
import fun.rich.macro.Macro;
import net.minecraft.client.util.InputMappings;

import java.io.*;

public class MacroConfig extends FileManager.CustomFile {

    public MacroConfig(String name, boolean loadOnStart) {
        super(name, loadOnStart);
    }

    public void loadFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream(this.getFile().getAbsolutePath());
            DataInputStream in = new DataInputStream(fileInputStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                String curLine = line.trim();
                String bind = curLine.split(":")[0];
                String value = curLine.split(":")[1];
                if (Rich.instance.macroManager != null)
                    Rich.instance.macroManager.addMacro(new Macro(bind, Integer.parseInt(value)));
            }
            br.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void saveFile() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(this.getFile()));
            for (Macro m : Rich.instance.macroManager.getMacros()) {
                if (m != null) {
                    out.write(InputMappings.getInputByCode(m.getKey(), 0).getTranslationKey() + ":" + m.getValue());
                    out.write("\r\n");
                }
            }
            out.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
