package xyz.przemyk.gutech.blocks.cable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import xyz.przemyk.gutech.setup.ModBlocks;

import javax.annotation.Nullable;
import java.util.Map;

public class ElectricCableBlock extends Block {

    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.REDSTONE_NORTH;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.REDSTONE_EAST;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.REDSTONE_SOUTH;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.REDSTONE_WEST;
    public static final Map<Direction, EnumProperty<RedstoneSide>> FACING_PROPERTY_MAP = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));

    protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};

    public ElectricCableBlock() {
        super(Properties.from(Blocks.REDSTONE_WIRE));
        this.setDefaultState(this.stateContainer.getBaseState()
                .with(NORTH, RedstoneSide.NONE)
                .with(EAST, RedstoneSide.NONE)
                .with(SOUTH, RedstoneSide.NONE)
                .with(WEST, RedstoneSide.NONE));
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[getAABBIndex(state)];
    }

    private static int getAABBIndex(BlockState state) {
        int i = 0;
        boolean flag = state.get(NORTH) != RedstoneSide.NONE;
        boolean flag1 = state.get(EAST) != RedstoneSide.NONE;
        boolean flag2 = state.get(SOUTH) != RedstoneSide.NONE;
        boolean flag3 = state.get(WEST) != RedstoneSide.NONE;
        if (flag || flag2 && !flag && !flag1 && !flag3) {
            i |= 1 << Direction.NORTH.getHorizontalIndex();
        }

        if (flag1 || flag3 && !flag && !flag1 && !flag2) {
            i |= 1 << Direction.EAST.getHorizontalIndex();
        }

        if (flag2 || flag && !flag1 && !flag2 && !flag3) {
            i |= 1 << Direction.SOUTH.getHorizontalIndex();
        }

        if (flag3 || flag1 && !flag && !flag2 && !flag3) {
            i |= 1 << Direction.WEST.getHorizontalIndex();
        }

        return i;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IBlockReader iblockreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        return this.getDefaultState().with(WEST, this.getSide(iblockreader, blockpos, Direction.WEST)).with(EAST, this.getSide(iblockreader, blockpos, Direction.EAST)).with(NORTH, this.getSide(iblockreader, blockpos, Direction.NORTH)).with(SOUTH, this.getSide(iblockreader, blockpos, Direction.SOUTH));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.DOWN) {
            return stateIn;
        } else {
            return facing == Direction.UP ? stateIn.with(WEST, this.getSide(worldIn, currentPos, Direction.WEST)).with(EAST, this.getSide(worldIn, currentPos, Direction.EAST)).with(NORTH, this.getSide(worldIn, currentPos, Direction.NORTH)).with(SOUTH, this.getSide(worldIn, currentPos, Direction.SOUTH)) : stateIn.with(FACING_PROPERTY_MAP.get(facing), this.getSide(worldIn, currentPos, facing));
        }
    }

    private RedstoneSide getSide(IBlockReader worldIn, BlockPos pos, Direction face) {
        BlockPos blockpos = pos.offset(face);
        BlockState blockstate = worldIn.getBlockState(blockpos);
        BlockPos blockPosUp = pos.up();
        BlockState blockStateUp = worldIn.getBlockState(blockPosUp);
        if (!blockStateUp.isNormalCube(worldIn, blockPosUp)) {
            boolean flag = blockstate.isSolidSide(worldIn, blockpos, Direction.UP);
            if (flag && canConnectTo(worldIn, blockpos.up(), null)) {
                if (blockstate.isCollisionShapeOpaque(worldIn, blockpos)) {
                    return RedstoneSide.UP;
                }

                return RedstoneSide.SIDE;
            }
        }

        return !canConnectTo(worldIn, blockpos, face) && (blockstate.isNormalCube(worldIn, blockpos) || !canConnectTo(worldIn, blockpos.down(), null)) ? RedstoneSide.NONE : RedstoneSide.SIDE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isRemote() && !isValidPosition(state, worldIn, pos)) {
            spawnDrops(state, worldIn, pos);
            worldIn.removeBlock(pos, false);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return blockstate.isSolidSide(worldIn, blockpos, Direction.UP);
    }

    protected static boolean canConnectTo(IBlockReader world, BlockPos pos, @Nullable Direction side) {
        if (world.getBlockState(pos).getBlock() == ModBlocks.ELECTRIC_CABLE.get()) { //TODO: remove this
            return true;
        }
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) {
            return false;
        }
        return tileEntity.getCapability(CapabilityEnergy.ENERGY, side).isPresent();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void updateDiagonalNeighbors(BlockState state, IWorld worldIn, BlockPos pos, int flags) {
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.retain()) {
            for(Direction direction : Direction.Plane.HORIZONTAL) {
                RedstoneSide redstoneside = state.get(FACING_PROPERTY_MAP.get(direction));
                if (redstoneside != RedstoneSide.NONE && worldIn.getBlockState(pooledMutable.setPos(pos).move(direction)).getBlock() != this) {
                    pooledMutable.move(Direction.DOWN);
                    BlockState blockstate = worldIn.getBlockState(pooledMutable);
                    if (blockstate.getBlock() != Blocks.OBSERVER) {
                        BlockPos blockpos = pooledMutable.offset(direction.getOpposite());
                        BlockState blockstate1 = blockstate.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos), worldIn, pooledMutable, blockpos);
                        replaceBlock(blockstate, blockstate1, worldIn, pooledMutable, flags);
                    }

                    pooledMutable.setPos(pos).move(direction).move(Direction.UP);
                    BlockState blockstate3 = worldIn.getBlockState(pooledMutable);
                    if (blockstate3.getBlock() != Blocks.OBSERVER) {
                        BlockPos blockpos1 = pooledMutable.offset(direction.getOpposite());
                        BlockState blockstate2 = blockstate3.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos1), worldIn, pooledMutable, blockpos1);
                        replaceBlock(blockstate3, blockstate2, worldIn, pooledMutable, flags);
                    }
                }
            }
        }

    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ElectricCableTileEntity();
    }
}
