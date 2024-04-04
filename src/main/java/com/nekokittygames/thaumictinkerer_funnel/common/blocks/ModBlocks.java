package com.nekokittygames.thaumictinkerer_funnel.common.blocks;

import com.nekokittygames.thaumictinkerer_funnel.common.libs.LibMisc;

import net.minecraftforge.fml.common.registry.GameRegistry;


@GameRegistry.ObjectHolder(LibMisc.MOD_ID)
public class ModBlocks {
	public static final BlockFunnel funnel = new BlockFunnel();	
	public static void init() {

	}
   
}
