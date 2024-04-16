package es.luiscuesta.thaumictinkerer_funnel.common.misc;

import javax.annotation.Nonnull;

import es.luiscuesta.thaumictinkerer_funnel.common.blocks.ModBlocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;


public class ThaumicTInkererCreativeTab extends CreativeTabs {
    public ThaumicTInkererCreativeTab() {
        super("thaumictinkerer_funnel");
    }
   
    
    @Nonnull
    public ItemStack createIcon() {
    	return new ItemStack(ModBlocks.funnel);
    }

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(ModBlocks.funnel);
	}
		
}

