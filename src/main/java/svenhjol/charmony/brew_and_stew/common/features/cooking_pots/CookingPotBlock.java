package svenhjol.charmony.brew_and_stew.common.features.cooking_pots;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CookingPotBlock extends BaseEntityBlock {
    public static IntegerProperty PORTIONS = IntegerProperty.create("portions", 0,
        CookingPots.feature().handlers.maxPortions());
    public static EnumProperty<CookingStatus> COOKING_STATUS = EnumProperty.create("cooking_status", CookingStatus.class);

    private static final MapCodec<CookingPotBlock> CODEC = simpleCodec(CookingPotBlock::new);
    private static final VoxelShape RAY_TRACE_SHAPE;
    private static final VoxelShape OUTLINE_SHAPE;

    public CookingPotBlock(ResourceKey<Block> key) {
        this(Properties.of()
            .requiresCorrectToolForDrops()
            .strength(2.0f)
            .mapColor(MapColor.COLOR_ORANGE)
            .sound(SoundType.COPPER)
            .noOcclusion()
            .setId(key));

        registerDefaultState(defaultBlockState()
            .setValue(PORTIONS, 0)
            .setValue(COOKING_STATUS, CookingStatus.Empty));
    }

    private CookingPotBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player,
                                          InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return CookingPots.feature().handlers.playerAddToPot(itemStack, blockState, level, blockPos, player, interactionHand);
    }

    @Override
    public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        if (precipitation == Biome.Precipitation.RAIN && level.getRandom().nextFloat() < 0.05f) {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CookingPotBlockEntity pot) {
                pot.fillTwoLevelsOfWater();
            }
        }
    }

    @Nullable
    @Override
    public CookingPotBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CookingPotBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return RAY_TRACE_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PORTIONS, COOKING_STATUS);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        int portions = state.getValue(PORTIONS);

        if (!(level.getBlockEntity(pos) instanceof CookingPotBlockEntity pot)) {
            return 0;
        }

        if (!pot.hasFinishedCooking()) {
            return 0;
        }

        return portions;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        return super.playerWillDestroy(level, blockPos, CookingPots.feature().registers.block.get().defaultBlockState(), player);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier) {
        if (level.getBlockEntity(pos) instanceof CookingPotBlockEntity pot) {
            CookingPots.feature().handlers.entityInside(level, pos, state, entity, pot);
            return;
        }
        super.entityInside(state, level, pos, entity, insideBlockEffectApplier);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (level.getBlockEntity(pos) instanceof CookingPotBlockEntity pot) {
            if (pot.hasFire() && !pot.isEmpty()) {
                int portions = state.getValue(PORTIONS);
                var status = state.getValue(COOKING_STATUS);

                if (status == CookingStatus.FilledWithWater) {
                    // When heated water, show white smoke and bubbles
                    var count = random.nextInt(5);
                    for (int i = 0; i < count; i++) {
                        level.addParticle(ParticleTypes.WHITE_SMOKE,
                            pos.getX() + 0.13d + (0.7d * random.nextDouble()),
                            pos.getY() + (portions - 2) * 0.153d,
                            pos.getZ() + 0.13d + (0.7d * random.nextDouble()),
                            0.0d, 0.0d, 0.0d);
                    }
                    level.addParticle(ParticleTypes.BUBBLE,
                        pos.getX() + 0.13d + (0.7d * random.nextDouble()),
                        pos.getY() + (portions - 2) * 0.153d,
                        pos.getZ() + 0.13d + (0.7d * random.nextDouble()),
                        0.3d, 0.3d, 0.3d);

                } else if (status == CookingStatus.HasSomeFood) {
                    // When in progress, show black smoke.
                    var count = random.nextInt(5);
                    for (int i = 0; i < count; i++) {
                        level.addParticle(ParticleTypes.SMOKE,
                            pos.getX() + 0.13d + (0.7d * random.nextDouble()),
                            pos.getY() + (portions - 2) * 0.153d,
                            pos.getZ() + 0.13d + (0.7d * random.nextDouble()),
                            0.0d, 0.0d, 0.0d);
                    }
                } else if (status == CookingStatus.Cooked) {
                    var count = random.nextInt(5);
                    for (int i = 0; i < count; i++) {
                        level.addParticle(ParticleTypes.SMOKE,
                            pos.getX() + 0.13d + (0.7d * random.nextDouble()),
                            pos.getY() + (portions - 2) * 0.153d,
                            pos.getZ() + 0.13d + (0.7d * random.nextDouble()),
                            0.0d, 0.0d, 0.0d);
                    }
                    
                    count = random.nextInt(3);
                    for (int i = 0; i < count; i++) {
                        level.addParticle(ParticleTypes.DUST_PLUME,
                            pos.getX() + 0.13d + (0.7d * random.nextDouble()),
                            pos.getY() + (portions - 2) * 0.153d,
                            pos.getZ() + 0.13d + (0.7d * random.nextDouble()),
                            0.0d, 0.0d, 0.0d);
                    }
                }

                if (random.nextInt(16) == 0) {
                    level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(),
                        CookingPots.feature().registers.ambientSound.get(),
                        SoundSource.BLOCKS,
                        0.15f + (0.15f * random.nextFloat()),
                        random.nextFloat() * 0.7f + 0.6f, false);
                }
            }
        }
    }

    public static class CookingPotBlockItem extends BlockItem {
        public CookingPotBlockItem(Supplier<CookingPotBlock> block, ResourceKey<Item> key) {
            super(block.get(), new Properties().setId(key));
        }
    }

    static {
        RAY_TRACE_SHAPE = box(2.0d, 4.0d, 2.0d, 14.0d, 16.0d, 14.0d);
        OUTLINE_SHAPE = Shapes.join(Shapes.block(),
            Shapes.or(
                box(0.0d, 0.0d, 4.0d, 16.0d, 3.0d, 12.0d),
                box(4.0d, 0.0d, 0.0d, 12.0d, 3.0d, 16.0d),
                box(2.0d, 0.0d, 2.0d, 14.0d, 3.0d, 14.0d), RAY_TRACE_SHAPE),
            BooleanOp.ONLY_FIRST);
    }
}
