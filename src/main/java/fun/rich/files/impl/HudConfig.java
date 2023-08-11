package fun.rich.files.impl;

import fun.rich.Rich;
import fun.rich.draggable.component.DraggableComponent;
import fun.rich.files.FileManager;

import java.io.*;

public class HudConfig extends FileManager.CustomFile {

    public HudConfig(String name, boolean loadOnStart) {
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
                String x = curLine.split(":")[1];
                String y = curLine.split(":")[2];
                for (DraggableComponent hudModule : Rich.instance.draggableHUD.getComponents()) {
                    if (hudModule.getName().equals(curLine.split(":")[0])) {
                        hudModule.setX(Integer.parseInt(x));
                        hudModule.setY(Integer.parseInt(y));
                    }
                }
            }
            br.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void saveFile() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(this.getFile()));
            for (DraggableComponent draggableModule : Rich.instance.draggableHUD.getComponents()) {
                out.write(draggableModule.getName() + ":" + draggableModule.getX() + ":" + draggableModule.getY());
                out.write("\r\n");
            }
            out.close();
        } catch (Exception ignored) {

        }
    }
}
