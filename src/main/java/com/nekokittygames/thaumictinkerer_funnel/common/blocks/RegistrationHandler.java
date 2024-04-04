package com.nekokittygames.thaumictinkerer_funnel.common.blocks;

import java.util.ArrayList;

import com.nekokittygames.thaumictinkerer_funnel.thaumictinkerer_funnel;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public  class RegistrationHandler {
    
    private ArrayList<TTBlock> blocks = new ArrayList<>();
    private ArrayList<TTBlock> itemBlocks = new ArrayList<>();
    
    
	public void addBlockForRegistry(TTBlock block) {
		blocks.add(block);
	}
	public void addBlockItemForRegistry(TTBlock block) {
		itemBlocks.add(block);
	}
    
    public  void registerBlocks(final RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
		for(int i = 0; i < blocks.size(); i++) {			
			TTBlock b=(TTBlock) (blocks.get(i));					
			((Block)b).setRegistryName(b.getResourceLocation());						
			((Block)b).setUnlocalizedName(blocks.get(i).getUnlocalizedName());			
			registry.register((Block) blocks.get(i));
		}
    }

    public  void registerItems(final RegistryEvent.Register<Item> event) {

        IForgeRegistry<Item> registry = event.getRegistry();

        /*
        for (final ItemBlock item : items) {
            final Block block = item.getBlock();
            final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null registry name", block);
            registry.register(item.setRegistryName(registryName));
            ITEM_BLOCKS.add(item);
        }
        */
        
		for(int i = 0; i < itemBlocks.size(); i++) {			
			ItemBlock itemBlock=new ItemBlock ((Block)(itemBlocks.get(i)));
			ResourceLocation rl=new ResourceLocation (itemBlocks.get(i).getItemBlockName()) ;
			itemBlock.setRegistryName(rl);
			itemBlock.setUnlocalizedName(itemBlocks.get(i).getUnlocalizedName());					
			registry.register(itemBlock);
		}

		for(int i = 0; i < blocks.size(); i++) {
			Block block =(Block) (blocks.get(i));
			if (block instanceof TTTileEntity) {
				Class<? extends TileEntity> classTileEntity=((TTTileEntity)block).getClassTileEntity();
				GameRegistry.registerTileEntity(classTileEntity, block.getRegistryName().toString());
			}
				
		} 
    }
    
	public void registerModels(ModelRegistryEvent  event) {		
		for(int i = 0; i < itemBlocks.size(); i++) {
			blocks.get(i).registerModels();
		}

		/*
		for(int i = 0; i < items.size(); i++) {
			items.get(i).registerModels();
		}*/
	}

	
	
    /*
    private static void registerTileEntities() {
        registerTileEntity(TileEntityFunnel.class, LibBlockNames.FUNNEL);
    }

    private static void registerTileEntity(Class<? extends TileEntity> clazz, String name) {
        GameRegistry.registerTileEntity(clazz, new ResourceLocation("thaumictinkerer_funnel", name).toString());
    }*/
}
