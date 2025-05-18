package svenhjol.charmony.cooking;

import net.minecraft.resources.ResourceLocation;
import svenhjol.charmony.api.core.ModDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.api.core.Side;

@ModDefinition(
    id = CookingMod.ID,
    sides = {Side.Client, Side.Common},
    name = "Cooking",
    description = "Adds a cooking pot that can be used to combine different foods into a single stew.")
public final class CookingMod extends Mod {
    public static final String ID = "charmony-cooking";
    private static CookingMod instance;

    private CookingMod() {}

    public static CookingMod instance() {
        if (instance == null) {
            instance = new CookingMod();
        }
        return instance;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}