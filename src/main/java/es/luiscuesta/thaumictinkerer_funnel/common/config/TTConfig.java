package es.luiscuesta.thaumictinkerer_funnel.common.config;


import es.luiscuesta.thaumictinkerer_funnel.common.libs.LibMisc;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = LibMisc.MOD_ID)
@Config.LangKey("thaumictinkerer_funnel.config.title")
public class TTConfig {
    @Config.Comment("This is the amount of essentia the funnel try to move")
    @Config.Name("Funnel Speed")
    @Config.LangKey("thaumictinkerer_funnel.config.funnel")
    @Config.RangeInt(min = 1, max = 100)
    public static int funnelSpeed = 20;
    
    @Config.Comment("If true, automatic jar swapping ,with essentia meter, is disabled.")
    @Config.Name("Disable Jar Swapping")
    @Config.LangKey("thaumictinkerer_funnel.config.jarswapping")   
    public static boolean disableJarSwapping=false;

    @Mod.EventBusSubscriber
    private static class EventHandler {

        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(LibMisc.MOD_ID)) {
                ConfigManager.sync(LibMisc.MOD_ID, net.minecraftforge.common.config.Config.Type.INSTANCE);
            }
        }
    }
}

