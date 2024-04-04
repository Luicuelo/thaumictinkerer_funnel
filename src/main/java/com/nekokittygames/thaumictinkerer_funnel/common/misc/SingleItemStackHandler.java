package com.nekokittygames.thaumictinkerer_funnel.common.misc;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class SingleItemStackHandler extends ItemStackHandler {


    public SingleItemStackHandler() {
        super(1);
    }

    public SingleItemStackHandler(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
