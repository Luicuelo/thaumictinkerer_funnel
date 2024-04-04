/*
 * Copyright (c) 2020. Katrina Knight
 */

package com.nekokittygames.thaumictinkerer_funnel.client.rendering;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;

import com.nekokittygames.thaumictinkerer_funnel.thaumictinkerer_funnel;
import com.nekokittygames.thaumictinkerer_funnel.common.helper.IItemVariants;
import com.nekokittygames.thaumictinkerer_funnel.common.libs.LibMisc;
import com.nekokittygames.thaumictinkerer_funnel.common.utils.IVariant;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Model manager helper class for loading models
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = LibMisc.MOD_ID)
public class ModelManager {
    private static final ModelManager INSTANCE = new ModelManager();

    private Map<Item, Map<String,ModelResourceLocation>> Item_Variants_baked=new HashMap<>();
    /**
     * default mapper for properties to string
     */
    private final StateMapperBase propertyStringMapper = new StateMapperBase() {
        @Override
        protected ModelResourceLocation getModelResourceLocation(@Nonnull final IBlockState state) {
            return new ModelResourceLocation("minecraft:air");
        }
    };
    /**
     * The {@link Item}s that have had models registered so far.
     */
    private final Set<Item> itemsRegistered = new HashSet<>();

    /**
     * Empty constructor
     */
    private ModelManager() {

    }


    public static ModelManager getInstance() {
        return INSTANCE;
    }


/*    
    @SubscribeEvent
    public static void registerAllModels(final ModelRegistryEvent event) {
        //INSTANCE.registerFluidModels();
        registerBlockModels();
    }   

    private void registerItemModel(final Item item) {
        final ResourceLocation registryName = Objects.requireNonNull(item.getRegistryName());
        registerItemModel(item, registryName.toString());
    }

    private void registerBlockModels() {

       //registerBlockItemModel(ModBlocks.funnel.getDefaultState().withProperty(BlockFunnel.JAR, false));
       OBJLoader.INSTANCE.addDomain("thaumictinkerer_funnel");
       thaumictinkerer_funnel.modRegistry.registerModels(null);

    }

*/ 
    
    public void registerBlockItemModel(IBlockState state) {
        final Block block = state.getBlock();
        final Item item = Item.getItemFromBlock(block);

        if (item != Items.AIR) {
            final ResourceLocation registryName = Objects.requireNonNull(block.getRegistryName());
            //ModelResourceLocation mrl = new ModelResourceLocation(registryName, propertyStringMapper.getPropertyString(state.getProperties()));
        	ModelResourceLocation mrl = new ModelResourceLocation(registryName, "inventory");
            registerItemModel(item, mrl);
        }
    }


    private <T extends IVariant> void registerVariantItemModels(final Item item, final String variantName, final T[] values) {
        for (final T value : values) {

            registerItemModelForMeta(item, value.getMeta(), variantName + "=" + value.getName());
        }
    }




    private void registerItemModel(final Item item, final String modelLocation) {
        final ModelResourceLocation fullModelLocation = new ModelResourceLocation(modelLocation, "inventory");
        registerItemModel(item, fullModelLocation);
    }

    private void registerItemModel(final Item item, final ModelResourceLocation fullModelLocation) {
        ModelBakery.registerItemVariants(item, fullModelLocation); // Ensure the custom model is loaded and prevent the default model from being loaded
        if(item instanceof IItemVariants)
        {
            for (String variant:((IItemVariants)item).GetVariants()) {
                ResourceLocation loc=new ResourceLocation(LibMisc.MOD_ID,fullModelLocation.getResourcePath()+"/"+variant);
                ModelResourceLocation mrl=new ModelResourceLocation(loc,fullModelLocation.getVariant());
                try {
                    ResourceLocation test=new ResourceLocation(LibMisc.MOD_ID,"models/item/"+loc.getResourcePath()+".json");
                    if(Minecraft.getMinecraft().getResourceManager().getResource(test)==null) {
                        thaumictinkerer_funnel.logger.info("Unable to find model file for item: "+item.toString()+" variant: "+variant);
                        Item_Variants_baked.putIfAbsent(item,new HashMap<>());
                        Item_Variants_baked.get(item).put(variant,fullModelLocation);
                        continue;
                    }

                } catch (IOException e) {
                    thaumictinkerer_funnel.logger.info("Unable to find model file for item: "+item.toString()+" variant: "+variant);
                    Item_Variants_baked.putIfAbsent(item,new HashMap<>());
                    Item_Variants_baked.get(item).put(variant,fullModelLocation);
                    continue;
                }
                Item_Variants_baked.putIfAbsent(item,new HashMap<>());
                Item_Variants_baked.get(item).put(variant,mrl);
                ModelBakery.registerItemVariants(item, mrl);
            }
            registerItemModel(item, stack -> {
                String var=((IItemVariants)item).GetVariant(stack);
                if(Item_Variants_baked.getOrDefault(stack.getItem(),new HashMap<>()).containsKey(var))
                    return Item_Variants_baked.getOrDefault(stack.getItem(),new HashMap<>()).get(var);
                else {
                    thaumictinkerer_funnel.logger.error("Variant "+var+" Has been added to item "+stack.getItem().toString()+"since loading. Variant list should  not change");
                    return fullModelLocation;
                }

            });
        }
        else
            registerItemModel(item, stack -> fullModelLocation);
    }

    /**
     * Register an {@link ItemMeshDefinition} for an {@link Item}.
     *
     * @param item           The Item
     * @param meshDefinition The ItemMeshDefinition
     */
    private void registerItemModel(final Item item, final ItemMeshDefinition meshDefinition) {
        itemsRegistered.add(item);
        ModelLoader.setCustomMeshDefinition(item, meshDefinition);
    }


    /**
     * Register a model for a metadata value an {@link Item}.
     * <p>
     * Uses the registry name as the domain/path and {@code variant} as the variant.
     *
     * @param item     The Item
     * @param metadata The metadata
     * @param variant  The variant
     */
    private void registerItemModelForMeta(final Item item, final int metadata, final String variant) {
        registerItemModelForMeta(item, metadata, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), variant));
    }

    /**
     * Register a model for a metadata value of an {@link Item}.
     * <p>
     * Uses {@code modelResourceLocation} as the domain, path and variant.
     *
     * @param item                  The Item
     * @param metadata              The metadata
     * @param modelResourceLocation The full model location
     */
    private void registerItemModelForMeta(final Item item, final int metadata, final ModelResourceLocation modelResourceLocation) {
        itemsRegistered.add(item);
        ModelLoader.setCustomModelResourceLocation(item, metadata, modelResourceLocation);
    }
}
