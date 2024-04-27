package es.luiscuesta.thaumictinkerer_funnel.common.blocks;

import es.luiscuesta.thaumictinkerer_funnel.Thaumictinkerer_funnel;
import es.luiscuesta.thaumictinkerer_funnel.common.libs.LibBlockNames;
import es.luiscuesta.thaumictinkerer_funnel.common.libs.LibMisc;
import es.luiscuesta.thaumictinkerer_funnel.common.tileentity.TileEntityFunnel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.items.ItemsTC;

public class BlockFunnel extends BlockTileEntity<TileEntityFunnel> {

    public static final PropertyBool JAR = PropertyBool.create("jar");
    public static final PropertyBool POWER = PropertyBool.create("power");

    public ResourceLocation resourceLocation;


    public BlockFunnel() {
        super(LibBlockNames.FUNNEL, Material.ROCK, true);
        setHardness(3.0F);
        setResistance(8.0f);
        setDefaultState(this.getBlockState().getBaseState().withProperty(JAR, false).withProperty(POWER, false));
        setTickRandomly(true);
       
        //this.setCreativeTab(thaumictinkerer_funnel.getTab());
		resourceLocation= new ResourceLocation(LibMisc.MOD_ID, LibBlockNames.FUNNEL);
		Thaumictinkerer_funnel.modRegistry.addBlockForRegistry(this);
		Thaumictinkerer_funnel.modRegistry.addBlockItemForRegistry(this);
		
    }
    
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
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
    public boolean isFullCube(IBlockState state) {
        return true;
    }
    
	
	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}
	
	

	@Override //block the hopper below
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
	
	    World world = blockAccess instanceof World ? (World) blockAccess : null;
	    if (world == null) return 0;
	    if (world.isRemote) return 0;
	    	    
	    //side is from who is asking
		if (side==EnumFacing.UP) {
			IBlockState blockStateDown=world.getBlockState(pos.offset(EnumFacing.DOWN));
			if(blockStateDown.getBlock() == Blocks.HOPPER) {				
					if (blockState.getValue(POWER)) return 15;				
			}
		}			
		return 0;
	}
	

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return this.getWeakPower(blockState, blockAccess, pos, side);		
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
        
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    	//return EnumBlockRenderType.INVISIBLE;
    }
 

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, JAR,POWER);
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
       int hasJar=state.getValue(JAR)?1:0;
       int hasPower=state.getValue(POWER)?1:0;
       return (2*hasPower+hasJar);
       
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {    	
        boolean power = (meta & 2) != 0;
        boolean jar = (meta & 1) != 0;    	
        return this.blockState.getBaseState().withProperty(JAR, jar).withProperty(POWER, power);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos.down()).getBlock() == Blocks.HOPPER;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityFunnel();
    }

	public static Aspect getAspectFromTag(ItemStack jar) {
		NBTTagCompound itemTags = jar.getTagCompound();
		if(itemTags!=null) {
			String aspectName = itemTags.getString("AspectFilter");
			return Aspect.getAspect(aspectName);
		}
		return null;
	}
	
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	
		if (hand == EnumHand.OFF_HAND) 	return false;
		TileEntity te = worldIn.getTileEntity(pos);
		if (!(te instanceof TileEntityFunnel)) 	return false;
			
		TileEntityFunnel funnel = (TileEntityFunnel) te;
		ItemStack stack = funnel.getInventory().getStackInSlot(0);
		ItemStack playerStack = playerIn.getHeldItem(hand);
		
		if (stack == ItemStack.EMPTY) { //no jar
			if (worldIn.isRemote) 	return true;
			
			if ( playerIn.getHeldItemMainhand().isEmpty()) { //change POWER
				if (worldIn.isRemote) return true;				
				changePower(worldIn, pos, state);
				funnel.markDirty();
				return true;
			}
			
			if (funnel.isItemValidForSlot(0, playerStack)) { //Put JAR
				funnel.getInventory().insertItem(0, playerStack.copy(), false);
				playerStack.setCount(playerStack.getCount() - 1);
				if (playerStack.isEmpty()) {
					playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, ItemStack.EMPTY);
				}
				funnel.markDirty();
				return true;
			}
									
		} else { //jar

			if ( !playerStack.isEmpty()) {
				Item playerItem = playerStack.getItem();
				
				if (playerItem == ItemsTC.phial) {   //Phial
					IEssentiaContainerItem phial = (IEssentiaContainerItem) playerItem;
					AspectList phialAspectList = phial.getAspects(playerStack);
					if (phialAspectList != null && phialAspectList.size() >= 0) {
						funnel.fromPhial(worldIn, pos, playerIn, hand, state, phial);
					} else {
						funnel.fillPhial(worldIn, pos, playerIn, hand, phial);
					}
					return true;
				} else {  //Switch Jar

					if (playerStack.getCount()==1&& funnel.isItemValidForSlot(0, playerStack)) {
						
						ItemStack jar = stack.copy();
						
						funnel.getInventory().setStackInSlot(0, playerStack);			
						clearTagsFromEmptyJar(jar);			
						playerIn.setHeldItem(hand, jar);
						funnel.markDirty();
						return true;						
						
					}	else return false;									
				}					
			}
			
			//playerStack.isEmpty()
			
			if (!playerIn.isSneaking()) {
				if (worldIn.isRemote) return true;
				
				ItemStack jar = stack.copy();
				clearTagsFromEmptyJar(jar);			
				if (!playerIn.inventory.addItemStackToInventory(jar)) {
					playerIn.dropItem(jar, false);
	
				}
				funnel.getInventory().setStackInSlot(0, ItemStack.EMPTY);
			}else{ //Change POWER
				if (worldIn.isRemote) return true;				
				changePower(worldIn, pos, state);				
			}
			funnel.markDirty();
			return true;			
		}		
		return false;

	}

    private void changePower(World worldIn, BlockPos pos, IBlockState state) {
    
		boolean power = state.getValue(POWER);
		IBlockState newState = this.getDefaultState().withProperty(POWER, !power);
		worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos),newState, 3);
		worldIn.setBlockState(pos, newState, 2);			
		worldIn.notifyNeighborsOfStateChange(pos, this, false);
    }
    
    
    
    private ItemStack clearTagsFromEmptyJar(ItemStack jar) {
		IEssentiaContainerItem item = (IEssentiaContainerItem) jar.getItem();
		if (item.getAspects(jar) == null || item.getAspects(jar).getAspects().length == 0) {
			Aspect aspectFromTag = getAspectFromTag(jar);
			NBTTagCompound itemTags = null;
			if (aspectFromTag != null) {
				itemTags = new NBTTagCompound();
				itemTags.setString("AspectFilter", aspectFromTag.getTag());
			}
			jar.setTagCompound(itemTags);
		}
		return jar;
    	
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

    
    @SuppressWarnings("deprecation")
	private BlockPos findPosFunnelOnHopperToMe(World worldIn, BlockPos pos) {				    	
		for(EnumFacing dir : EnumFacing.VALUES) {
			if (dir.equals(EnumFacing.DOWN))continue;			
			BlockPos posCheck=pos.offset(dir);			
			boolean isHopper=(worldIn.getBlockState(posCheck).getBlock() == Blocks.HOPPER);			
			IBlockState blockState = worldIn.getBlockState(posCheck);
			if (isHopper&&blockState!=null&blockState.getBlock().hasTileEntity()) {
			    TileEntity tileEntity = worldIn.getTileEntity(posCheck);
			    EnumFacing fhdir = BlockHopper.getFacing(tileEntity.getBlockMetadata());
			    if (posCheck.offset(fhdir).equals(pos)) 
			    	if (worldIn.getBlockState(posCheck.up()).getBlock() == ModBlocks.funnel) return posCheck.up();
			}		
		}		 		
		return null;
	}
	
    @SuppressWarnings("deprecation")
	private BlockPos findPosFunnelOnHopperFromMe(World worldIn, BlockPos pos) {		
    	BlockPos posCheck=pos.offset(EnumFacing.DOWN);			
		if(worldIn.getBlockState(posCheck).getBlock() != Blocks.HOPPER)return null;
		IBlockState blockState = worldIn.getBlockState(posCheck);
		if (blockState!=null&blockState.getBlock().hasTileEntity()) {
		    TileEntity tileEntity = worldIn.getTileEntity(posCheck);
		    EnumFacing fhdir = BlockHopper.getFacing(tileEntity.getBlockMetadata());
		    if (worldIn.getBlockState(posCheck.offset(fhdir)).getBlock() == ModBlocks.funnel) return posCheck.offset(fhdir);
		}
		
		return null;
    }
	
	@Override
	public void onBlockPlaced(World world, BlockPos pos, ItemStack itemStackUsed) {
		
		BlockPos funnelToChange;
		
		BlockPos funnel2= findPosFunnelOnHopperFromMe(world, pos);
		if (funnel2 != null)  {
			funnelToChange=pos;
		}else funnelToChange = findPosFunnelOnHopperToMe(world, pos);
			

		if (funnelToChange != null) {
			IBlockState newState = ModBlocks.funnel.getDefaultState().withProperty(BlockFunnel.POWER, true);
			world.setBlockState(funnelToChange, newState, 2);
			TileEntity tileEntity = world.getTileEntity(funnelToChange);
			if (tileEntity instanceof TileEntityFunnel) {
				TileEntityFunnel funnelTile=(TileEntityFunnel) tileEntity;
				funnelTile.sendUpdate();
			}
		}
		
	}
}

