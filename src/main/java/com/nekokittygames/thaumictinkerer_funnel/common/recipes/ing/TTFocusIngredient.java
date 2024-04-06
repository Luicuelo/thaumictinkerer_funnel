package com.nekokittygames.thaumictinkerer_funnel.common.recipes.ing;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.common.items.casters.ItemFocus;

public class TTFocusIngredient extends Ingredient {
    private Class<? extends FocusEffect> effect;
    public TTFocusIngredient(Class<? extends FocusEffect> effect,ItemStack... ingredient) {
        super(ingredient);
        this.effect=effect;
    }

    @Override
    public boolean apply(@Nullable ItemStack ingredient) {
        if(!(Objects.requireNonNull(ingredient).getItem() instanceof ItemFocus))
            return false;
        if(ItemFocus.getPackage(ingredient)==null)
            return false;
        for (FocusEffect eff:ItemFocus.getPackage(ingredient).getFocusEffects()) {
            if(eff.getClass().equals(effect))
            {
                return true;
            }
        }
        return false;
    }
}
