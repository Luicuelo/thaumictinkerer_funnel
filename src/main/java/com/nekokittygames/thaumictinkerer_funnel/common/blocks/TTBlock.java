package com.nekokittygames.thaumictinkerer_funnel.common.blocks;

import com.nekokittygames.thaumictinkerer_funnel.thaumictinkerer_funnel;
import com.nekokittygames.thaumictinkerer_funnel.client.libs.LibClientMisc;
import com.nekokittygames.thaumictinkerer_funnel.client.rendering.ModelManager;
import com.nekokittygames.thaumictinkerer_funnel.common.libs.LibMisc;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IRegistryDelegate;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TTBlock extends Block {
    private String baseName;
    private ResourceLocation resourceLocation;
    

	private static final Map<IRegistryDelegate<Block>, IStateMapper> customStateMappers = ReflectionHelper.getPrivateValue(ModelLoader.class, null, "customStateMappers");
	private static final DefaultStateMapper fallbackMapper = new DefaultStateMapper();
    
    public ResourceLocation getResourceLocation() {
    	return resourceLocation;
    }
    
    
	public   String getUnlocalizedName() {
		return resourceLocation.getResourceDomain() + "." + resourceLocation.getResourcePath();
	}
	
	public   String getItemBlockName() {
		return resourceLocation.getResourceDomain() + ":" + resourceLocation.getResourcePath();
	}	
	
    
    public TTBlock(String name, Material materialIn) {
        this(name, materialIn, materialIn.getMaterialMapColor());

    }
	
    public TTBlock(String name, Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
        baseName = name;
        //setBlockName(this, name);
        setHardness(2);
        if (isInCreativeTab())
            setCreativeTab(thaumictinkerer_funnel.getTab());
        resourceLocation= new ResourceLocation(LibMisc.MOD_ID, name);
    }

	@SideOnly(Side.CLIENT)
	public void registerModels() {
		if(Item.getItemFromBlock(this) != Items.AIR)	{
			 
			
			//OJO
			//registerBlockToState(this, 0, getDefaultState());
			ModelManager.getInstance().registerBlockItemModel(getDefaultState());			 
			//ModelLoader.setCustomStateMapper(this,new StateMap.Builder().build());
			
		}
	}
	
	/*
	private static ModelResourceLocation getMrlForState(IBlockState state) {
		return customStateMappers
				.getOrDefault(state.getBlock().delegate, fallbackMapper)
				.putStateModelLocations(state.getBlock())
				.get(state);
	}
	
	public static void registerBlockToState(Block b, int meta, IBlockState state) {
		ModelLoader.setCustomModelResourceLocation(
				Item.getItemFromBlock(b),
				meta,
				getMrlForState(state)
				);
	}
	*/

   /*
    private static void setBlockName(final TTBlock block, final String blockName) {
        block.setRegistryName(LibMisc.MOD_ID, blockName);
        final ResourceLocation registryName = Objects.requireNonNull(block.getRegistryName());
        block.setUnlocalizedName(registryName.toString());
    }*/

    protected boolean isInCreativeTab() {
        return true;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        int i = 0;
        String name = "item." + LibClientMisc.RESOURCE_PREFIX + baseName + "." + i;
        while (I18n.hasKey(name)) {
            tooltip.add(I18n.format(name));
            i++;
            name = "item." + LibClientMisc.RESOURCE_PREFIX + baseName + "." + i;
        }

    }
}
