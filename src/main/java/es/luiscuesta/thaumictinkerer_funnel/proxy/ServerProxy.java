package es.luiscuesta.thaumictinkerer_funnel.proxy;

import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@SuppressWarnings("deprecation")
public class ServerProxy extends ICommonProxy {


    @Override
    public void preInit(FMLPreInitializationEvent event) {
    	super.preInit(event);
    }
    
    @Override
    public void init(FMLInitializationEvent event) {
		super.init(event);
    }

    
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
    
    
    @Override
    public String localize(String translationKey, Object... args) {
        return I18n.translateToLocalFormatted(translationKey, args);
    }



}
