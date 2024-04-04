package com.nekokittygames.thaumictinkerer_funnel.common.recipes;

import com.nekokittygames.thaumictinkerer_funnel.common.blocks.ModBlocks;
import com.nekokittygames.thaumictinkerer_funnel.common.items.ModItems;
import com.nekokittygames.thaumictinkerer_funnel.common.libs.LibRecipes;
import com.nekokittygames.thaumictinkerer_funnel.common.libs.LibResearch;
import com.nekokittygames.thaumictinkerer_funnel.common.recipes.ing.TTFocusIngredient;
import com.nekokittygames.thaumictinkerer_funnel.common.recipes.ing.TTIngredientNBT;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.crafting.*;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.misc.BlockNitor;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.resources.ItemCrystalEssence;

import java.util.Objects;

import static thaumcraft.api.ThaumcraftApi.*;

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
        //addArcaneCraftingRecipe(LibRecipes.FUNNEL, new ShapedArcaneRecipe(defaultGroup, LibResearch.ESSENTIA_FUNNEL, 60, new AspectList().add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), new ItemStack(Objects.requireNonNull(ModBlocksRegistry.funnel)), "STS", 'S', Blocks.STONE, 'T', "ingotThaumium"));
        // Magnets
        ItemStack airCrystal=new ItemStack(ItemsTC.crystalEssence);
        ((ItemCrystalEssence)ItemsTC.crystalEssence).setAspects(airCrystal,new AspectList().add(Aspect.AIR,1));
        ItemStack earthCrystal=new ItemStack(ItemsTC.crystalEssence);
        ((ItemCrystalEssence)ItemsTC.crystalEssence).setAspects(earthCrystal,new AspectList().add(Aspect.EARTH,1));
        ItemStack focus=new ItemStack(ItemsTC.focus1);
        FocusPackage focusPackage=new FocusPackage();
        ItemFocus.setPackage(focus,focusPackage);
        
    }

    private static void initializeInfusionRecipes() {
        // Empty for now
     }
}
