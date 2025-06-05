package svenhjol.charmony.brew_and_stew.client.features.casks;

import svenhjol.charmony.brew_and_stew.common.features.casks.Casks;
import svenhjol.charmony.brew_and_stew.common.features.casks.Registers;

public class Common {
    public final Registers registers;

    public Common() {
        var feature = Casks.feature();
        registers = feature.registers;
    }
}
