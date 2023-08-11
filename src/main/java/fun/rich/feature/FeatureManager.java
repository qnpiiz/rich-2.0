package fun.rich.feature;

import com.google.common.collect.Lists;
import fun.rich.feature.impl.FeatureCategory;
import fun.rich.feature.impl.hud.ClickGUI;
import fun.rich.feature.impl.hud.FeatureList;
import fun.rich.feature.impl.hud.Hud;
import fun.rich.feature.impl.hud.Notifications;
import fun.rich.feature.impl.misc.MiddleClickPearl;
import fun.rich.feature.impl.movement.GuiWalk;
import fun.rich.feature.impl.movement.NoSlowDown;
import fun.rich.feature.impl.movement.Sprint;
import fun.rich.feature.impl.movement.WaterSpeed;
import fun.rich.feature.impl.player.DeathCoordinates;
import fun.rich.feature.impl.player.NoServerRotations;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FeatureManager {

    public CopyOnWriteArrayList<Feature> features;

    public FeatureManager() {
        features = new CopyOnWriteArrayList<>();
        features.add(new ClickGUI());
        features.add(new Sprint());
        features.add(new WaterSpeed());
        features.add(new NoSlowDown());
        features.add(new MiddleClickPearl());
        features.add(new NoServerRotations());
        features.add(new GuiWalk());
        features.add(new DeathCoordinates());
        features.add(new Notifications());
        features.add(new Hud());
        features.add(new FeatureList());
    }

    public List<Feature> getAllFeatures() {
        return this.features;
    }

    public List<Feature> getFeaturesCategory(FeatureCategory category) {
        List<Feature> features = Lists.newArrayList();
        for (Feature feature : getAllFeatures())
            if (feature.getCategory() == category)
                features.add(feature);

        return features;
    }

    public Feature getFeature(Class<? extends Feature> classFeature) {
        for (Feature feature : getAllFeatures()) {
            if (feature != null) {
                if (feature.getClass() == classFeature)
                    return feature;
            }
        }

        return null;
    }

    public Feature getFeature(String name) {
        for (Feature feature : getAllFeatures())
            if (feature.getLabel().equals(name))
                return feature;

        return null;
    }

    public List<Feature> getFeatureList() {
        return this.features;
    }
}
