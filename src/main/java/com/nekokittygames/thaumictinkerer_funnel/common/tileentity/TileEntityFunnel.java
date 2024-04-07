package com.nekokittygames.thaumictinkerer_funnel.common.tileentity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.nekokittygames.thaumictinkerer_funnel.common.misc.SingleItemStackHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.essentia.TileJarFillable;
import com.nekokittygames.thaumictinkerer_funnel.common.config.TTConfig;

public class TileEntityFunnel extends TileEntityThaumicTinkerer
		implements IAspectContainer, ITickable, ITileJarFillable,IAspectSource,IEssentiaTransport {

	private static class ItemCapacityDictionary {
		private static final Map<String, Integer> ITEM_CAPACITY_MAP = new HashMap<>();

		static {
			ITEM_CAPACITY_MAP.put("thaumcraft:jar_normal", 250);

		}

		public static Integer getCapacity(String itemName) {
			return ITEM_CAPACITY_MAP.get(itemName);
		}

		public static void putCapacity(String nombre, Integer capacidad) {
			ITEM_CAPACITY_MAP.put(nombre, capacidad);
		}
	}

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

	private class MyItemStackHandler extends  SingleItemStackHandler{
		
		public MyItemStackHandler() {
			super();
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

		JarAspect jarAspect = getJarAspect();
		int signal = 0;
		int ammount = 0;
		int capacity = 0;

		if (jarAspect != null) {
			ammount = jarAspect.getAmount();
			capacity = jarAspect.getCapacity();
			signal = (int) Math.floor((double) (ammount * 15d) / (double) capacity);
			if (signal==0 && ammount>0)signal=1;
		}

		// if (jarAspect!=null) System.out.println(jarAspect.getAspect().getName()+"
		// Amount:"+ammount+" Capadity:"+capacity);
		if (lastComparatorSignal != signal) {
			// System.out.println(" Signal change from:"+lastComparatorInputOverride+"
			// to:"+signal);
			lastComparatorSignal = signal;
			sendUpdates();
		}
		return signal;
	}
	
	


	public JarAspect getJarAspect() {
		int amount = 0;
		if (inventory == null) return null;		
		
		ItemStack jar=inventory.getStackInSlot(0);
		if (jar == ItemStack.EMPTY) return null;
		if (!(jar.getItem() instanceof IEssentiaContainerItem)) return null;
		IEssentiaContainerItem jarItem = (IEssentiaContainerItem) jar.getItem();		


		String jarName = jar.getItem().getRegistryName().toString();
		int capacity = 0;
		Integer cachedCapacity=ItemCapacityDictionary.getCapacity(jarName);
		if (cachedCapacity != null) {
			capacity = ItemCapacityDictionary.getCapacity(jarName);
			// System.out.println(" Jar:"+jarName+" found, capacity:"+capacity);
		}

		else {

			// System.out.println(" Jar:"+jarName+" Not found,");
			Block blockjar = Block.getBlockFromItem(jar.getItem());
			if (blockjar instanceof ITileEntityProvider) {
				TileEntity tileEntityJar = ((ITileEntityProvider) blockjar).createNewTileEntity(null, 0);
				if (tileEntityJar instanceof TileJarFillable) {

					try {
						Method method = tileEntityJar.getClass().getDeclaredMethod("getCapacity");
						method.setAccessible(true);
						capacity = (int) method.invoke(tileEntityJar);
					} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						// e.printStackTrace();
						capacity = TileJarFillable.CAPACITY;
					}
					// System.out.println(" Jar:"+jarName+" put in dictionary, capacity:"+capacity);
					ItemCapacityDictionary.putCapacity(jarName, capacity);
				}
			}
		}

		if (jarItem.getAspects(jar) == null || jarItem.getAspects(jar).size() == 0) return new JarAspect(capacity, 0, null,jarItem);
		//the jar is empty
		
		AspectList aspectList = jarItem.getAspects(jar);
		if (aspectList != null && aspectList.size() == 1) {
			Aspect aspect = aspectList.getAspects()[0];
			amount = aspectList.getAmount(aspect);
			return new JarAspect(capacity, amount, aspect,jarItem);
		}
		return null;

	}

	@Override
	public void setRedstonePowered(boolean b) {
		super.setRedstonePowered(b);
		
		if (this.getRedstonePowered()) {
			//System.out.println("DBG: Redstone Powered");
			if (inventory == null)
				return;
			if (inventory.getStackInSlot(0) == ItemStack.EMPTY)
				return;
			if (!(inventory.getStackInSlot(0).getItem() instanceof IEssentiaContainerItem))
				return;

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
	}

	private boolean insertInHopper(ItemStack itemStackIn, TileEntityHopper hopper) {

		ItemStack itemStack = null;
		if (itemStackIn == ItemStack.EMPTY)
			return false;
		int itemStackInMetadata = itemStackIn.getMetadata();

		//System.out.println("DBG: Try to insert into Hopper:"+itemStackInMetadata);
		for (int i = 0; i < hopper.getSizeInventory(); ++i) {
			itemStack = hopper.getStackInSlot(i);
			if (itemStack != null && itemStack != ItemStack.EMPTY) {
				
				
				int quantity = itemStack.getCount();
				int maxQuantity = itemStack.getMaxStackSize();

				if (quantity < hopper.getInventoryStackLimit() && quantity < maxQuantity) {
					if (itemStack.getItem().equals(itemStackIn.getItem())
							&& itemStack.getMetadata() == itemStackInMetadata) {
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
		return item instanceof IEssentiaContainerItem;
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
		nbttagcompound.setTag("inventory", inventory.serializeNBT());
	}

	@Override
	public void readExtraNBT(NBTTagCompound nbttagcompound) {
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
					if (hoppered instanceof TileJarFillable || hoppered instanceof ITileJarFillable) {
						int remain = 0;
						boolean added = false;

						if (hoppered instanceof TileJarFillable) {
							TileJarFillable jar = (TileJarFillable) hoppered;
							AspectList aspectListDestiny = jar.getAspects();
							if (aspectListDestiny != null && (aspectListDestiny.size() == 0 || // is empty
									aspectListDestiny.size() == 1 && aspectListDestiny.getAspects() != null
									&& aspectListDestiny.getAspects()[0] == aspect)) {
								remain = jar.addToContainer(aspect, ammountToTransfer);
								added = true;
							}
						}
						if (hoppered instanceof ITileJarFillable) {
							ITileJarFillable jar = (ITileJarFillable) hoppered;
							AspectList aspectListDestiny = jar.getAspects();
							if (aspectListDestiny != null && (aspectListDestiny.size() == 0 || // is empty
									aspectListDestiny.size() == 1 && aspectListDestiny.getAspects() != null
									&& aspectListDestiny.getAspects()[0] == aspect)) {
								remain = jar.addToContainer(aspect, ammountToTransfer);
								added = true;
							}
						}
						if (added) {
							int amtToRemove = ammountToTransfer - remain;
							boolean hasToRemoveTag =false;
							if ((sourceAmmount-amtToRemove)==0) hasToRemoveTag=true;
							jarItem.setAspects(sourceJar, aspectList.remove(aspect, amtToRemove));
							if (hasToRemoveTag)sourceJar.setTagCompound(null);							
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
		if (jarAspect==null)return false;
		if (jarAspect.getAspect()==null)return true;//isEmpty
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
				if (hasToRemoveTag)internalJar.setTagCompound(null);							
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

		JarAspect jarAspect=this.getJarAspect();
		if (jarAspect==null) return false;
		
		for(EnumFacing dir : EnumFacing.HORIZONTALS) 
			if (face.equals(dir)) return true;
		
		return false;
	}

	@Override
	public boolean canInputFrom(EnumFacing face) {
		return isConnectable(face);
	}

	@Override
	public boolean canOutputTo(EnumFacing face) {
		return isConnectable(face);
	}

	@Override
	public void setSuction(Aspect aspect, int amount) {
		int internalSucction=0;
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
		if (hasToRemoveTag)internalJar.setTagCompound(null);							
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
		if (jarAspect.getAspect()==null)return true;
		return false;
	}

}
/*
 * public static class BakedModelCache { private static final Map<String,
 * IBakedModel> BAKED_MODELS = new HashMap<>(); static {
 * getBakedModel("block/funnel"); //getBakedModel("block/funnel_jar"); }
 * 
 * private static ResourceLocation ResourceLocationFromString(String location) {
 * return new ResourceLocation(LibMisc.MOD_ID + ":"+ location); } private static
 * IBakedModel storeBakedModel(String stringLocation) {
 * 
 * ResourceLocation modelLocation=ResourceLocationFromString(stringLocation);
 * IModel iModel; try { iModel = ModelLoaderRegistry.getModel(modelLocation);
 * IBakedModel bakedModel= iModel.bake( TRSRTransformation.identity(),
 * DefaultVertexFormats.BLOCK ,(Function<ResourceLocation, TextureAtlasSprite>)
 * location->{return
 * Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.
 * toString());}); if(bakedModel!=null) BAKED_MODELS.put(stringLocation,
 * bakedModel); return bakedModel; } catch (Exception e) { e.printStackTrace();
 * return null; }
 * 
 * } public static IBakedModel getBakedModel(String stringLocation) {
 * 
 * IBakedModel bakedModel= BAKED_MODELS.get(stringLocation); if
 * (bakedModel==null) bakedModel= storeBakedModel(stringLocation); return
 * bakedModel; }
 * 
 * }
 */