//Copyright (c) 2020. Katrina Knight, Luis Cuesta 2024
package es.luiscuesta.thaumictinkerer_funnel;
import org.apache.logging.log4j.Logger;

import es.luiscuesta.thaumictinkerer_funnel.common.blocks.RegistrationHandler;
import es.luiscuesta.thaumictinkerer_funnel.common.libs.LibMisc;
import es.luiscuesta.thaumictinkerer_funnel.common.misc.ThaumicTInkererCreativeTab;
import es.luiscuesta.thaumictinkerer_funnel.common.packets.PacketHandler;
import es.luiscuesta.thaumictinkerer_funnel.proxy.ICommonProxy;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;

@Mod(modid = LibMisc.MOD_ID, name = LibMisc.MOD_NAME,
     version = LibMisc.MOD_VERSION, dependencies = LibMisc.MOD_DEPENDENCIES)
public class Thaumictinkerer_funnel {
  public static Logger logger;

  public static RegistrationHandler modRegistry= new RegistrationHandler();
  private static CreativeTabs tab;

  @SidedProxy(serverSide =
                  "es.luiscuesta.thaumictinkerer_funnel.proxy.ServerProxy",
              clientSide =
                  "es.luiscuesta.thaumictinkerer_funnel.proxy.ClientProxy")
  public static ICommonProxy proxy;

  @Mod.Instance(LibMisc.MOD_ID) public static Thaumictinkerer_funnel instance;

  public static CreativeTabs getTab() { return tab; }

  public static void setTab(CreativeTabs tab) { Thaumictinkerer_funnel.tab = tab; }

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    tab = new ThaumicTInkererCreativeTab();
    logger = event.getModLog();

    proxy.preInit(event);
    PacketHandler.registerMessages(LibMisc.MOD_ID);
  }  

  @Mod.EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
   
  }

  @EventHandler
  public void processIMC(FMLInterModComms.IMCEvent event) {
    for (FMLInterModComms.IMCMessage message : event.getMessages()) {

    }
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    proxy.init(event);
    ResearchCategories.registerCategory(
        "THAUMIC_TINKERER", null, new AspectList(),
        new ResourceLocation("thaumictinkerer_funnel",
                             "textures/items/share_book.png"),
        new ResourceLocation("thaumictinkerer_funnel", "textures/misc/sky1.png"),
        new ResourceLocation("thaumictinkerer_funnel", "textures/misc/sky1.png"));
    ThaumcraftApi.registerResearchLocation(
        new ResourceLocation("thaumictinkerer_funnel", "research/misc"));
    ThaumcraftApi.registerResearchLocation(
        new ResourceLocation("thaumictinkerer_funnel", "research/baubles"));
    ThaumcraftApi.registerResearchLocation(
        new ResourceLocation("thaumictinkerer_funnel", "research/machines"));
    ThaumcraftApi.registerResearchLocation(
        new ResourceLocation("thaumictinkerer_funnel", "research/foci"));

    // IDustTrigger.registerDustTrigger(ModBlocks.osmotic_enchanter);
  }

}
