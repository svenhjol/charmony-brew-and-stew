package svenhjol.charmony.brewing.client.features.cooking_pots;

import svenhjol.charmony.brewing.common.features.cooking_pots.CookingPots;
import svenhjol.charmony.brewing.common.features.cooking_pots.Registers;

public final class Common {
    public final Registers registers;

    public Common() {
        var feature = CookingPots.feature();
        registers = feature.registers;
    }
}
