package es.luiscuesta.thaumictinkerer_funnel.common.recipes;

import java.util.ArrayList;
import java.util.List;

import es.luiscuesta.thaumictinkerer_funnel.common.tileentity.JarCapacityDictionary;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;


public class RecipeCombineEssentia extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	
	@Override
	public boolean isDynamic() {
	    return true;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		return getJarsFromInventoryCrafting(inv) != null;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack jar1 , jar2 ;		
		List<ItemStack> jars=getJarsFromInventoryCrafting(inv);
		if (jars == null) return ItemStack.EMPTY;		
		jar1=jars.get(0);
		jar2=jars.get(1);
				
		
		int total = getAmount(jar1) + (getAmount(jar2)*jar2.getCount());
		int capacity = geCapacity(jar1);

		ItemStack result = jar1.copy();
		 
		seAmount(result,jar2, (total >= capacity)?capacity:total);
		return result;
	}
	
	
	// The most complex case is:
	// there are leftover phials/jars, and one hasn't been completely consumed
	// we need to return the leftovers in slot 0, the empty ones in slot 1, and the remainder in slot 2

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		ItemStack jar1 , jar2 ;
		List<ItemStack> jars=getJarsFromInventoryCrafting(inv);
		if (jars == null) return  remaining;	
		
		jar1=jars.get(0);
		jar2=jars.get(1);

		int jar2UnitaryAmount=getAmount(jar2);
		int jar2Count=jar2.getCount();
		int total = getAmount(jar1) + (jar2UnitaryAmount*jar2Count);
		
		int capacity = geCapacity(jar1);
		int extraAmount = total - capacity;		
		
		
		if (jar2.getItem()==ItemsTC.crystalEssence) {
			jar2.setCount(extraAmount+1);		
			return remaining;//a crystall cant be empty
		}
		
		ItemStack result = jar2.copy();
		
		NBTTagCompound tag = jar2.getTagCompound().copy();
		NBTTagList aspects = tag.getTagList("Aspects", 10);
		NBTTagCompound aspectTag = aspects.getCompoundTagAt(0);
		int exceed=0;
		
			if (total > capacity) {				
				if (jar2Count==1) {
					aspectTag.setInteger("amount", extraAmount);
				}else {
					int jar2Toreturn= extraAmount/jar2UnitaryAmount;
					exceed=extraAmount-(jar2Toreturn*jar2UnitaryAmount);					
					result.setCount(jar2Toreturn);			
					
					if (exceed>0) {
						ItemStack result2 = jar2.copy();
						result2.setCount(1);
						NBTTagCompound tag2 = jar2.getTagCompound().copy();
						NBTTagList aspects2 = tag2.getTagList("Aspects", 10);
						NBTTagCompound aspectTag2 = aspects2.getCompoundTagAt(0);
						aspectTag2.setInteger("amount", exceed);	
						result2.setTagCompound(tag2);
						remaining.set(2, result2);
					}
					 ItemStack usedItems = jar2.copy();
					 usedItems.setCount(jar2Count-jar2Toreturn-(exceed>0?1:0));
					 //usedItems.getTagCompound().removeTag("Aspects");
					 if (usedItems.getItem()==ItemsTC.phial) {
						 usedItems.setTagCompound(null);
						 usedItems.setItemDamage(0);
					 }
					 else usedItems.setTagCompound(new NBTTagCompound());
					 remaining.set(1, usedItems);
				}
			} else {		
				//tag.removeTag("Aspects");
				if (result.getItem()==ItemsTC.phial) {
					tag=null;
					result.setItemDamage(0);
				}
				else tag=new NBTTagCompound();
			}		
			
		jar2.setCount(0);			
		result.setTagCompound(tag);
		remaining.set(0, result);
		
		return remaining;
	}

	private void seAmount(ItemStack jar,ItemStack other, int amount) {
		
		NBTTagCompound tag;
		if(jar.getTagCompound()==null) {
			tag=other.getTagCompound().copy();
			if (jar.getItem()==ItemsTC.phial && amount>0)jar.setItemDamage(1); 			
		}
		else tag = jar.getTagCompound().copy();			
		NBTTagList aspects;
		if (tag.hasKey("Aspects")) {
			aspects = tag.getTagList("Aspects", 10);
		}else {
			aspects = other.getTagCompound().copy().getTagList("Aspects", 10);
			//add aspects tag from other
			tag.setTag("Aspects", aspects);				
		}				
		NBTTagCompound aspectTag = aspects.getCompoundTagAt(0);		
		aspectTag.setInteger("amount", amount);
		jar.setTagCompound(tag);
	}
	
	private  List<ItemStack> getJarsFromInventoryCrafting(InventoryCrafting inv){
	    List<ItemStack> listToReturn = new ArrayList<>();
		ItemStack jar1 = null, jar2 = null;		
		
		int counter=0;			
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty())counter++;
			//if (!stack.isEmpty() && jar1 == null && stack.getItem() == Item.getItemFromBlock(BlocksTC.jarNormal)) 
			if (!stack.isEmpty() && jar1 == null && stack.getItem() instanceof IEssentiaContainerItem)
			{
				jar1 = stack;
				continue;
			}
			if (!stack.isEmpty() && jar1 != null && stack.getItem() instanceof IEssentiaContainerItem)
				jar2 = stack;
		}
		
		if(counter!=2)return null;
		if (jar1 == null || jar2 == null) return null;
		if(jar1.getCount()>1) return null; // the destiny has to be single
		if (getSameAspect(jar1, jar2) == null) return null;
		String aspect2 = getAspectKey(jar2);
		if (aspect2==null)return null; //the second cant be empty
	    
		int capacity; 
		
		capacity= geCapacity(jar1);
		if (getAmount(jar1)==capacity) return null; //jar1 is full, no need to combine.
		
		listToReturn.add(jar1);
		listToReturn.add(jar2);
	    
	    
	    return listToReturn;
	    
	}
	
	private int geCapacity(ItemStack stack)
	{
		if (stack==null) return 0;
		Item essentiaContainer=stack.getItem();
		if(!(essentiaContainer instanceof IEssentiaContainerItem)) return 0;
		//check if item is a phial, return 10 if is a phial   
		if (essentiaContainer==ItemsTC.phial) return 10;
		if (essentiaContainer==ItemsTC.crystalEssence) return 1;
		return JarCapacityDictionary.getCapcityFromJar(stack);
		
	}
	private String getSameAspect(ItemStack jar1, ItemStack jar2) {
		String aspect1 = getAspectKey(jar1);
		String aspect2 = getAspectKey(jar2);
		
		if (aspect1 == null && aspect2 == null) return null;
		if (aspect1 !=null && aspect2 != null && !aspect1.equals(aspect2)) return null;		
		return aspect1 == null ? aspect2 : aspect1;
	}
	
	

	private int getAmount(ItemStack stack) {
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Aspects"))
			return 0;
		NBTTagList aspects = stack.getTagCompound().getTagList("Aspects", 10);
		return aspects.tagCount() > 0 ? aspects.getCompoundTagAt(0).getInteger("amount") : 0;
	}

	private String getAspectKey(ItemStack stack) {
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Aspects"))
			return null;
		NBTTagList aspects = stack.getTagCompound().getTagList("Aspects", 10);
		return aspects.tagCount() > 0 ? aspects.getCompoundTagAt(0).getString("key") : null;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(BlocksTC.jarNormal);
	}
}