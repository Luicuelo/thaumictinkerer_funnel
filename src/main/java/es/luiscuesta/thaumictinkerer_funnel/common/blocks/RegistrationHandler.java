package es.luiscuesta.thaumictinkerer_funnel.common.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public  class RegistrationHandler {
    
    private ArrayList<BlockTileEntity<?>> blocks = new ArrayList<>();
    private ArrayList<BlockTileEntity<?>> itemBlocks = new ArrayList<>();
    
    
	public void addBlockForRegistry(BlockTileEntity<?> block) {
		blocks.add(block);
	}
	public void addBlockItemForRegistry(BlockTileEntity<?> block) {
		itemBlocks.add(block);
	}
    
    public  void registerBlocks(final RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
		for(BlockTileEntity<?> b:blocks) {		
			b.setRegistryName(b.getResourceLocation());						
			b.setUnlocalizedName(b.getUnlocalizedName());			
			registry.register(b);
		}
    }

    @SuppressWarnings("deprecation")
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
			ItemBlock itemBlock=new ItemBlock ((itemBlocks.get(i)));
			ResourceLocation rl=new ResourceLocation (itemBlocks.get(i).getItemBlockName()) ;
			itemBlock.setRegistryName(rl);
			itemBlock.setUnlocalizedName(itemBlocks.get(i).getUnlocalizedName());					
			registry.register(itemBlock);
		}

		for(int i = 0; i < blocks.size(); i++) {
			Block block =(blocks.get(i));
			if (block instanceof BlockTileEntity) {
				Class<? extends TileEntity> classTileEntity=((BlockTileEntity<?>)block).getClassTileEntity();
				GameRegistry.registerTileEntity(classTileEntity, block.getRegistryName().toString());
			}
				
		} 
    }


	public void registerModels(ModelRegistryEvent  event) {		
		for(int i = 0; i < itemBlocks.size(); i++) {
			blocks.get(i).registerModels(event);
		}

		/*
		for(int i = 0; i < items.size(); i++) {
			items.get(i).registerModels();
		}*/
				
	}

}
