package svenhjol.charmony.brew_and_stew;

import net.minecraft.resources.ResourceLocation;
import svenhjol.charmony.api.core.ModDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.api.core.Side;

@ModDefinition(
    id = BrewAndStewMod.ID,
    sides = {Side.Client, Side.Common},
    name = "Brew And Stew",
    description = "Adds a cooking pot that can be used to combine different foods into a single stew.")
public final class BrewAndStewMod extends Mod {
    public static final String ID = "charmony-brew-and-stew";
    private static BrewAndStewMod instance;

    private BrewAndStewMod() {}

    public static BrewAndStewMod instance() {
        if (instance == null) {
            instance = new BrewAndStewMod();
        }
        return instance;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}