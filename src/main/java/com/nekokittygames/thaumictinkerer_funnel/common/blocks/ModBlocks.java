package com.nekokittygames.thaumictinkerer_funnel.common.blocks;

import com.google.common.base.Preconditions;
import com.nekokittygames.thaumictinkerer_funnel.thaumictinkerer_funnel;
import com.nekokittygames.thaumictinkerer_funnel.common.libs.LibBlockNames;
import com.nekokittygames.thaumictinkerer_funnel.common.libs.LibMisc;
import com.nekokittygames.thaumictinkerer_funnel.common.tileentity.*;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.nekokittygames.thaumictinkerer_funnel.common.utils.MiscUtils.nullz;

@SuppressWarnings("WeakerAccess")
@GameRegistry.ObjectHolder(LibMisc.MOD_ID)
public class ModBlocks {
	public static final BlockFunnel funnel = new BlockFunnel();	
	public static void init() {

	}
   
}
