package svenhjol.charmony.brewing.client.features.cooking_pots;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

import java.util.function.Supplier;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
public final class CookingPots extends SidedFeature {
    public final Supplier<Common> common;
    public final Registers registers;
    public final Handlers handlers;

    public CookingPots(Mod mod) {
        super(mod);
        common = Common::new;
        handlers = new Handlers(this);
        registers = new Registers(this);
    }
}
