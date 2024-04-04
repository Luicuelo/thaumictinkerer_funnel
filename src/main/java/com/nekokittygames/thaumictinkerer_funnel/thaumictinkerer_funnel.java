//Copyright (c) 2020. Katrina Knight, Luis Cuesta 2024
package com.nekokittygames.thaumictinkerer_funnel;
import com.nekokittygames.thaumictinkerer_funnel.common.packets.PacketHandler;
import com.nekokittygames.thaumictinkerer_funnel.api.MobAspect;
import com.nekokittygames.thaumictinkerer_funnel.api.MobAspects;
import com.nekokittygames.thaumictinkerer_funnel.api.ThaumicTinkererAPI;
import com.nekokittygames.thaumictinkerer_funnel.common.blocks.RegistrationHandler;
import com.nekokittygames.thaumictinkerer_funnel.common.libs.LibMisc;
import com.nekokittygames.thaumictinkerer_funnel.common.misc.ThaumicTInkererCreativeTab;
import com.nekokittygames.thaumictinkerer_funnel.proxy.ICommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;

@Mod(modid = LibMisc.MOD_ID, name = LibMisc.MOD_NAME,
     version = LibMisc.MOD_VERSION, dependencies = LibMisc.MOD_DEPENDENCIES)
public class thaumictinkerer_funnel {
  public static Logger logger;

  public static RegistrationHandler modRegistry= new RegistrationHandler();
  private static CreativeTabs tab;

  @SidedProxy(serverSide =
                  "com.nekokittygames.thaumictinkerer_funnel.proxy.ServerProxy",
              clientSide =
                  "com.nekokittygames.thaumictinkerer_funnel.proxy.ClientProxy")
  public static ICommonProxy proxy;

  @Mod.Instance(LibMisc.MOD_ID) public static thaumictinkerer_funnel instance;

  public static CreativeTabs getTab() { return tab; }

  public static void setTab(CreativeTabs tab) { thaumictinkerer_funnel.tab = tab; }

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
      if (message.key.equalsIgnoreCase("addDislocateBlacklist") &&
          message.isStringMessage()) {
    	  ThaumicTinkererAPI.getDislocationBlacklist().add(
            message.getStringValue());
      }
      if (message.key.equalsIgnoreCase("addTabletBlacklist") &&
          message.isStringMessage()) {
    	  ThaumicTinkererAPI.getAnimationTabletBlacklist().add(
            message.getStringValue());
      }
      if (message.key.equalsIgnoreCase("addMobAspect") &&
          message.isNBTMessage()) {
        MobAspects.getAspects().put(
            EntityEndermite.class,
            new MobAspect(EntityEndermite.class, new AspectList()));
      }
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
