package svenhjol.charmony.brew_and_stew.common.features.elixirs;

import svenhjol.charmony.api.Api;
import svenhjol.charmony.api.elixirs.ElixirDefinition;
import svenhjol.charmony.api.elixirs.ElixirDefinitionProvider;
import svenhjol.charmony.brew_and_stew.common.features.elixirs.items.CommonElixir;
import svenhjol.charmony.core.base.Setup;

import java.util.List;

public class Providers extends Setup<Elixirs> implements ElixirDefinitionProvider {
    public Providers(Elixirs feature) {
        super(feature);
        Api.registerProvider(this);
    }

    @Override
    public List<ElixirDefinition> getElixirDefinitions() {
        return List.of(
            new CommonElixir()
        );
    }
}
