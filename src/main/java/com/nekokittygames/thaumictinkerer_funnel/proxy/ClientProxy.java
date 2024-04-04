/*
 * Copyright (c) 2020. Katrina Knight
 */

package com.nekokittygames.thaumictinkerer_funnel.proxy;

import static com.nekokittygames.thaumictinkerer_funnel.thaumictinkerer_funnel.instance;

import com.nekokittygames.thaumictinkerer_funnel.thaumictinkerer_funnel;
import com.nekokittygames.thaumictinkerer_funnel.client.rendering.tileentities.TileEntityFunnelRenderer;
import com.nekokittygames.thaumictinkerer_funnel.common.commands.CommandThaumicTinkererClient;
import com.nekokittygames.thaumictinkerer_funnel.common.tileentity.TileEntityFunnel;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client side proxy
 */

@Mod.EventBusSubscriber
public class ClientProxy extends ICommonProxy {


	@SideOnly(Side.CLIENT)
    public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFunnel.class, new TileEntityFunnelRenderer());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileElvenAvatar.class, new RenderTileElvenAvatar());
        //Shaders.initShaders();
    }
    
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		thaumictinkerer_funnel.modRegistry.registerModels(event);
	}
	

    @Override
    public void preInit(FMLPreInitializationEvent event) {
    	super.preInit(event);
        ClientCommandHandler.instance.registerCommand(new CommandThaumicTinkererClient());
    	registerRenderers();
    }
    
    @Override
    public void init(FMLInitializationEvent event) {
    	super.init(event);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiProxy());
    }

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);		
	}


    /**
     * Localize a string
     * @param translationKey unlocalised string
     * @param args arguments to the localisation
     * @return the string fully localised to current locale
     */
    @Override
    public String localize(String translationKey, Object... args) {
        return I18n.format(translationKey, args);
    }
}
