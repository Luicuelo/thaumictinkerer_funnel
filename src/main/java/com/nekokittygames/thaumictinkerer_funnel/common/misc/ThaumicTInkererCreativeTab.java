package com.nekokittygames.thaumictinkerer_funnel.common.misc;

import com.nekokittygames.thaumictinkerer_funnel.common.blocks.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;


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

