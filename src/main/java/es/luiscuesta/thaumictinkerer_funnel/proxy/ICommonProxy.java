package es.luiscuesta.thaumictinkerer_funnel.proxy;

import es.luiscuesta.thaumictinkerer_funnel.Thaumictinkerer_funnel;
import es.luiscuesta.thaumictinkerer_funnel.common.blocks.BlockTileEntity;
import es.luiscuesta.thaumictinkerer_funnel.common.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public abstract class ICommonProxy{


    abstract String localize(String translationKey, Object... args) ;
    
	public void preInit(FMLPreInitializationEvent e) {
		ModBlocks.init();
		//ModItems.init();
		//ModDimensions.init();		
		//ModSimpleNetworkChannel.registerMessages();
	}
	
	public abstract void init(FMLInitializationEvent e);		
	

	public abstract void postInit(FMLPostInitializationEvent e);

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		Thaumictinkerer_funnel.modRegistry.registerBlocks(event);
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		Thaumictinkerer_funnel.modRegistry.registerItems(event);
	}
	
	@SubscribeEvent
    public static  void onBlockPlaced(BlockEvent.PlaceEvent event) {
		if (event.getWorld().isRemote) return;
	    try {	
	    	ItemStack stack = event.getItemInHand(); 	        
	        Block block = event.getPlacedBlock().getBlock(); 
	        if (block!=null&&block instanceof BlockTileEntity) {
	        	if(stack!=null)((BlockTileEntity<?>)block).onBlockPlaced(event.getWorld(),event.getPos(), stack);
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}  
	

}
