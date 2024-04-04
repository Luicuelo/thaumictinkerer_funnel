package com.nekokittygames.thaumictinkerer_funnel.common.crafting;
import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class VisCrystalFactory implements IRecipeFactory
{

    @Override
    public IRecipe parse(JsonContext jsonContext, JsonObject jsonObject) {

        // Get Primer
        //final CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
        // ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(jsonObject, "result"), jsonContext);
        return null;
    }
}
