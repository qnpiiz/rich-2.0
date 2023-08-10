package fun.rich.feature.impl;

import lombok.Getter;

@Getter
public enum FeatureCategory {

    Combat("Combat"),
    Movement("Movement"),
    Visuals("Visual"),
    Player("Player"),
    Misc("Misc"),
    Hud("Hud");

    private final String displayName;

    FeatureCategory(String displayName) {
        this.displayName = displayName;
    }
}
