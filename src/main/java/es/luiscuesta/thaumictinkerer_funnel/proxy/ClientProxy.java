/*
 * Copyright (c) 2020. Katrina Knight
 */

package es.luiscuesta.thaumictinkerer_funnel.proxy;
import es.luiscuesta.thaumictinkerer_funnel.Thaumictinkerer_funnel;
import es.luiscuesta.thaumictinkerer_funnel.client.rendering.TileEntityEssentiaMeterRenderer;
import es.luiscuesta.thaumictinkerer_funnel.client.rendering.TileEntityFunnelRenderer;
import es.luiscuesta.thaumictinkerer_funnel.common.libs.LibMisc;
import es.luiscuesta.thaumictinkerer_funnel.common.tileentity.TileEntityEssentiaMeter;
import es.luiscuesta.thaumictinkerer_funnel.common.tileentity.TileEntityFunnel;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Client side proxy
 */

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends ICommonProxy {

	public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFunnel.class, new TileEntityFunnelRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEssentiaMeter.class, new TileEntityEssentiaMeterRenderer());
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
    	super.preInit(event);
    	registerRenderers();
    }
    
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		Thaumictinkerer_funnel.modRegistry.registerModels(event);
	}
	
	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent.Pre event) {

		 	TextureStitchEvent.Pre textures =(TextureStitchEvent.Pre) event ;
		 	ResourceLocation location;
		 	
		 	location=new ResourceLocation(LibMisc.MOD_ID,"blocks/funnel/jar_side");
            textures.getMap().registerSprite(location);
            
            location=new ResourceLocation(LibMisc.MOD_ID,"blocks/funnel/jar_bottom");
            textures.getMap().registerSprite(location);
            
            location=new ResourceLocation(LibMisc.MOD_ID,"blocks/funnel/jar_top");
            textures.getMap().registerSprite(location);            
	 }
	
    
    @Override
    public String localize(String translationKey, Object... args) {
        return I18n.format(translationKey, args);
    }

	@Override
	public void init(FMLInitializationEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		// TODO Auto-generated method stub
		
	}
    

}
