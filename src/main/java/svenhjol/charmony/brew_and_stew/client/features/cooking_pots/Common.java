package svenhjol.charmony.brew_and_stew.client.features.cooking_pots;

import svenhjol.charmony.brew_and_stew.common.features.cooking_pots.CookingPots;
import svenhjol.charmony.brew_and_stew.common.features.cooking_pots.Registers;

public final class Common {
    public final Registers registers;

    public Common() {
        var feature = CookingPots.feature();
        registers = feature.registers;
    }
}
