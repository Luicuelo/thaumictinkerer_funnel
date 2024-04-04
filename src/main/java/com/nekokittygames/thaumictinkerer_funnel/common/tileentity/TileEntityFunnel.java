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
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.tiles.essentia.TileJarFillable;

public class TileEntityFunnel extends TileEntityThaumicTinkerer
		implements IAspectContainer, ITickable, ITileJarFillable {

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
	}

	public boolean isTitleTick() {
		return((ticksElapsed%TITLE_TICK==1));
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
		
		return 0;
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

		if (ItemCapacityDictionary.getCapacity(jarName) != null) {
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

		ticksElapsed++;
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
	public boolean doesContainerAccept(Aspect aspect) {
		return false;
	}

	@Override
	public boolean takeFromContainer(Aspect aspect, int i) {
		return false;
	}

	@Override
	public boolean takeFromContainer(AspectList aspectList) {
		return false;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect aspect, int i) {
		return false;
	}

	@Override
	public boolean doesContainerContain(AspectList aspectList) {
		return false;
	}

	@Override
	public int containerContains(Aspect aspect) {
		return 0;
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