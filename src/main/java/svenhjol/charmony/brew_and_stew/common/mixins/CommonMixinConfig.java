package svenhjol.charmony.brew_and_stew.common.mixins;

import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.brew_and_stew.BrewAndStewMod;
import svenhjol.charmony.core.base.MixinConfig;

public class CommonMixinConfig extends MixinConfig {
    @Override
    protected String modId() {
        return BrewAndStewMod.ID;
    }

    @Override
    protected String modRoot() {
        return "svenhjol.charmony.brew_and_stew";
    }

    @Override
    protected Side side() {
        return Side.Common;
    }
}
