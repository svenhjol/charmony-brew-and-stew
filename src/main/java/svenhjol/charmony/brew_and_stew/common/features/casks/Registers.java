package svenhjol.charmony.brew_and_stew.common.features.casks;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.brew_and_stew.common.features.casks.Networking.S2CAddedToCask;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;

import java.util.List;
import java.util.function.Supplier;

public class Registers extends Setup<Casks> {
    static final String BLOCK_ID = "cask";
    public final Supplier<CaskBlock> block;
    public final Supplier<CaskBlock.CaskBlockItem> blockItem;
    public final Supplier<BlockEntityType<CaskBlockEntity>> blockEntity;
    public final Supplier<DataComponentType<CaskData>> caskData;
    public final Supplier<DataComponentType<HomeBrewData>> homeBrewData;
    public final Supplier<SoundEvent> addSound;
    public final Supplier<SoundEvent> emptySound;
    public final Supplier<SoundEvent> nameSound;
    public final Supplier<SoundEvent> takeSound;

    public Registers(Casks feature) {
        super(feature);
        var registry = CommonRegistry.forFeature(feature);

        block = registry.block(BLOCK_ID, CaskBlock::new);
        blockItem = registry.item(BLOCK_ID, key -> new CaskBlock.CaskBlockItem(block, key));
        blockEntity = registry.blockEntity(BLOCK_ID, () -> CaskBlockEntity::new, List.of(block));
        
        // Cask data component.
        caskData = registry.dataComponent("cask",
            () -> builder -> builder
                .persistent(CaskData.CODEC)
                .networkSynchronized(CaskData.STREAM_CODEC));

        // Home brew data component.
        homeBrewData = registry.dataComponent("home_brew",
            () -> builder -> builder
                .persistent(HomeBrewData.CODEC)
                .networkSynchronized(HomeBrewData.STREAM_CODEC));

        // Casks can be burned for fuel
        registry.fuel(block);

        registry.packetSender(Side.Common, S2CAddedToCask.TYPE, S2CAddedToCask.CODEC);

        // Sounds
        addSound = registry.sound("cask_add");
        emptySound = registry.sound("cask_empty");
        nameSound = registry.sound("cask_name");
        takeSound = registry.sound("cask_take");
    }
}
