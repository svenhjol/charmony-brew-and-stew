package svenhjol.charmony.brew_and_stew.common.features.cooking_pots;

import net.minecraft.util.StringRepresentable;

public enum CookingStatus implements StringRepresentable {
    Empty("empty"),
    HasSomeWater("has_some_water"),
    FilledWithWater("filled_with_water"),
    HasSomeFood("has_some_food"),
    Cooked("cooked");

    private final String name;

    CookingStatus(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
