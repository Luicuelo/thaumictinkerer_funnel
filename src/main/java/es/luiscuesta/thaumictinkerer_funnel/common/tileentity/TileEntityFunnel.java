package es.luiscuesta.thaumictinkerer_funnel.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import es.luiscuesta.thaumictinkerer_funnel.common.blocks.BlockFunnel;
import es.luiscuesta.thaumictinkerer_funnel.common.config.TTConfig;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.tiles.essentia.TileJarFillable;

public class TileEntityFunnel extends TileEntityJarFillable
		implements IAspectContainer, ITickable, IAspectSource,IEssentiaTransport {

	public class JarAspect {

		private int _capacity;
		private int _amount;
		private Aspect _aspect;
		IEssentiaContainerItem _jarItem;

		public int getCapacity() {
			return _capacity;
		}

		public int getAmount() {
			return _amount;
		}

		public Aspect getAspect() {
			return _aspect;
		}
		
		public IEssentiaContainerItem getItem() {
			return _jarItem;
		}

		public JarAspect(int capacity, int amount, Aspect aspect,IEssentiaContainerItem jarItem) {
			_capacity = capacity;
			_amount = amount;
			_aspect = aspect;
			_jarItem=jarItem;
		}
	}

	private class MyItemStackHandler extends  ItemStackHandler{
		


	    @Override
	    public int getSlotLimit(int slot) {
	        return 1;
	    }
	
		
		public MyItemStackHandler() {
			super(1);
		}
		
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			comparatorSignal();// Updates if needed
			sendUpdates();
		}

		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return TileEntityFunnel.this.isItemValidForSlot(index, stack);
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (!isItemValidForSlot(slot, stack))
				return stack;
			return super.insertItem(slot, stack, simulate);
		}
				
	}

	private MyItemStackHandler inventory = new MyItemStackHandler();
	private Aspect lastAspect=null;
	private int lastComparatorSignal = 0;
	private int speed = 20;
	private static final int TITLE_TICK=20;
	private int ticksElapsed=0;

	public TileEntityFunnel() {
		super();
		if (TTConfig.funnelSpeed>0 && TTConfig.funnelSpeed<100) {
			speed=TTConfig.funnelSpeed;
		}
	}

	public boolean isTitleTick() {
		return((ticksElapsed%TITLE_TICK==1));
	}
	
	public int comparatorSignal() {

		Aspect aspect;
		boolean aspectHasChanged=false;
		boolean signalHasChanged;
		JarAspect jarAspect = getJarAspect();
		int signal = 0;
		int ammount = 0;
		int capacity = 0;

		if (jarAspect != null) {
			aspect=jarAspect.getAspect();
			aspectHasChanged=(aspect!=lastAspect);
			lastAspect=aspect;
			ammount = jarAspect.getAmount();
			capacity = jarAspect.getCapacity();
			signal = (int) Math.floor((double) (ammount * 15d) / (double) capacity);
			if (signal==0 && ammount>0)signal=1;
		}else {
			aspectHasChanged=(lastAspect!=null);
			lastAspect=null;
		}

		signalHasChanged=(lastComparatorSignal != signal);
		lastComparatorSignal = signal;
		
		if (signalHasChanged||aspectHasChanged) {			
			TileEntityEssentiaMeter tileEntityEssentiaMeter=findTileEntityEssentiaMeterFromMe();
			if (tileEntityEssentiaMeter!=null)tileEntityEssentiaMeter.updateInfoFromHopper();	
			
			if(signalHasChanged) {
				// System.out.println(" Signal change from:"+lastComparatorInputOverride+to:"+signal);				
				sendUpdates();				
				if(!TTConfig.disableJarSwapping&&signal==0 && findTileEntityEssentiaMeterToMe()) {
					changeJar(); //EntityEssentiaMeter acts as comparator circuit to change jar when empty
				}
				if (tileEntityEssentiaMeter!=null&&!tileEntityEssentiaMeter.getRedstonePowered()) {				
					if (ammount>0&&ammount==capacity)changeJar();//is full
				}
			}
		}				
		return signal;
	}
	
	
	private boolean checkTileEntityEssentiaMeter(BlockPos pos) {
		//search for TileEntityEssentiaMeter, not powered, power disables the automatic pulse
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityHopper) {
			TileEntity tile2= world.getTileEntity(pos.up());
			if (tile2 != null && tile2 instanceof TileEntityEssentiaMeter) {
				TileEntityEssentiaMeter tileEntityEssentiaMeter=(TileEntityEssentiaMeter)tile2;
				if (!tileEntityEssentiaMeter.getRedstonePowered()) return true; 											
			}
		}
		
		return false;
	}
	
	private boolean findTileEntityEssentiaMeterToMe() {				
		for(EnumFacing dir : EnumFacing.HORIZONTALS) {// EnumFacing.HORIZONTALS
			if(checkTileEntityEssentiaMeter(pos.offset(dir))) return true;		
		}		 
		if(checkTileEntityEssentiaMeter(pos.up())) return true;			
		return false;
	}
	
	
	private TileEntityEssentiaMeter findTileEntityEssentiaMeterFromMe() {
		TileEntity tile = world.getTileEntity(pos.down());
		if (tile != null && tile instanceof TileEntityHopper) {
			TileEntity hoppered = getHopperOppositeFacing(tile.getPos(), tile.getBlockMetadata()); //20240425
			if (hoppered instanceof TileEntityEssentiaMeter) {
				return (TileEntityEssentiaMeter)hoppered;
			}
		}		
		return null;		
	} 	
	

	private void clearTag(ItemStack jar) {
		
		Aspect aspect= BlockFunnel.getAspectFromTag(jar);
		if (aspect!=null) {
			NBTTagCompound itemTags = new NBTTagCompound();
			itemTags.setString("AspectFilter",aspect.getTag());
			jar.setTagCompound(itemTags);
			return;			
		}		
		jar.setTagCompound(null);	
			
	}
		
	
	
	public JarAspect getJarAspect() {
		int amount = 0;
		if (inventory == null) return null;		
		
		ItemStack jar=inventory.getStackInSlot(0);
		if (jar == ItemStack.EMPTY) return null;
		if (!(jar.getItem() instanceof IEssentiaContainerItem)) return null;
		IEssentiaContainerItem jarItem = (IEssentiaContainerItem) jar.getItem();		
	
		int capacity = JarCapacityDictionary.getCapcityFromJar(jar);		
		if (jarItem.getAspects(jar) == null || jarItem.getAspects(jar).size() == 0)  {			
			return new JarAspect(capacity, 0, BlockFunnel.getAspectFromTag(jar),jarItem);//if have filter returns filter
		}
		//the jar is empty
		
		AspectList aspectList = jarItem.getAspects(jar);
		if (aspectList != null && aspectList.size() == 1) {
			Aspect aspect = aspectList.getAspects()[0];
			amount = aspectList.getAmount(aspect);
			return new JarAspect(capacity, amount, aspect,jarItem);
		}
		return null;

	}

	private void changeJar() {
		//System.out.println("DBG: Redstone Powered");
		if (inventory == null)
			return;
		if (inventory.getStackInSlot(0) == ItemStack.EMPTY)
			return;
		if (!(inventory.getStackInSlot(0).getItem() instanceof IEssentiaContainerItem))
			return;

		if (TTConfig.disableJarSwapping)return;
		
		ItemStack jar = inventory.getStackInSlot(0);
		TileEntity hopper = world.getTileEntity(pos.down());
		if (hopper != null && hopper instanceof TileEntityHopper) {
			//System.out.println("DBG: Try to insert into Hopper");
			if (insertInHopper(jar, (TileEntityHopper) hopper)) {
				inventory.extractItem(0, 1, false);
				if (inventory.getStackInSlot(0).isEmpty()) {
					inventory.setStackInSlot(0, ItemStack.EMPTY);
				}
			}
		}
	}
	
	@Override
	public void setRedstonePowered(boolean b) {
		super.setRedstonePowered(b);		
		if (this.getRedstonePowered()) {
			changeJar();
		}
	}
	
	private boolean compareItemStacks(ItemStack itemStackIn, ItemStack itemStack) {
		
		if(itemStackIn.getMetadata()!=itemStack.getMetadata()) return false;
		if(BlockFunnel.getAspectFromTag(itemStackIn)!=BlockFunnel.getAspectFromTag(itemStack))return false;
		
		boolean stackInEssentiaContainer=(itemStackIn.getItem() instanceof IEssentiaContainerItem) ;
		boolean stackEssentiaContainer=(itemStack.getItem() instanceof IEssentiaContainerItem) ;
		
		if (!stackInEssentiaContainer&& !stackEssentiaContainer)return true;
		if (stackInEssentiaContainer!=stackEssentiaContainer)return false;
		
		IEssentiaContainerItem essentiaItemIn=(IEssentiaContainerItem) (itemStackIn.getItem());
		IEssentiaContainerItem essentiaItem=(IEssentiaContainerItem) (itemStack.getItem());		
		
		AspectList aspectListIn=essentiaItemIn.getAspects(itemStackIn);
		AspectList aspectList=essentiaItem.getAspects(itemStack);
		
		boolean aspectListInIsNull=(aspectListIn==null);
		boolean aspectListIsNull=(aspectList==null);		
		if (aspectListInIsNull!=aspectListIsNull)return false;
		
		if (!aspectListInIsNull) {
			if (aspectListIn.size()!=aspectList.size()) return false;
					
			Aspect[] aspectsIn=aspectListIn.getAspects();
			Aspect[] aspects=aspectList.getAspects();
			for (int index = 0; index<aspectsIn.length;index++) {
				if (aspectsIn[index]!=aspects[index]) return false;
			}		
		}
		return true;			
	}

	private boolean insertInHopper(ItemStack itemStackIn, TileEntityHopper hopper) {

		ItemStack itemStack = null;
		if (itemStackIn == ItemStack.EMPTY)
			return false;
	

		//System.out.println("DBG: Try to insert into Hopper:"+itemStackInMetadata);
		for (int i = 0; i < hopper.getSizeInventory(); ++i) {
			itemStack = hopper.getStackInSlot(i);
			if (itemStack != null && itemStack != ItemStack.EMPTY) {
				
				
				int quantity = itemStack.getCount();
				int maxQuantity = itemStack.getMaxStackSize();

				if (quantity < hopper.getInventoryStackLimit() && quantity < maxQuantity) {
					if (itemStack.getItem().equals(itemStackIn.getItem())
							&& compareItemStacks(itemStackIn, itemStack) ) {
						itemStack.setCount(quantity + 1);
						if (itemStackIn.getCount() - 1 <= 0)
							itemStackIn.setCount(0);
						else {
							itemStackIn.shrink(1);
						}
						//System.out.println("DBG: Same stack at slot:"+i);
						hopper.setInventorySlotContents(i, itemStack);
						return true;
					}
				}
			}
		}

		// if (ItemStack.areItemStacksEqual(titemStack, titemStackIn)) {//compara
		// tambien los nameTags

		for (int i = 0; i < hopper.getSizeInventory(); ++i) {
			itemStack = hopper.getStackInSlot(i);
			if (itemStack == null || itemStack.isEmpty()||itemStack == ItemStack.EMPTY) {
				//System.out.println("DBG: Slot empty:"+i);
				itemStack = itemStackIn.copy();
				itemStack.setCount(1);
				hopper.setInventorySlotContents(i, itemStack);
				if (itemStackIn.getCount() - 1 <= 0)
					itemStackIn.setCount(0);
				else {
					itemStackIn.shrink(1);
				}
				return true;
			}
		}

		return false;
	}

	public boolean isItemValidForSlot(int index, ItemStack stack) {
		Item item = stack.getItem();
		if (stack.getItem()==ItemsTC.phial)return false;
		if (stack.getItem()==ItemsTC.label)return false; 
		return (item instanceof IEssentiaContainerItem);
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public ItemStackHandler getInventory() {
		return inventory;
	}

	@Override
	public void writeExtraNBT(NBTTagCompound nbttagcompound) {
		super.writeExtraNBT(nbttagcompound);//redstone
		nbttagcompound.setTag("inventory", inventory.serializeNBT());
	}

	@Override
	public void readExtraNBT(NBTTagCompound nbttagcompound) {
		super.readExtraNBT(nbttagcompound);//redstone
		inventory.deserializeNBT(nbttagcompound.getCompoundTag("inventory"));
	}

	@Override
	public boolean respondsToPulses() {
		return false;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		if (facing != EnumFacing.DOWN)
			return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
					|| super.hasCapability(capability, facing);
		else
			return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (facing != EnumFacing.DOWN && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) inventory;
		} else {
			return super.getCapability(capability, facing);
		}
	}

	private TileEntity getHopperFacing(BlockPos pos, int getBlockMetadata) {
		EnumFacing i = BlockHopper.getFacing(getBlockMetadata);
		return world.getTileEntity(pos.offset(i));
	}
	
	private TileEntity getHopperOppositeFacing(BlockPos pos, int getBlockMetadata) {
		EnumFacing i = BlockHopper.getFacing(getBlockMetadata).getOpposite();
		return world.getTileEntity(pos.offset(i));
	}

	@Override
	public void update() {
		if (world.isRemote)
			return;

		ticksElapsed++;
		tryToGetEssence(1);
		
		if (isTitleTick()) {

			ItemStack sourceJar = inventory.getStackInSlot(0);
			if (sourceJar == null) return;
			JarAspect jarAspect = this.getJarAspect();
			if (jarAspect == null) return;
			if (jarAspect.getAmount() == 0 || jarAspect.getAspect() == null) return;

			IEssentiaContainerItem jarItem = jarAspect.getItem();
			AspectList aspectList = jarItem.getAspects(sourceJar);
			if (aspectList.size() == 0) return;

			int sourceAmmount=jarAspect.getAmount();
			int ammountToTransfer=Math.min(sourceAmmount, speed);
			//cant transfer more ammount that is in origin Jar.

			if (aspectList != null && aspectList.size() == 1) {
				Aspect aspect = aspectList.getAspects()[0];
				TileEntity tile = world.getTileEntity(pos.down());
				if (tile != null && tile instanceof TileEntityHopper) {
					TileEntity hoppered = getHopperFacing(tile.getPos(), tile.getBlockMetadata());
					if (hoppered instanceof TileJarFillable) {
						int remain = 0;
						boolean added = false;						
						TileJarFillable jar = (TileJarFillable) hoppered;
						AspectList aspectListDestiny = jar.getAspects();
						if (aspectListDestiny != null && (aspectListDestiny.size() == 0 || // is empty
								aspectListDestiny.size() == 1 && aspectListDestiny.getAspects() != null
								&& aspectListDestiny.getAspects()[0] == aspect)) {
							remain = jar.addToContainer(aspect, ammountToTransfer);
							added = true;
						}

						if (added) {
							int amtToRemove = ammountToTransfer - remain;
							boolean hasToRemoveTag =false;
							if ((sourceAmmount-amtToRemove)==0) hasToRemoveTag=true;
							jarItem.setAspects(sourceJar, aspectList.remove(aspect, amtToRemove));
							if (hasToRemoveTag) clearTag(sourceJar);							
							comparatorSignal();// Updates if needed
						}
					}
				}
			}
		}
	}

	
	private void tryToGetEssence(int amtAsked) {

		ItemStack sourceJar = inventory.getStackInSlot(0);
		if (sourceJar == null) return;
		JarAspect jarAspect = this.getJarAspect();
		if (jarAspect == null) return;
		Aspect aspect=jarAspect.getAspect();
		//if (aspect == null) is empty Jar

		int capacity=jarAspect.getCapacity();
		int ammount =jarAspect.getAmount();
		int mySuction= Math.max(32,(capacity-ammount));

		for(EnumFacing dir : EnumFacing.HORIZONTALS) {
			EnumFacing dirFrom=dir.getOpposite();

			TileEntity tile = world.getTileEntity(pos.offset(dir));
			if (tile instanceof TileEntityFunnel)continue;//is another funnel
				
			if (tile != null && tile instanceof IEssentiaTransport) {
				IEssentiaTransport essentiaTransport=(IEssentiaTransport)tile;
				if (essentiaTransport.canOutputTo(dirFrom)) {
					Aspect aspectFrom=essentiaTransport.getEssentiaType(dirFrom);
					if(((aspect == null) ||aspectFrom==aspect) && 
							essentiaTransport.getSuctionAmount(dirFrom)<=mySuction && essentiaTransport.getMinimumSuction()<=mySuction)
					{
						int ammountCanAsk=Math.min(amtAsked, (capacity-ammount));						
						int taken=essentiaTransport.takeEssentia(aspectFrom, ammountCanAsk, dirFrom);
						if (taken>0) {
							addToContainer(aspectFrom, taken);
						}
					}
				}
			}
		}
	}
	
	@Override
	public AspectList getAspects() {
		
		ItemStack stack=inventory.getStackInSlot(0);
		if (stack==null) return null;
		if (stack== ItemStack.EMPTY) return null;
		if (stack.getItem()==null)return null;		
		if (!(stack.getItem() instanceof IEssentiaContainerItem)) return null;
		IEssentiaContainerItem jar=(IEssentiaContainerItem) stack.getItem();
		
		if (jar.getAspects(stack)==null) 
			return new AspectList();				
		return jar.getAspects(stack);
		//can have no essentia
		//jar.getAspects(stack).size()==0
	}

	@Override
	public void setAspects(AspectList aspectList) {
		// Empty
	}

	
	@Override
	public int addToContainer(Aspect aspect, int quantity) {
		JarAspect jarAspect=getJarAspect();
		ItemStack jar=inventory.getStackInSlot(0);
		
		if (jarAspect!=null) {
			int capacity=jarAspect.getCapacity();
			int amount=jarAspect.getAmount();
			IEssentiaContainerItem jarItem=jarAspect.getItem();
			
			int canFill =capacity-amount;
			int quantityToAdd=quantity;
			if (quantityToAdd>canFill) quantityToAdd=canFill;
			AspectList aspectList = jarItem.getAspects(jar);
			if(aspectList==null) aspectList=new AspectList();
			aspectList.add(aspect, quantityToAdd);			
			jarItem.setAspects(jar, aspectList);
	
			inventory.onContentsChanged(0);
			return quantity-quantityToAdd;//remains not added
			
		}
		
		return quantity;
	}
	
	
	@Override
	public boolean doesContainerAccept(Aspect aspect) {
		JarAspect jarAspect=this.getJarAspect();
		if (jarAspect==null)return false;//no jar
		if (jarAspect.getAspect()==null) {
			TileEntityEssentiaMeter tileEntityEssentiaMeter=findTileEntityEssentiaMeterFromMe();
			if(tileEntityEssentiaMeter==null)return true;
			Aspect aspectMeter=tileEntityEssentiaMeter.getHopperAspect();
			if (aspectMeter==null) return true;
			return aspectMeter.equals(aspect); //The essentia meter has aspect in hopper
		}
		if (jarAspect.getAmount()>=jarAspect.getCapacity()) return false;//is full
		return jarAspect.getAspect().equals(aspect);
	}

	@Override
	public boolean takeFromContainer(Aspect aspect, int amtToRemove) {
		JarAspect jarAspect=this.getJarAspect();
		if (jarAspect.getAspect()==aspect) {
			int sourceAmmount=jarAspect.getAmount();
			if (sourceAmmount>=amtToRemove) {
				IEssentiaContainerItem jarItem=jarAspect.getItem();
				
				ItemStack internalJar = inventory.getStackInSlot(0);
				AspectList aspectList = jarItem.getAspects(internalJar);
				boolean hasToRemoveTag =false;
				if ((sourceAmmount-amtToRemove)==0) hasToRemoveTag=true;
				
				jarItem.setAspects(internalJar, aspectList.remove(aspect, amtToRemove));
				if (hasToRemoveTag) clearTag(internalJar);							
				comparatorSignal();// Updates if needed
				return true;
			}
		}
		return false;
		
	}

	@Override
	public boolean takeFromContainer(AspectList aspectList) {		
		Aspect aspect=(aspectList.getAspects())[0];
		int amount=aspectList.getAmount(aspect);
		return takeFromContainer(aspect,amount);
	}

	@Override
	public boolean doesContainerContainAmount(Aspect aspect, int amtToCheck) {
		JarAspect jarAspect=this.getJarAspect();
		if (jarAspect.getAspect()==aspect) {
			int sourceAmmount=jarAspect.getAmount();
			if (sourceAmmount>=amtToCheck) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doesContainerContain(AspectList aspectList) {
		Aspect aspect=(aspectList.getAspects())[0];
		int amount=aspectList.getAmount(aspect);
		return doesContainerContainAmount(aspect,amount);
	}

	@Override
	public int containerContains(Aspect aspect) {
		JarAspect jarAspect=this.getJarAspect();
		if (jarAspect.getAspect()==aspect) return jarAspect.getAmount();
		return 0;
	}

	@Override
	public boolean isConnectable(EnumFacing face) {

		if(face.equals(EnumFacing.DOWN)) return false;	
		JarAspect jarAspect=this.getJarAspect();
		if (jarAspect==null) return false;			
		return true;
	}

	@Override
	public boolean canInputFrom(EnumFacing face) {					
		if (isConnectable(face)) {
			JarAspect jarAspect=this.getJarAspect();
			if (jarAspect.getAmount()>=jarAspect.getCapacity()) return false;;//is full
			return true;
		}
		return false;
	}

	@Override
	public boolean canOutputTo(EnumFacing face) {		
		if ( isConnectable(face)) {
			JarAspect jarAspect=this.getJarAspect();		
			if (jarAspect.getAmount()==0) return false;//is empty
			return true;
		}
		return false;
	}

	@Override
	public void setSuction(Aspect aspect, int amount) {
		//int internalSucction=0;
	}

	@Override
	public Aspect getSuctionType(EnumFacing face) {
		JarAspect jarAspect=this.getJarAspect();
		if (jarAspect==null) return null;	
		Aspect aspect=jarAspect.getAspect();;
		return aspect;		
	}

	@Override
	public int getSuctionAmount(EnumFacing face) {
		JarAspect jarAspect=this.getJarAspect();
		if (jarAspect==null) return 0;
		
		int capacity=jarAspect.getCapacity();
		int ammount =jarAspect.getAmount();
		if (jarAspect.getAmount()>=jarAspect.getCapacity()) return 0;
		return Math.max(32,(capacity-ammount));
	}

	@Override
	public int takeEssentia(Aspect aspect, int amtToRemove, EnumFacing face) {
		JarAspect jarAspect=this.getJarAspect();
		if(jarAspect.getAspect()!=aspect) return 0;
		int sourceAmmount=jarAspect.getAmount();
		if (sourceAmmount==0) return 0;

		int canBeRemoved=Math.min(amtToRemove, sourceAmmount);
		IEssentiaContainerItem jarItem=jarAspect.getItem();
		ItemStack internalJar = inventory.getStackInSlot(0);
		AspectList aspectList = jarItem.getAspects(internalJar);
		boolean hasToRemoveTag =false;
		if ((sourceAmmount-canBeRemoved)==0) hasToRemoveTag=true;

		jarItem.setAspects(internalJar, aspectList.remove(aspect, canBeRemoved));
		if (hasToRemoveTag)clearTag(internalJar);							
		comparatorSignal();// Updates if needed
		return canBeRemoved;			
		//Returns:how much was actually taken
	}

	@Override
	public int addEssentia(Aspect aspect, int amountToAdd, EnumFacing face) {
	
		int amountNotAdded=addToContainer(aspect, amountToAdd);
		return amountToAdd-amountNotAdded;
		//Returns:how much was actually added
	}

	@Override
	public Aspect getEssentiaType(EnumFacing face) {
		return getSuctionType(face); 
	}

	@Override
	public int getEssentiaAmount(EnumFacing face) {
		JarAspect jarAspect=this.getJarAspect();
		return jarAspect.getAmount();
	}

	@Override
	public int getMinimumSuction() {
		return 1;
	}

	@Override
	public boolean isBlocked() {
		JarAspect jarAspect=this.getJarAspect();
		if (jarAspect==null) return true;
		return false;
	}
	
	public void sendUpdate() {
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
		markDirty();	
	}
	
	
	public void fromPhial(World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, IBlockState bi,
			IEssentiaContainerItem phialItem) {

		int base = 10;
		JarAspect jarAspect = this.getJarAspect();
		if (jarAspect == null)
			return;

		int jarAmount = jarAspect.getAmount();
		int jarCapacity = jarAspect.getCapacity();

		ItemStack playerStack = player.getHeldItem(hand);
		AspectList al;
		if (playerStack.getItem() instanceof IEssentiaContainerItem) {
			al = ((IEssentiaContainerItem) playerStack.getItem()).getAspects(playerStack);
		} else
			return;

		if (al != null && al.size() == 1) {
			Aspect phialItemAspect = al.getAspects()[0];
			if (player.getHeldItem(hand).getItemDamage() != 0) {

				if (jarAmount <= jarCapacity - base && doesContainerAccept(phialItemAspect)) {
					if (world.isRemote) {
						player.swingArm(hand);
						player.playSound(SoundEvents.ITEM_BOTTLE_FILL, 0.25f, 1.0f);
						return;
					}
					if (addToContainer(phialItemAspect, base) == 0) {
						world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), bi, bi, 3);
						markDirty();
						player.getHeldItem(hand).shrink(1);
						if (!player.inventory.addItemStackToInventory(new ItemStack((Item) phialItem, 1, 0))) {
							world.spawnEntity(new EntityItem(world, pos.getX() + 0.5f, pos.getY() + 0.5f,
									pos.getZ() + 0.5f, new ItemStack((Item) phialItem, 1, 0)));
						}						
						player.inventoryContainer.detectAndSendChanges();
					}
				}
			}
		}
	}
	
	public void fillPhial(World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, IEssentiaContainerItem phialItem) {
		
		int base=10;
		
		JarAspect jarAspect = this.getJarAspect();
		if (jarAspect==null)return;
		
		int jarAmount=jarAspect.getAmount();
		Aspect aspect=jarAspect.getAspect();
		
		
        if (player.getHeldItem(hand).getItemDamage() == 0) {

            if (jarAmount >= base) {
                if (world.isRemote) {
                    player.swingArm(hand);    
                    player.playSound(SoundEvents.ITEM_BOTTLE_FILL, 0.25f, 1.0f);
                    return;
                }
               
                if (takeFromContainer(aspect, base)) {
                    player.getHeldItem(hand).shrink(1);
                    ItemStack phial2 = new ItemStack((Item)phialItem,1,1);
                    phialItem.setAspects(phial2, new AspectList().add(aspect, base));
                    if (!player.inventory.addItemStackToInventory(phial2)) {
                        world.spawnEntity(new EntityItem(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, phial2));
                    }                   
                    player.inventoryContainer.detectAndSendChanges();
                }
            }
        }        
	}

}
