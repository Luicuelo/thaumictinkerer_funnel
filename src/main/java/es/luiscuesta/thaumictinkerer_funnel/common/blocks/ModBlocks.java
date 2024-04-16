package es.luiscuesta.thaumictinkerer_funnel.common.blocks;

import es.luiscuesta.thaumictinkerer_funnel.common.libs.LibMisc;

import net.minecraftforge.fml.common.registry.GameRegistry;


@GameRegistry.ObjectHolder(LibMisc.MOD_ID)
public class ModBlocks {
	public static final BlockFunnel funnel = new BlockFunnel();
	public static final BlockEssenceMeter essenceMeter = new BlockEssenceMeter();	
	public static void init() {//ensures all objects has to be created.
		
	}
}
