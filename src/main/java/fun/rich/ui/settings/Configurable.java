package fun.rich.ui.settings;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class Configurable {

    private final List<Setting> settings = Lists.newLinkedList();

    public void addSettings(Setting... options) {
        this.settings.addAll(Arrays.asList(options));
    }
}
