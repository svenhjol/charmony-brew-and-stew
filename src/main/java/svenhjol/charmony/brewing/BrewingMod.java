package svenhjol.charmony.brewing;

import net.minecraft.resources.ResourceLocation;
import svenhjol.charmony.api.core.ModDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.api.core.Side;

@ModDefinition(
    id = BrewingMod.ID,
    sides = {Side.Client, Side.Common},
    name = "Brewing",
    description = "Adds a cooking pot that can be used to combine different foods into a single stew.")
public final class BrewingMod extends Mod {
    public static final String ID = "charmony-brewing";
    private static BrewingMod instance;

    private BrewingMod() {}

    public static BrewingMod instance() {
        if (instance == null) {
            instance = new BrewingMod();
        }
        return instance;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}