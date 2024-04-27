package es.luiscuesta.thaumictinkerer_funnel.common.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import es.luiscuesta.thaumictinkerer_funnel.Thaumictinkerer_funnel;
import es.luiscuesta.thaumictinkerer_funnel.common.libs.LibMisc;
import es.luiscuesta.thaumictinkerer_funnel.common.tileentity.IRedstoneTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract  class BlockTileEntity<T extends TileEntity> extends Block {
	
    private String baseName;
    private ResourceLocation resourceLocation;
    private boolean preserveTileEntity;
    
    
   

	//private static final Map<IRegistryDelegate<Block>, IStateMapper> customStateMappers = ReflectionHelper.getPrivateValue(ModelLoader.class, null, "customStateMappers");
	//private static final DefaultStateMapper fallbackMapper = new DefaultStateMapper();
    
    public ResourceLocation getResourceLocation() {
    	return resourceLocation;
    }
    
    
	public   String getUnlocalizedName() {
		return resourceLocation.getResourceDomain() + "." + resourceLocation.getResourcePath();
	}
	
	public   String getItemBlockName() {
		return resourceLocation.getResourceDomain() + ":" + resourceLocation.getResourcePath();
	}	
	
	
    public BlockTileEntity(String name, Material materialIn, final boolean preserveTileEntity) {

        this(name, materialIn);
        this.setTickRandomly(false);
        this.preserveTileEntity = preserveTileEntity;
    }
	
    public BlockTileEntity(String name, Material blockMaterialIn) {
  
        super(blockMaterialIn, blockMaterialIn.getMaterialMapColor());
        baseName = name;
        //setBlockName(this, name);
        setHardness(2);
        if (isInCreativeTab())
            setCreativeTab(Thaumictinkerer_funnel.getTab());
        resourceLocation= new ResourceLocation(LibMisc.MOD_ID, name);
    }


    
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		Item item=Item.getItemFromBlock(this);
		if(item != Items.AIR)	{			
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));			
		}
	}
	
	
    protected boolean isInCreativeTab() {
        return true;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        int i = 0;
        String name = "item." + LibMisc.RESOURCE_PREFIX + baseName + "." + i;
        while (I18n.hasKey(name)) {
            tooltip.add(I18n.format(name));
            i++;
            name = "item." + LibMisc.RESOURCE_PREFIX + baseName + "." + i;
        }

    }
	
	//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	


    @Override
    public boolean hasTileEntity(final IBlockState state) {
        return true;
    }
    
    public abstract Class<? extends TileEntity> getClassTileEntity();
    	
    public abstract void onBlockPlaced(World world,BlockPos pos,ItemStack itemStackUsed);

    @Override
    public abstract TileEntity createTileEntity(World world, IBlockState state);


    @SuppressWarnings("unchecked")
    @Nullable
    protected T getTileEntity(final IBlockAccess world, final BlockPos pos) {
        return (T) world.getTileEntity(pos);
    }

    @Override
    public boolean removedByPlayer(final IBlockState state, final World world, final BlockPos pos, final EntityPlayer player, final boolean willHarvest) {
        // If it will harvest, delay deletion of the block until after getDrops
        if(preserveTileEntity && willHarvest && !player.capabilities.isCreativeMode)
            return true;
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(final World world, final EntityPlayer player, final BlockPos pos, final IBlockState state, @Nullable final TileEntity te, final ItemStack stack) {
        super.harvestBlock(world, player, pos, state, te, stack);

        if (preserveTileEntity) {
            this.onBlockHarvested(world,pos,state,player);
            world.setBlockToAir(pos);
            world.removeTileEntity(pos);
        }
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }


    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        super.updateTick(world, pos, state, random);
    }

    public void customNeighborsChanged(World world, BlockPos pos) {
        this.updateRedstone(world, pos);

    }

    @SuppressWarnings("deprecation")
	@Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
    	super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        customNeighborsChanged(worldIn, pos);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(world, pos, neighbor);
        if (world instanceof World)
            customNeighborsChanged((World) world, pos);
    }
    
    private boolean isPowered(World world,BlockPos pos) {
	    boolean powered = false;
		for(EnumFacing dir : EnumFacing.VALUES) {// EnumFacing.HORIZONTALS
			int redstoneSide = world.getRedstonePower(pos.offset(dir), dir);
			if(redstoneSide >= 14) {
				powered = true;				
				break;
			}
		}
		return powered;
	}

    public void updateRedstone(World world, BlockPos pos) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof IRedstoneTileEntity) {
            	IRedstoneTileEntity base = (IRedstoneTileEntity) tile;
      
                boolean powered = isPowered (world,pos);
                boolean wasPowered = base.getRedstonePowered();
                if (powered && !wasPowered) {
                    if (base.respondsToPulses()) {
                        world.scheduleUpdate(pos, this, 4);
                    }
                    base.setRedstonePowered(true);
                } else if (!powered && wasPowered) {
                    base.setRedstonePowered(false);
                }
            }
        }

    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        this.updateRedstone(worldIn, pos);
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IRedstoneTileEntity) {
        	IRedstoneTileEntity base = (IRedstoneTileEntity) tile;
            return base.canRedstoneConnect();
        }
        return false;
    }
}
