package com.nekokittygames.thaumictinkerer_funnel.common.blocks;

import com.nekokittygames.thaumictinkerer_funnel.thaumictinkerer_funnel;
import com.nekokittygames.thaumictinkerer_funnel.common.libs.LibBlockNames;
import com.nekokittygames.thaumictinkerer_funnel.common.libs.LibMisc;
import com.nekokittygames.thaumictinkerer_funnel.common.tileentity.TileEntityFunnel;
import com.nekokittygames.thaumictinkerer_funnel.common.tileentity.TileEntityFunnel.JarAspect;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.aspects.IEssentiaContainerItem;

public class BlockFunnel extends TTTileEntity<TileEntityFunnel> {

    public static final PropertyBool JAR = PropertyBool.create("jar");
    public ResourceLocation resourceLocation;


    public BlockFunnel() {
        super(LibBlockNames.FUNNEL, Material.ROCK, true);
        setHardness(3.0F);
        setResistance(8.0f);
        setDefaultState(this.getBlockState().getBaseState().withProperty(JAR, false));
        setTickRandomly(true);
       
        //this.setCreativeTab(thaumictinkerer_funnel.getTab());
		resourceLocation= new ResourceLocation(LibMisc.MOD_ID, LibBlockNames.FUNNEL);
		thaumictinkerer_funnel.modRegistry.addBlockForRegistry(this);
		thaumictinkerer_funnel.modRegistry.addBlockItemForRegistry(this);
		
    }
    
    
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	
	

	
	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos){
		
		TileEntityFunnel funnel = (TileEntityFunnel) world.getTileEntity(pos);		
		return funnel.comparatorSignal();

	}
	
	
	@Override
    public Block setCreativeTab(CreativeTabs tab)
    {
		super.setCreativeTab(tab);
		return this;
	}
    
    
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    	//return EnumBlockRenderType.INVISIBLE;
    }
    
    @Override
    public boolean isTranslucent(IBlockState state) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, JAR);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity te = (TileEntity) worldIn.getTileEntity(pos);
        if (te instanceof TileEntityFunnel) {
            TileEntityFunnel funnel = (TileEntityFunnel) te;
            if (funnel.getInventory().getStackInSlot(0) == ItemStack.EMPTY)
                return state.withProperty(JAR, false);
            else
                return state.withProperty(JAR, true);
        }
        return state.withProperty(JAR, false);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.blockState.getBaseState().withProperty(JAR, false);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos.down()).getBlock() == Blocks.HOPPER;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityFunnel();
    }


    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityFunnel) {
            TileEntityFunnel funnel = (TileEntityFunnel) te;
            ItemStack stack = funnel.getInventory().getStackInSlot(0);
            if (stack == ItemStack.EMPTY) {
                ItemStack playerStack = playerIn.getHeldItem(hand);
                if (funnel.isItemValidForSlot(0, playerStack)) {
                    funnel.getInventory().insertItem(0, playerStack.copy(), false);
                    playerStack.setCount(playerStack.getCount() - 1);
                    if (playerStack.isEmpty()) {
                        playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, ItemStack.EMPTY);
                    }
                    funnel.markDirty();
                    return true;
                }
            } else {
                ItemStack jar = stack.copy();
                IEssentiaContainerItem item = (IEssentiaContainerItem) stack.getItem();
                if (item.getAspects(jar) == null || item.getAspects(jar).getAspects().length == 0) {
                    jar.setTagCompound(null);
                }
                if (!playerIn.inventory.addItemStackToInventory(jar)) {
                    playerIn.dropItem(jar, false);

                }
                funnel.getInventory().setStackInSlot(0, ItemStack.EMPTY);
                funnel.markDirty();
                return true;
            }
        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntityFunnel funnel = (TileEntityFunnel) worldIn.getTileEntity(pos);
        ItemStack inv = funnel.getInventory().getStackInSlot(0);
        if (inv != ItemStack.EMPTY) {
            EntityItem item = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), inv);
            worldIn.spawnEntity(item);
        }
        super.breakBlock(worldIn, pos, state);

    }

	@Override
	public Class<? extends TileEntity> getClassTileEntity() {
		// TODO Auto-generated method stub
		return TileEntityFunnel.class;
	}
}

