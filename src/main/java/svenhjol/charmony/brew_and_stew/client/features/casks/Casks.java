package svenhjol.charmony.brew_and_stew.client.features.casks;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

import java.util.function.Supplier;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
public final class Casks extends SidedFeature {
    public final Supplier<Common> common;
    public final Handlers handlers;
    public final Registers registers;

    public Casks(Mod mod) {
        super(mod);
        common = Common::new;
        handlers = new Handlers(this);
        registers = new Registers(this);
    }
}
