package fun.rich.feature.impl.player;

import fun.rich.feature.impl.FeatureCategory;
import fun.rich.feature.Feature;

public class DeathCoordinates extends Feature {

    public DeathCoordinates() {
        // Прописывается в DeathScreen#init
        super("DeathCoordinates", FeatureCategory.Player);
    }
}
