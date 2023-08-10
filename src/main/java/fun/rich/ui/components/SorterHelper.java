package fun.rich.ui.components;

import fun.rich.ui.clickgui.component.Component;
import fun.rich.ui.clickgui.component.impl.ModuleComponent;

import java.util.Comparator;

public class SorterHelper implements Comparator<Component> {

    @Override
    public int compare(Component component, Component component2) {
        if (component instanceof ModuleComponent && component2 instanceof ModuleComponent)
            return component.getName().compareTo(component2.getName());

        return 0;
    }
}
