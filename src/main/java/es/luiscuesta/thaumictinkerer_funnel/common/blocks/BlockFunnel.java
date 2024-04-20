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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.items.ItemsTC;

public class BlockFunnel extends BlockTileEntity<TileEntityFunnel> {

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
		Thaumictinkerer_funnel.modRegistry.addBlockForRegistry(this);
		Thaumictinkerer_funnel.modRegistry.addBlockItemForRegistry(this);
		
    }
    
    @Override
    @SideOnly(Side.CLIENT)
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
	
	
	
    @SuppressWarnings("deprecation")
	private boolean findMeterToMe(World worldIn, BlockPos pos) {				
    	for(EnumFacing dir : EnumFacing.VALUES) {
			if (dir.equals(EnumFacing.DOWN))continue;			
			BlockPos posCheck=pos.offset(dir);			
			boolean isHopper=(worldIn.getBlockState(posCheck).getBlock() == Blocks.HOPPER);			
			IBlockState blockState = worldIn.getBlockState(posCheck);
			if (isHopper&&blockState!=null&blockState.getBlock().hasTileEntity()) {
			    TileEntity tileEntity = worldIn.getTileEntity(posCheck);
			    EnumFacing fhdir = BlockHopper.getFacing(tileEntity.getBlockMetadata());
			    if (posCheck.offset(fhdir).equals(pos)) 
			    	if (worldIn.getBlockState(posCheck.up()).getBlock() == ModBlocks.essenceMeter) return true;
			}
		
		}		 		
		return false;
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
				if (findMeterToMe(world, pos))return 15;
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
    	
    	
    	if (hand==EnumHand.OFF_HAND)return false;
    	
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityFunnel) {
        	        	
            TileEntityFunnel funnel = (TileEntityFunnel) te;
            ItemStack stack = funnel.getInventory().getStackInSlot(0);
            
            if (stack == ItemStack.EMPTY) {
            	if (worldIn.isRemote) return true;
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
            	
            	ItemStack playerStack=playerIn.getHeldItemMainhand();
                if (hand != EnumHand.MAIN_HAND || !playerStack.isEmpty()) {                
                	Item playerItem=playerStack.getItem();
                	if (playerItem==ItemsTC.phial) {
                		IEssentiaContainerItem phial=(IEssentiaContainerItem) playerItem;
                		AspectList phialAspectList=phial.getAspects(playerStack) ;
                		if (phialAspectList!= null && phialAspectList.size()>=0) {
                			funnel.fromPhial(worldIn, pos, playerIn, hand, state, phial);
                		}
                		else {
                			funnel.fillPhial(worldIn, pos, playerIn, hand, phial);
                		}
                		return true;
                	}
                	else return true; 
                }
                if (worldIn.isRemote) return true;
                ItemStack jar = stack.copy();
                IEssentiaContainerItem item = (IEssentiaContainerItem) stack.getItem();
                if (item.getAspects(jar) == null || item.getAspects(jar).getAspects().length == 0) {
                	Aspect aspectFromTag=getAspectFromTag(jar);
                	NBTTagCompound itemTags = null;
                	if (aspectFromTag!=null) {
                		itemTags=new NBTTagCompound();
                		itemTags.setString("AspectFilter", aspectFromTag.getTag());
                	}
                    jar.setTagCompound(itemTags);
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

	@Override
	public void onBlockPlaced(World world, BlockPos pos, ItemStack itemStackUsed) {
		// TODO Auto-generated method stub
		
	}
}

