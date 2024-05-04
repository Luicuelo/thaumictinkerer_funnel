package es.luiscuesta.thaumictinkerer_funnel.common.tileentity;

import es.luiscuesta.thaumictinkerer_funnel.common.blocks.BlockEssenceMeter;
import es.luiscuesta.thaumictinkerer_funnel.common.blocks.BlockEssenceMeter.BlockColors;
import es.luiscuesta.thaumictinkerer_funnel.common.blocks.ModBlocks;
import es.luiscuesta.thaumictinkerer_funnel.common.tileentity.TileEntityFunnel.JarAspect;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaContainerItem;

public class TileEntityEssentiaMeter extends TileEntityBase implements ITickable,IAspectContainer,IRedstoneTileEntity	 {


	private static final int TITLE_TICK=20;
	public int ticksElapsed=0;
	
	
	private int _hooperAmmount;
	private Aspect _hopperAspect;
	private AspectList hopperAspectList=null;
	
	private EnumFacing _hopperPosition=null;
	
	private EnumFacing _LabelFacing=EnumFacing.DOWN;
	
	public EnumFacing getLabelFacing() {
		return _LabelFacing;
	}

	public void setLabelFacing(EnumFacing facing) {
		_LabelFacing=facing;
	}
    
	public void setHopperAspect (Aspect hopperAspect,int hooperAmmount) {

		boolean needChangeAspectlist=false;
		if (hopperAspectList==null)needChangeAspectlist=true;
		if (hopperAspect!=_hopperAspect)needChangeAspectlist=true;
		_hopperAspect = hopperAspect;	

		if (hopperAspect==null)hooperAmmount=0;
		if (_hooperAmmount!=hooperAmmount)needChangeAspectlist=true;			
		_hooperAmmount = hooperAmmount;
		if(_hopperAspect==null)hopperAspectList=null;
		else
			if(needChangeAspectlist&&_hopperAspect!=null)hopperAspectList=new AspectList().add(getHopperAspect(),getHooperAmmount());

	}
	

	public Aspect getHopperAspect() {
		return _hopperAspect;
	}
	public int getHooperAmmount() {
		if (_hopperAspect==null)return 0;
		return _hooperAmmount;
	}
	
	public TileEntityEssentiaMeter() {
		super();				
	}
	
	public BlockColors getBlockColor(){
		IBlockState blockState=getWorld().getBlockState(pos);
        if(blockState==null) return null;      
        if(blockState.getBlock()!=ModBlocks.essenceMeter) return null;
        BlockColors blockColor=blockState.getValue(BlockEssenceMeter.BLOCK_COLOR);
        return blockColor;		
	}
	
    @Override
    public void onLoad() {

        World world = getWorld();
        if (world != null) {
        	if (!world.isRemote) updateInfoFromHopper();
        }
    }

	public boolean isTitleTick() {
		return((ticksElapsed%TITLE_TICK==1));
	}
	
	public int comparatorSignal() {
		return 0;
	}
	
	
	//-----------------------------------------------------------------------------
	@Override
	public void writeExtraNBT(NBTTagCompound nbt) {
		super.writeExtraNBT(nbt);//redstone
		String tag="NONE";
		if ( getHopperAspect()!=null)tag=getHopperAspect().getTag();
		nbt.setString("aspect", tag);
		nbt.setInteger("ammount", getHooperAmmount());
		nbt.setInteger("dirlabel", getLabelFacing().getIndex());
		if (_hopperPosition!=null) 	nbt.setInteger("hopperposition", _hopperPosition.getIndex());

	}
	
	@Override
	public void readExtraNBT(NBTTagCompound nbt){	
		super.readExtraNBT(nbt);//redstone
		String aspectString=nbt.getString("aspect");
		this.setHopperAspect(Aspect.getAspect(aspectString),nbt.getInteger("ammount"));
		this.setLabelFacing(EnumFacing.getFront(nbt.getInteger("dirlabel")));
		_hopperPosition=EnumFacing.getFront(nbt.getInteger("hopperposition"));
	}
	
	//-----------------------------------------------------------------------------

	private TileEntityHopper getHopper(EnumFacing dirHopper) {	
		if (dirHopper==null)return null;
		TileEntity tile=world.getTileEntity(pos.offset(dirHopper));
		if(tile!=null && tile instanceof TileEntityHopper) return (TileEntityHopper)tile;
		return null;
	}
	
	private TileEntityHopper getHopper() {		
		return getHopper(_hopperPosition);
	}
	
	private EnumFacing findHopperToMe() {				
		for(EnumFacing dir : EnumFacing.HORIZONTALS) {// EnumFacing.HORIZONTALS
			BlockPos posCheck=pos.offset(dir);
			TileEntity tile = world.getTileEntity(posCheck);
			if (tile instanceof TileEntityHopper) {
				EnumFacing fhdir = BlockHopper.getFacing(tile.getBlockMetadata());//20240425
				if (posCheck.offset(fhdir.getOpposite()).equals(pos)) return fhdir;
			}			
		}		 		
		return null;
	}
	

	
	public void updateInfoFromHopper() {
		// Aspect aspect = aspectList.getAspects()[0];
		if (world == null)
			return;
		if (world.isRemote)
			return;

		TileEntity tile = getHopper(); // cached Hopper;
		if (tile == null) {
			_hopperPosition = null;
			tile = getHopper(EnumFacing.DOWN);
			if (tile == null) {
				EnumFacing dirHopper = findHopperToMe();
				if (dirHopper != null) {
					_hopperPosition = dirHopper;
					tile = getHopper(dirHopper);
				}

			} else
				_hopperPosition = EnumFacing.DOWN;
		}

		Aspect actualAspect = null;
		int totalAmmount = 0;
		boolean mixedAspects = false;

		if (tile != null && tile instanceof TileEntityHopper) {
			TileEntityHopper hopper = (TileEntityHopper) tile;
			ItemStack itemStack;
			for (int i = 0; i < hopper.getSizeInventory(); ++i) {
				itemStack = hopper.getStackInSlot(i);
				if (itemStack != null && itemStack != ItemStack.EMPTY) {

					int stackQuantity = itemStack.getCount();
					// int maxQuantity = itemStack.getMaxStackSize();

					if (itemStack.getItem() instanceof IEssentiaContainerItem) {
						IEssentiaContainerItem jarItem = (IEssentiaContainerItem) itemStack.getItem();
						// int itemCapacity = ItemCapacityDictionary.getCapcityFromJar(itemStack);
						if (jarItem.getAspects(itemStack) == null || jarItem.getAspects(itemStack).size() == 0)
							continue;

						AspectList aspectList = jarItem.getAspects(itemStack);
						if (aspectList != null && aspectList.size() > 1) {
							mixedAspects = true;
							break;
						}
						if (aspectList != null && aspectList.size() == 1) {
							Aspect aspect = aspectList.getAspects()[0];
							if (actualAspect != null && !aspect.getTag().equals(actualAspect.getTag())) {
								mixedAspects = true;
								break;
							}
							if (aspect != null) {
								actualAspect = aspect;
								int amount = aspectList.getAmount(aspect);
								totalAmmount += (amount * stackQuantity);
							}
						}
					}
				}
			}
			
			if (_hopperPosition != EnumFacing.DOWN) {
				BlockPos funnelPos= hopper.getPos().offset(EnumFacing.UP);
				TileEntity tile2 = world.getTileEntity(funnelPos);
				if (tile2 instanceof TileEntityFunnel) {
					TileEntityFunnel funnel= (TileEntityFunnel)tile2;
					JarAspect jarAspect=funnel.getJarAspect();
					if(jarAspect!=null) {
						Aspect aspect=jarAspect.getAspect();
						if (aspect!=null&&(aspect.equals(actualAspect)||actualAspect==null)) {
							actualAspect=aspect;
							totalAmmount +=jarAspect.getAmount();
						}
					}
				}				
			}
		}

		String lastAspectString = "";
		String actualAspectString = "";

		if (mixedAspects) {
			actualAspect = null;
			totalAmmount = 0;
		}
		if (getHopperAspect() != null)
			lastAspectString = getHopperAspect().getName();
		if (actualAspect != null)
			actualAspectString = actualAspect.getName();
		int lastAmmount = getHooperAmmount();

		setHopperAspect(actualAspect, totalAmmount);

		if (!actualAspectString.equals(lastAspectString) || lastAmmount != totalAmmount) {
			sendUpdate();
		}

	}
	

	public void sendUpdate() {
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
		markDirty();	
	}
	
	@Override
	public void update() {
		ticksElapsed++;
	}

	/*
	TileEntity hoppered = getHopperFacing(hopper.getPos(), hopper.getBlockMetadata());
	private TileEntity getHopperFacing(BlockPos hopperPos, int hopperMetadata) {
		EnumFacing i = BlockHopper.getFacing(hopperMetadata);
		return world.getTileEntity(hopperPos.offset(i));
	}*/
	


	
	@Override
	public boolean respondsToPulses() {
		return false;
	}
	@Override
	public AspectList getAspects() {
		return hopperAspectList;
	}
	@Override
	public void setAspects(AspectList aspects) {
	}
	@Override
	public boolean doesContainerAccept(Aspect tag) {
		return false;
	}
	@Override
	public int addToContainer(Aspect tag, int amount) {
		return 0;
	}
	@Override
	public boolean takeFromContainer(Aspect tag, int amount) {
		return false;
	}
	@Override
	public boolean takeFromContainer(AspectList ot) {
		return false;
	}
	@Override
	public boolean doesContainerContainAmount(Aspect tag, int amount) {
		if (getHopperAspect()!=null&&getHopperAspect().equals(tag)&&getHooperAmmount()>=amount)return true;
		return false;
	}
	@Override
	public boolean doesContainerContain(AspectList ot) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int containerContains(Aspect tag) {
		if (getHopperAspect()!=null&&getHopperAspect().equals(tag)) return getHooperAmmount();
		return 0;
	}

}
