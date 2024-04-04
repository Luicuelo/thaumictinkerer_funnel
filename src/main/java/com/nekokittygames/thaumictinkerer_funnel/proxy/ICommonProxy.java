package com.nekokittygames.thaumictinkerer_funnel.proxy;

import com.nekokittygames.thaumictinkerer_funnel.thaumictinkerer_funnel;
import com.nekokittygames.thaumictinkerer_funnel.common.blocks.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public abstract class ICommonProxy {


    String localize(String translationKey, Object... args) {
		return null;
	}
    
	public void preInit(FMLPreInitializationEvent e) {
		ModBlocks.init();
		//ModItems.init();
		//ModDimensions.init();		
		//ModSimpleNetworkChannel.registerMessages();
	}
	
	public void init(FMLInitializationEvent e) {		
	}

	public void postInit(FMLPostInitializationEvent e) {		
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		thaumictinkerer_funnel.modRegistry.registerBlocks(event);
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		thaumictinkerer_funnel.modRegistry.registerItems(event);
		//ModBlocks.registerTileEntities();
	}
	
	

}
