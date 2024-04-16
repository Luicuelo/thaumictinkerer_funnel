package es.luiscuesta.thaumictinkerer_funnel.common.tileentity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.essentia.TileJarFillable;

public  class ItemCapacityDictionary {
	private static final Map<String, Integer> ITEM_CAPACITY_MAP = new HashMap<>();

	static {
		ITEM_CAPACITY_MAP.put("thaumcraft:jar_normal", 250);

	}

	private static Integer getCapacity(String itemName) {
		return ITEM_CAPACITY_MAP.get(itemName);
	}

	private static void putCapacity(String nombre, Integer capacidad) {
		ITEM_CAPACITY_MAP.put(nombre, capacidad);
	}
	
	public static int getCapcityFromJar(ItemStack jar) {
		String jarName = jar.getItem().getRegistryName().toString();
		
		int capacity = 0;
		Integer cachedCapacity=getCapacity(jarName);
		if (cachedCapacity != null) {
			capacity = getCapacity(jarName);
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
					putCapacity(jarName, capacity);
				}
			}
		}
		return capacity;
	}
}
