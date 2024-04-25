//Copyright (c) 2020. Katrina Knight, Luis Cuesta 2024
package es.luiscuesta.thaumictinkerer_funnel;
import org.apache.logging.log4j.Logger;

import es.luiscuesta.thaumictinkerer_funnel.common.blocks.RegistrationHandler;
import es.luiscuesta.thaumictinkerer_funnel.common.libs.LibMisc;
import es.luiscuesta.thaumictinkerer_funnel.common.misc.ThaumicTInkererCreativeTab;
import es.luiscuesta.thaumictinkerer_funnel.common.packets.PacketHandler;
import es.luiscuesta.thaumictinkerer_funnel.proxy.ICommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = LibMisc.MOD_ID, name = LibMisc.MOD_NAME,
     version = LibMisc.MOD_VERSION, dependencies = LibMisc.MOD_DEPENDENCIES)
public class Thaumictinkerer_funnel {
	
@Mod.Instance(LibMisc.MOD_ID) public static Thaumictinkerer_funnel instance;	


@SidedProxy(clientSide="es.luiscuesta.thaumictinkerer_funnel.proxy.ClientProxy", serverSide="es.luiscuesta.thaumictinkerer_funnel.proxy.ServerProxy")
  public static ICommonProxy commonProxy=null;

 
  public static Logger logger;
  public static RegistrationHandler modRegistry= new RegistrationHandler();
  private static CreativeTabs tab;
  



  public static CreativeTabs getTab() { return tab; }

  public static void setTab(CreativeTabs tab) { Thaumictinkerer_funnel.tab = tab; }

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    tab = new ThaumicTInkererCreativeTab();
    logger = event.getModLog();
 	commonProxy.preInit(event);
    PacketHandler.registerMessages(LibMisc.MOD_ID);
  }  
  
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
			if (commonProxy!=null) commonProxy.init(event);

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (commonProxy!=null) commonProxy.postInit(event);

		MinecraftForge.EVENT_BUS.register(this);
	}	
	
    @Mod.EventHandler
    public void init(FMLServerStartingEvent event)
    {
      logger.info("initalise FMLServerStartingEvent :" + LibMisc.MOD_NAME);
      //event.registerServerCommand(new HandCommand());
    }
    

  @Mod.EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
   
  }




}
