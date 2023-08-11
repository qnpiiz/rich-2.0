package fun.rich.macro;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MacroManager {

    public List<Macro> macros = new ArrayList<>();

    public void addMacro(Macro macro) {
        this.macros.add(macro);
    }

    public void deleteMacroByKey(int key) {
        this.macros.removeIf(macro -> macro.getKey() == key);
    }
}
