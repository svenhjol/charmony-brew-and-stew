package svenhjol.charmony.brew_and_stew.common.features.casks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import svenhjol.charmony.api.core.Color;
import svenhjol.charmony.api.core.FuelProvider;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CaskBlock extends BaseEntityBlock implements FuelProvider {
    private static final MapCodec<CaskBlock> CODEC = simpleCodec(CaskBlock::new);

    private static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    private static final VoxelShape X1, X2, X3, X4;
    private static final VoxelShape Y1, Y2, Y3, Y4;
    private static final VoxelShape Z1, Z2, Z3, Z4;
    private static final VoxelShape X_SHAPE;
    private static final VoxelShape Y_SHAPE;
    private static final VoxelShape Z_SHAPE;
    private static final int FUEL_TIME = 300;

    public CaskBlock(ResourceKey<Block> key) {
        this(Properties.of()
            .strength(2.5f)
            .sound(SoundType.WOOD)
            .setId(key));

        registerDefaultState(defaultBlockState()
            .setValue(FACING, Direction.NORTH));
    }

    private CaskBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public InteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player,
                                       InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return Casks.feature().handlers.playerAddToCask(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof CaskBlockEntity cask) {
            if (stack.has(DataComponents.CUSTOM_NAME)) {
                cask.name = stack.get(DataComponents.CUSTOM_NAME);
            }
        }

        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CaskBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    @Override
    public int fuelTime() {
        return FUEL_TIME;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof CaskBlockEntity cask && cask.bottles > 0) {
            return Mth.lerpDiscrete((float)cask.bottles / (float) Casks.feature().maxBottles(), 0, 15);
        }
        return 0;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch ((state.getValue(CaskBlock.FACING)).getAxis()) {
            default -> X_SHAPE;
            case Z -> Z_SHAPE;
            case Y -> Y_SHAPE;
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch ((state.getValue(CaskBlock.FACING)).getAxis()) {
            default -> X_SHAPE;
            case Z -> Z_SHAPE;
            case Y -> Y_SHAPE;
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextDouble() < 0.7d) {
            var blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof CaskBlockEntity cask && cask.bottles > 0) {
                var effects = cask.effects
                    .stream()
                    .map(BuiltInRegistries.MOB_EFFECT::getValue)
                    .toList();

                if (effects.isEmpty()) {
                    // There's only water in the cask.
                    createWaterParticle(level, pos);
                } else {
                    // Create particles for each effect color
                    effects.forEach(effect -> createEffectParticle(level, pos, effect));
                }
            }
        }
    }

    void createWaterParticle(Level level, BlockPos pos) {
        var random = level.getRandom();

        level.addParticle(ParticleTypes.DRIPPING_WATER,
            pos.getX() + random.nextDouble(),
            pos.getY() + 0.7d,
            pos.getZ() + random.nextDouble(),
            0.0d, 0.0d, 0.0d);
    }

    void createEffectParticle(Level level, BlockPos pos, MobEffect effect) {
        var random = level.getRandom();
        var color = new Color(effect.getColor());

//        var r = (color >> 16 & 255) / 255.0f;
//        var g = (color >> 8 & 255) / 255.0f;
//        var b = (color & 255) / 255.0f;

        var p = ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color.getArgbColor());

        level.addParticle(p,
            pos.getX() + 0.13d + (0.7d * random.nextDouble()),
            pos.getY() + 0.5d,
            pos.getZ() + 0.13d + (0.7d * random.nextDouble()),
            -1, -1, -1);
    }

    static {
        X1 = Block.box(1.0d, 0.0d, 4.0d, 15.0d, 16.0d, 12.0d);
        X2 = Block.box(1.0d, 1.0d, 2.0d, 15.0d, 15.0d, 14.0d);
        X3 = Block.box(1.0d, 2.0d, 1.0d, 15.0d, 14.0d, 15.0d);
        X4 = Block.box(1.0d, 4.0d, 0.0d, 15.0d, 12.0d, 16.0d);
        Y1 = Block.box(4.0d, 1.0d, 0.0d, 12.0d, 15.0d, 16.0d);
        Y2 = Block.box(2.0d, 1.0d, 1.0d, 14.0d, 15.0d, 15.0d);
        Y3 = Block.box(1.0d, 1.0d, 2.0d, 15.0d, 15.0d, 14.0d);
        Y4 = Block.box(0.0d, 1.0d, 4.0d, 16.0d, 15.0d, 12.0d);
        Z1 = Block.box(4.0d, 0.0d, 1.0d, 12.0d, 16.0d, 15.0d);
        Z2 = Block.box(2.0d, 1.0d, 1.0d, 14.0d, 15.0d, 15.0d);
        Z3 = Block.box(1.0d, 2.0d, 1.0d, 15.0d, 14.0d, 15.0d);
        Z4 = Block.box(0.0d, 4.0d, 1.0d, 16.0d, 12.0d, 15.0d);
        X_SHAPE = Shapes.or(X1, X2, X3, X4);
        Y_SHAPE = Shapes.or(Y1, Y2, Y3, Y4);
        Z_SHAPE = Shapes.or(Z1, Z2, Z3, Z4);
    }

    public static class CaskBlockItem extends BlockItem {
        public CaskBlockItem(Supplier<CaskBlock> block, ResourceKey<Item> key) {
            super(block.get(), new Properties().setId(key));
        }
    }
}
