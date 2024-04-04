package com.nekokittygames.thaumictinkerer_funnel.common.recipes;

import com.nekokittygames.thaumictinkerer_funnel.common.blocks.ModBlocks;
import com.nekokittygames.thaumictinkerer_funnel.common.libs.LibRecipes;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;


public class ModRecipes {


    private static final ResourceLocation defaultGroup = new ResourceLocation("");


    public static void initializeRecipes(IForgeRegistry<IRecipe> registry) {
        registerOreDict();
        initializeCraftingRecipes(registry);
        initializeCauldronRecipes();
        initializeArcaneRecipes();
        initializeInfusionRecipes();
        initializeMultiblockRecipes();

    }

    private static void registerOreDict() {
      
    }

    private static void initializeMultiblockRecipes() {

 
    }
    private static void initializeCauldronRecipes() {
      }

    private static void initializeCraftingRecipes(IForgeRegistry<IRecipe> registry) {

    }

    private static void initializeArcaneRecipes() {

    	AspectList aspectList=new AspectList().add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1);
    	ShapedArcaneRecipe recipe=new ShapedArcaneRecipe(defaultGroup, "", 60, aspectList, new ItemStack(ModBlocks.funnel), "STS"," S ", 'S', Blocks.STONE, 'T', "ingotThaumium");
    	thaumcraft.api.ThaumcraftApi.addArcaneCraftingRecipe(LibRecipes.FUNNEL, recipe);
    }

    private static void initializeInfusionRecipes() {
        // Empty for now
     }
}
