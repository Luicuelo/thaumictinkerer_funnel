package com.nekokittygames.thaumictinkerer_funnel.common.tileentity;

import com.nekokittygames.thaumictinkerer_funnel.common.libs.LibMisc;
import com.nekokittygames.thaumictinkerer_funnel.common.misc.SingleItemStackHandler;
import com.nekokittygames.thaumictinkerer_funnel.common.tileentity.TileEntityFunnel.JarAspect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.tiles.essentia.TileJarFillable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class TileEntityFunnel extends TileEntityThaumicTinkerer implements IAspectContainer, ITickable {

	
	private static  class ItemCapacityDictionary {
	    private static final  Map<String, Integer> ITEM_CAPACITY_MAP = new HashMap<>();

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

	/*
	public static  class BakedModelCache {
	    private static final  Map<String, IBakedModel> BAKED_MODELS = new HashMap<>();
	    static {
	    	getBakedModel("block/funnel");
	    	//getBakedModel("block/funnel_jar");	    	
	    }
	    
		private static ResourceLocation ResourceLocationFromString(String location) {
			return new ResourceLocation(LibMisc.MOD_ID + ":"+ location);
		}
	    private static IBakedModel storeBakedModel(String stringLocation) {
	    	
	    	ResourceLocation modelLocation=ResourceLocationFromString(stringLocation);
	    	IModel iModel;
			try {
				iModel = ModelLoaderRegistry.getModel(modelLocation);
				IBakedModel bakedModel= iModel.bake( TRSRTransformation.identity(), DefaultVertexFormats.BLOCK
						,(Function<ResourceLocation, TextureAtlasSprite>)  location->{return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());});			
				if(bakedModel!=null) BAKED_MODELS.put(stringLocation, bakedModel);
				return bakedModel;
			} catch (Exception e) {				
				e.printStackTrace();
				return null;
			}
	    			
	    }
	    public static IBakedModel getBakedModel(String stringLocation) {
	    	
	    	IBakedModel bakedModel= BAKED_MODELS.get(stringLocation);
	    	if (bakedModel==null) 
	    		bakedModel= storeBakedModel(stringLocation);
	    	return bakedModel;
	    }
    
	}
	*/
	
	public TileEntityFunnel() {	
		super();
	}
	
		public class JarAspect{
		
		private int _capacity;
		private int _amount;
		private Aspect _aspect;
		public int getCapacity() {
			return _capacity;
		}
		public int getAmount() {
			return _amount;
		}
		public Aspect getAspect() {
			return _aspect;
		}
		public JarAspect (int capacity, int amount, Aspect aspect) {
			_capacity=capacity;
			_amount=amount;
			_aspect=aspect;
		}

		
	}
	
    private int lastComparatorSignal=0;
    private int speed = 1;

    
	public int comparatorSignal() {
		
		JarAspect jarAspect=getJarAspect();
		int signal=0;
		int ammount=0;
		int capacity=0;
		
		if(jarAspect!=null) {
			ammount=jarAspect.getAmount();
			capacity=jarAspect.getCapacity();		
			signal=(int) Math.ceil((double)(ammount*15d)/(double)capacity);			
		}
		
		//if (jarAspect!=null) System.out.println(jarAspect.getAspect().getName()+" Amount:"+ammount+" Capadity:"+capacity);
		if (lastComparatorSignal!=signal) {
			 //System.out.println(" Signal change from:"+lastComparatorInputOverride+" to:"+signal);
			lastComparatorSignal=signal;
			sendUpdates();			
		}
		return signal;
	}

	public JarAspect getJarAspect() {
		int amount=0;
		if (inventory == null) return null;
		if (inventory.getStackInSlot(0) == ItemStack.EMPTY) return null;
		if (!(inventory.getStackInSlot(0).getItem() instanceof IEssentiaContainerItem)) return null;
		ItemStack jar=inventory.getStackInSlot(0);
		IEssentiaContainerItem jarItem=(IEssentiaContainerItem) jar.getItem();		
		if(jarItem.getAspects(jar) == null ||jarItem.getAspects(jar).size()==0) return null;	
		
		
		String jarName=jar.getItem().getRegistryName().toString();
		int capacity = 0;
		
		if (ItemCapacityDictionary.getCapacity(jarName)!=null) {
			capacity=ItemCapacityDictionary.getCapacity(jarName);
			//System.out.println(" Jar:"+jarName+" found, capacity:"+capacity);
		}
		
		else {
			
			//System.out.println(" Jar:"+jarName+" Not found,");
			Block blockjar = Block.getBlockFromItem(jar.getItem());		    			
			if (blockjar instanceof ITileEntityProvider) {
				TileEntity tileEntityJar = ((ITileEntityProvider)blockjar).createNewTileEntity(null, 0);
				if (tileEntityJar instanceof TileJarFillable) {
										
			        try {
			            Method method = tileEntityJar.getClass().getDeclaredMethod("getCapacity");
			            method.setAccessible(true); 
			            capacity =  (int) method.invoke(tileEntityJar);
			        } catch (NoSuchMethodException | IllegalAccessException| IllegalArgumentException | InvocationTargetException  e) {
			        	//e.printStackTrace();
			        	capacity=((TileJarFillable)tileEntityJar).CAPACITY;
			        } 
    				//System.out.println(" Jar:"+jarName+" put in dictionary, capacity:"+capacity);
    				ItemCapacityDictionary.putCapacity(jarName, capacity);
    			}
			}
		}

		AspectList aspectList = jarItem.getAspects(jar);
        if (aspectList != null && aspectList.size() == 1) {
           Aspect aspect = aspectList.getAspects()[0];            	
           amount=aspectList.getAmount(aspect);	
           return new JarAspect(capacity, amount, aspect);
        }                
		return null;
		
	}
	
	@Override
	 public void setRedstonePowered(boolean b) {
		super.setRedstonePowered(b);
		if(b) {
			if (inventory == null) return;
			if (inventory.getStackInSlot(0) == ItemStack.EMPTY) return;
			if (!(inventory.getStackInSlot(0).getItem() instanceof IEssentiaContainerItem)) return;
			
			ItemStack jar=inventory.getStackInSlot(0);
			TileEntity hopper = world.getTileEntity(pos.down());
            if (hopper != null && hopper instanceof TileEntityHopper) {
            	if(insertInHopper(jar,(TileEntityHopper)hopper)) {
            		inventory.extractItem(0, 1, false);
            		if (inventory.getStackInSlot(0).isEmpty()) {            			
            			inventory.setStackInSlot(0, ItemStack.EMPTY);
            		}
            	}
            }	
		}
	 }
	
	private boolean insertInHopper (ItemStack itemStackIn, TileEntityHopper hopper) {

		 ItemStack itemStack=null;		 
		 if(itemStackIn==ItemStack.EMPTY) return false;
		 int itemStackInMetadata=itemStackIn.getMetadata();
		 
		 for (int i = 0; i < hopper.getSizeInventory(); ++i) {		     
			 itemStack = hopper.getStackInSlot(i);
			 if (itemStack!=null && itemStack!=ItemStack.EMPTY) {
				 int quantity=itemStack.getCount();
				 int maxQuantity=itemStack.getMaxStackSize();
						 
				 if (quantity<hopper.getInventoryStackLimit()&& quantity<maxQuantity) {
					 if (itemStack.getItem().equals(itemStackIn.getItem())&& itemStack.getMetadata()==itemStackInMetadata) {
						 itemStack.setCount(quantity+1);
						 if (itemStackIn.getCount()-1<=0) itemStackIn.setCount(0);
						 else {
							 itemStackIn.shrink(1);
						 }
						 hopper.setInventorySlotContents(i, itemStack);
						 return true;
					 }					 
				 }	 
			 }
		 }
		 
		 //if (ItemStack.areItemStacksEqual(titemStack, titemStackIn)) {//compara tambien los nameTags
		 
		 for (int i = 0; i < hopper.getSizeInventory(); ++i) {	
			 itemStack = hopper.getStackInSlot(i);
			 if (itemStack==null || itemStack==ItemStack.EMPTY) {
				 itemStack=itemStackIn.copy();
				 itemStack.setCount(1);
				 hopper.setInventorySlotContents(i, itemStack);
				 if (itemStackIn.getCount()-1<=0) itemStackIn.setCount(0);
				 else{
					 itemStackIn.shrink(1);
				 }
				 return true;
			 }
		 }
		 
		 return false;
	}
	
	
    private SingleItemStackHandler inventory = new SingleItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            comparatorSignal();//Updates if needed
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
    };

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
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
        else
            return super.hasCapability(capability, facing);
    }


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
        if (inventory != null && (inventory.getStackInSlot(0) != ItemStack.EMPTY) && (((IEssentiaContainerItem) inventory.getStackInSlot(0).getItem()).getAspects(inventory.getStackInSlot(0)) != null) && (((IEssentiaContainerItem) inventory.getStackInSlot(0).getItem()).getAspects(inventory.getStackInSlot(0)).size() > 0) && !world.isRemote) {
            IEssentiaContainerItem item = (IEssentiaContainerItem) inventory.getStackInSlot(0).getItem();
            AspectList aspectList = item.getAspects(inventory.getStackInSlot(0));
            if (aspectList != null && aspectList.size() == 1) {
                Aspect aspect = aspectList.getAspects()[0];
                TileEntity tile = world.getTileEntity(pos.down());
                if (tile != null && tile instanceof TileEntityHopper) {
                    TileEntity hoppered = getHopperFacing(tile.getPos(), tile.getBlockMetadata());
                    if (hoppered instanceof TileJarFillable) {
                        TileJarFillable jar = (TileJarFillable) hoppered;
                        AspectList JarAspects = jar.getAspects();

                        if (JarAspects != null && JarAspects.size() == 0 && (jar.aspectFilter == null || jar.aspectFilter == aspect) || Objects.requireNonNull(JarAspects).getAspects()[0] == aspect) {
                            int remain = jar.addToContainer(aspect, speed);
                            int amt = speed - remain;
                            item.setAspects(inventory.getStackInSlot(0), aspectList.remove(aspect, amt));
                            comparatorSignal();//Updates if needed

                        }
                    }
                }
            }
        }
    }

    @Override
    public AspectList getAspects() {
        if (inventory.getStackInSlot(0) != ItemStack.EMPTY && ((IEssentiaContainerItem) inventory.getStackInSlot(0).getItem()).getAspects(inventory.getStackInSlot(0)) != null && ((IEssentiaContainerItem) inventory.getStackInSlot(0).getItem()).getAspects(inventory.getStackInSlot(0)).size() > 0) {
            return ((IEssentiaContainerItem) inventory.getStackInSlot(0).getItem()).getAspects(inventory.getStackInSlot(0));
        } else
            return null;
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
    public int addToContainer(Aspect aspect, int i) {
        return 0;
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
