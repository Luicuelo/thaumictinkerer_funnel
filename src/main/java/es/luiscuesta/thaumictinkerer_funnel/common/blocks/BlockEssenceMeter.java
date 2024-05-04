package es.luiscuesta.thaumictinkerer_funnel.common.blocks;

import java.util.HashMap;
import java.util.Map;

import es.luiscuesta.thaumictinkerer_funnel.Thaumictinkerer_funnel;
import es.luiscuesta.thaumictinkerer_funnel.common.libs.LibBlockNames;
import es.luiscuesta.thaumictinkerer_funnel.common.libs.LibMisc;
import es.luiscuesta.thaumictinkerer_funnel.common.tileentity.TileEntityEssentiaMeter;
import es.luiscuesta.thaumictinkerer_funnel.common.tileentity.TileEntityFunnel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.SoundsTC;

public class BlockEssenceMeter extends BlockTileEntity<TileEntityFunnel> {

	public enum BlockColors implements IStringSerializable{
	    RED("red", 1),
	    BROWN("brown", 2),
	    ORANGE("orange", 3),
	    YELLOW("yellow", 4),
	    //GREEN("green", 5),
	    LIME("lime", 5),
	    LIGHT_BLUE("light_blue", 6),
	    BLUE("blue", 7),  
	    CYAN("cyan", 8),  	 
	    PINK("pink", 9),
	    //PURPLE("purple", 10),
	    MAGENTA("magenta", 10),
	    WHITE("white", 11),
	    SILVER("silver", 12),
	    GRAY("gray", 13),
	    BLACK("black", 14);  


		private static final Map<Integer,BlockColors> _COLORS_MAP= new HashMap<>();
		
	    private final String _colorName;
		private final int _value;

		
	    static {
	        for (BlockColors color : BlockColors.values()) {
	            _COLORS_MAP.put(color.getValue(), color);
	        }
	    }
		
	    public  BlockColors getNext() {
	    	return getNext(getValue());
	    }
	    
	    public int getEssenceCapacity() {
	    	return (int) Math.max((getValue()*1000),Math.pow(2, getValue()+2));
	    }
	    	    
		public static BlockColors getNext(Integer value) {
			int next=value+1;
			if (next>BlockColors.values().length) next=1;
			return _COLORS_MAP.get(next);
		}
		
		public static BlockColors getByIndex(Integer index) {
			return _COLORS_MAP.get(index+1);
		}
		
		BlockColors(String colorName, int value) {
	       _colorName = colorName;
	       _value=value;
	    }

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			 return _colorName;
		}
		
		public int getValue() {
			return _value;
		}
		
		public int getIndex() {
			return _value-1;
		}
		
	}

	///IStringSerializable 
    public static final net.minecraft.block.properties.PropertyEnum<BlockColors> BLOCK_COLOR=PropertyEnum.create("amtcolor", BlockColors.class); 
    public ResourceLocation resourceLocation;


    public BlockEssenceMeter() {
        super(LibBlockNames.METER, Material.ROCK, true);
        setHardness(3.0F);
        setResistance(8.0f);
        setDefaultState(this.getBlockState().getBaseState());
        setTickRandomly(true);
        setLightLevel(0.4F);
        //setDefaultState(this.getBlockState().getBaseState().withProperty(BLOCK_COLOR,BlockColors.CYAN));
		resourceLocation= new ResourceLocation(LibMisc.MOD_ID, LibBlockNames.METER);
		Thaumictinkerer_funnel.modRegistry.addBlockForRegistry(this);
		Thaumictinkerer_funnel.modRegistry.addBlockItemForRegistry(this);
		
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BLOCK_COLOR);
    }
    
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    	
    	return worldIn.getBlockState(pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
           // int colorIndex = worldIn.rand.nextInt(16);
            IBlockState newState=this.getDefaultState().withProperty(BLOCK_COLOR, BlockColors.getByIndex(0));
            worldIn.setBlockState(pos,newState, 2);   
            if(worldIn.isRemote)return;
        	TileEntity te;
        	te=worldIn.getTileEntity(pos);
        	if (te instanceof TileEntityEssentiaMeter) {   
        		if (!worldIn.isRemote) ((TileEntityEssentiaMeter)te).updateInfoFromHopper();
        	}
        }
    }
    
    private void updateInfoFromHopper(World worldIn, BlockPos pos) {
    	if(worldIn.isRemote)return;
    	TileEntity te;
    	te=worldIn.getTileEntity(pos);
    	if (te instanceof TileEntityEssentiaMeter) {
    		((TileEntityEssentiaMeter)te).updateInfoFromHopper();
    	}
    }
    
    
    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {    	
        super.onNeighborChange(world, pos, neighbor);      
        if (world instanceof World) {
        	 if(((World)world).isRemote)return;
        	 updateInfoFromHopper((World)world,pos);
        }
    }
    
    
    /*
    @Override
    public void neighborChanged(IBlockState newState,World worldIn,BlockPos pos,Block b, BlockPos fromPos) {
    	super.neighborChanged(newState, worldIn, pos, b, fromPos);
    	updateInfoFromHopper(worldIn,pos);
    }
    */
  
    protected void updateState(World worldIn, BlockPos pos, IBlockState state)
    {
    	if(worldIn.isRemote)return;
    	TileEntity te;
    	te=worldIn.getTileEntity(pos);
    	if (te instanceof TileEntityEssentiaMeter) {
    		((TileEntityEssentiaMeter)te).updateInfoFromHopper();
    	}
    
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    	//return EnumBlockRenderType.INVISIBLE;
    }
	
	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos){
		
		TileEntityEssentiaMeter meter = (TileEntityEssentiaMeter) world.getTileEntity(pos);		
		return meter.comparatorSignal();

	}
    
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	

	@Override
    public Block setCreativeTab(CreativeTabs tab)
    {
		super.setCreativeTab(tab);
		return this;
	}


    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
    	int index= state.getValue(BlockEssenceMeter.BLOCK_COLOR).getIndex();
    	return index;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.blockState.getBaseState().withProperty(BLOCK_COLOR, BlockColors.getByIndex(meta));
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
			    EnumFacing fhdir = BlockHopper.getFacing(tileEntity.getBlockMetadata()).getOpposite();//20240425
			    if (posCheck.offset(fhdir).equals(pos)) 
			    	if (worldIn.getBlockState(posCheck.up()).getBlock() == ModBlocks.funnel) return posCheck.up();
			}		
		}		 		
		return null;
	}
    
    private boolean findHopperToMe(World worldIn, BlockPos pos) {	
    	if (worldIn.getBlockState(pos.down()).getBlock() == Blocks.HOPPER) return true;
    	BlockPos funnel=findPosFunnelOnHopperToMe(worldIn,pos);
    	if (funnel==null)return false;
    	return true;    	
    }
    
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) 
        		&& findHopperToMe(worldIn, pos);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityEssentiaMeter();
    }


    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	
    	if (hand==EnumHand.OFF_HAND)return false; //se dispara una vez por cada mano.
    	
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TileEntityEssentiaMeter)) return false;
        TileEntityEssentiaMeter meter=(TileEntityEssentiaMeter)te;
        
        BlockColors blockColor=state.getValue(BLOCK_COLOR);
        if (hand == EnumHand.MAIN_HAND && playerIn.getHeldItemMainhand().isEmpty() &&!playerIn.isSneaking()) {
        	if(worldIn.isRemote)return false;
        	
        	 IBlockState newState=this.getDefaultState().withProperty(BLOCK_COLOR, blockColor.getNext());
             worldIn.setBlockState(pos,newState, 2);
             meter.sendUpdate();
        	return true;
        }
        if (hand == EnumHand.MAIN_HAND && playerIn.getHeldItemMainhand().getItem()==ItemsTC.label &&!playerIn.isSneaking()) {
        	
        	if (meter.getLabelFacing()==null || meter.getLabelFacing()==EnumFacing.DOWN) {
        		if(worldIn.isRemote)return false;
	        	 meter.setLabelFacing(facing);
	        	 ItemStack playerStack = playerIn.getHeldItem(hand);
	             playerStack.setCount(playerStack.getCount() - 1);
	             if (playerStack.isEmpty()) {
	                 playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, ItemStack.EMPTY);
	             }
	             meter.sendUpdate();
	             return true;
        	}
        }
        
        if (hand == EnumHand.MAIN_HAND &&playerIn.isSneaking() && playerIn.getHeldItemMainhand().isEmpty() ) {
        	
        	if (meter.getLabelFacing()==null || meter.getLabelFacing()==EnumFacing.DOWN) return false;
        	if (meter.getLabelFacing()!=facing) return false;
        	ItemStack label=new ItemStack(ItemsTC.label,1,0);
        	meter.setLabelFacing(EnumFacing.DOWN);
            if (worldIn.isRemote) {
            	worldIn.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.page, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            }
            else {
	        	if (!playerIn.inventory.addItemStackToInventory(label)) {
	                playerIn.dropItem(label, false);
	
	            }
	        	meter.sendUpdate();
            }
        	return true;
        }
        return false;
    }
    
    
	@Override
	public void onBlockPlaced(World world, BlockPos pos, ItemStack itemStackUsed) {
		if (world.isRemote) return;

		if (itemStackUsed.hasTagCompound()) {
			NBTTagCompound tag = itemStackUsed.getTagCompound();
			if (tag != null) {
				EnumFacing labelFacing = EnumFacing.getFront(tag.getInteger("labelfacing"));
				if (labelFacing != null) {
					TileEntity tile = world.getTileEntity(pos);
					if (tile != null && tile instanceof TileEntityEssentiaMeter) {
						TileEntityEssentiaMeter tileMeter = (TileEntityEssentiaMeter) tile;
						tileMeter.setLabelFacing(labelFacing);
					}
				}
			}
		}
	}
    
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

    	World worldIn=(World)world;
    	if(worldIn.isRemote) return;    	
    	super.getDrops(drops, world, pos, state, fortune);
    	
    	if (drops==null)return;
    	if (drops.size()<1)return;    	
    	ItemStack drop =drops.get(0);    
    	
    	TileEntity tile = world.getTileEntity(pos);	    	    	
    	if (tile!=null && tile instanceof TileEntityEssentiaMeter) {
    		TileEntityEssentiaMeter tileMeter = (TileEntityEssentiaMeter) tile;
    		EnumFacing labelFacing=tileMeter.getLabelFacing();
    		if(labelFacing!=null && labelFacing!=EnumFacing.DOWN) {
    			NBTTagCompound stackTags=new NBTTagCompound();
    			stackTags.setInteger("labelfacing", labelFacing.getIndex());    	
    			drop.setTagCompound(stackTags);
    		}
    	}    	      
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
            super.breakBlock(worldIn, pos, state);

    }

	@Override
	public Class<? extends TileEntity> getClassTileEntity() {
		return TileEntityEssentiaMeter.class;
	}
}

