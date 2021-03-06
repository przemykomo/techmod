package xyz.przemyk.gutech.modules.cables;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;

public class ElectricCableBlock extends Block {

    public final int maxTransfer;

    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.REDSTONE_NORTH;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.REDSTONE_EAST;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.REDSTONE_SOUTH;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.REDSTONE_WEST;
    public static final Map<Direction, EnumProperty<RedstoneSide>> FACING_PROPERTY_MAP = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));

    protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};

    public ElectricCableBlock(int maxTransfer) {
        super(Properties.from(Blocks.REDSTONE_WIRE));
        this.setDefaultState(this.stateContainer.getBaseState()
                .with(NORTH, RedstoneSide.NONE)
                .with(EAST, RedstoneSide.NONE)
                .with(SOUTH, RedstoneSide.NONE)
                .with(WEST, RedstoneSide.NONE));
        this.maxTransfer = maxTransfer;
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

    protected boolean canConnectTo(IBlockReader world, BlockPos pos, @Nullable Direction side) {
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
                    BlockPos blockpos = pooledMutable.offset(direction.getOpposite());
                    BlockState blockstate1 = blockstate.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos), worldIn, pooledMutable, blockpos);
                    replaceBlock(blockstate, blockstate1, worldIn, pooledMutable, flags);

                    pooledMutable.setPos(pos).move(direction).move(Direction.UP);
                    BlockState blockstate3 = worldIn.getBlockState(pooledMutable);
                    BlockPos blockpos1 = pooledMutable.offset(direction.getOpposite());
                    BlockState blockstate2 = blockstate3.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos1), worldIn, pooledMutable, blockpos1);
                    replaceBlock(blockstate3, blockstate2, worldIn, pooledMutable, flags);
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

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.DOWN || facing == Direction.UP) {
            return stateIn;
        } else {
            if (!worldIn.isRemote()) {
                TileEntity neighborTileEntity = worldIn.getTileEntity(facingPos);
                TileEntity cableTileEntity = worldIn.getTileEntity(currentPos);
                if (neighborTileEntity != null && cableTileEntity instanceof ElectricCableTileEntity && !(neighborTileEntity instanceof  ElectricCableTileEntity)) {
                    cableTileEntity.getCapability(CapabilityEnergy.ENERGY, facing).ifPresent(energy -> {
                        ((ElectricCableEnergyStorage) energy).machinesPos.add(new Pair<>(facingPos, facing.getOpposite()));
                        ((ElectricCableEnergyStorage) energy).getCachedMachines().add(new ElectricCableEnergyStorage.MachineEntry(neighborTileEntity, facing.getOpposite()));
                    });
                }
            }
            return stateIn.with(FACING_PROPERTY_MAP.get(facing), getSide(worldIn, currentPos, facing));
//            return facing == Direction.UP ? stateIn.with(WEST, this.getSide(worldIn, currentPos, Direction.WEST)).with(EAST, this.getSide(worldIn, currentPos, Direction.EAST)).with(NORTH, this.getSide(worldIn, currentPos, Direction.NORTH)).with(SOUTH, this.getSide(worldIn, currentPos, Direction.SOUTH)) : stateIn.with(FACING_PROPERTY_MAP.get(facing), this.getSide(worldIn, currentPos, facing));
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof ElectricCableTileEntity) {
                ElectricCableTileEntity myCableTileEntity = (ElectricCableTileEntity) tileEntity;
                TileEntityType<?> myType = myCableTileEntity.getType();
                ArrayList<ElectricCableTileEntity> connectedCables = new ArrayList<>();

                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    RedstoneSide redstoneSide = state.get(FACING_PROPERTY_MAP.get(direction));
                    switch (redstoneSide) {
                        case NONE:
                            continue;
                        case SIDE:
                            TileEntity other = worldIn.getTileEntity(pos.offset(direction));
                            if (other.getType() == myType) {
                                connectedCables.add((ElectricCableTileEntity) other);
                            } else {
                                other = worldIn.getTileEntity(pos.offset(direction).down());
                                if (other.getType() == myType) {
                                    connectedCables.add((ElectricCableTileEntity) other);
                                }
                            }
                            break;
                        case UP:
                            TileEntity otherUp = worldIn.getTileEntity(pos.offset(direction).up());
                            if (otherUp.getType() == myType) {
                                connectedCables.add((ElectricCableTileEntity) otherUp);
                            }
                            break;
                    }
                }

                CableNetworksData cableNetworksData = CableNetworksData.get((ServerWorld) worldIn);
                if (connectedCables.size() == 1) {
                    cableNetworksData.networks.get(connectedCables.get(0).networkID).cables.add(pos);
                    myCableTileEntity.networkID = connectedCables.get(0).networkID;
                } else if (connectedCables.size() > 1) {
                    int minID = Integer.MAX_VALUE;
                    for (ElectricCableTileEntity otherCableTileEntity : connectedCables) {
                        if (otherCableTileEntity.networkID < minID) {
                            minID = otherCableTileEntity.networkID;
                        }
                    }

                    CableNetwork cableNetwork = cableNetworksData.networks.get(minID);
                    for (ElectricCableTileEntity otherCableTileEntity : connectedCables) {
                        cableNetworksData.networks.get(otherCableTileEntity.networkID).mergeInto(cableNetwork, (ServerWorld) worldIn).cables.add(pos);
                        myCableTileEntity.networkID = minID;
                    }
                } else {
                    CableNetwork cableNetwork = cableNetworksData.createNetwork();
                    cableNetwork.cables.add(pos);
                    myCableTileEntity.networkID = cableNetwork.id;
                }
            }
        }
    }

    /*
    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        if (!world.isRemote()) {
            TileEntity tileEntity = world.getTileEntity(pos);
            TileEntity neighborTileEntity = world.getTileEntity(neighbor);
            if (tileEntity instanceof ElectricCableTileEntity && neighborTileEntity != null
                    && tileEntity.getType() != neighborTileEntity.getType()) {
                Vec3i relative = neighbor.subtract(pos);
                Direction direction = Direction.getFacingFromVector(relative.getX(), relative.getY(), relative.getZ());
                neighborTileEntity.getCapability(CapabilityEnergy.ENERGY, direction).ifPresent(energy -> {
                            tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(myEnergy -> {
                                ((ElectricCableEnergyStorage) myEnergy).machinesPos.add(new Pair<>(neighbor, direction));
                                ((ElectricCableEnergyStorage) myEnergy).getCachedMachines().add(new ElectricCableEnergyStorage.MachineEntry(neighborTileEntity, direction));
                            });
                });
            }
        }
    }
     */
}
