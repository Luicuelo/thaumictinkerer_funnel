package es.luiscuesta.thaumictinkerer_funnel.client.rendering;

import java.util.HashMap;
import java.util.Map;


import es.luiscuesta.thaumictinkerer_funnel.common.libs.LibMisc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class BakedModelCache {

	private static final Map<String, IBakedModel> BAKED_MODELS = new HashMap<>();
	static {
		getBakedModel("block/jar_normal");
	}

	private static ResourceLocation ResourceLocationFromString(String location) {
		return new ResourceLocation(LibMisc.MOD_ID + ":" + location);
	}

	private  static  TextureAtlasSprite getTextureAtlas(ResourceLocation location) {

		TextureMap textureMap=Minecraft.getMinecraft().getTextureMapBlocks(); 			
		return textureMap.getAtlasSprite(location.toString());
	}


	private static IBakedModel storeBakedModel(String stringLocation) {

		ResourceLocation modelLocation = ResourceLocationFromString(stringLocation);
		IModel iModel;
		try {
			iModel = ModelLoaderRegistry.getModel(modelLocation);
			IBakedModel bakedModel = iModel.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK,BakedModelCache::getTextureAtlas);
			if (bakedModel != null)
				BAKED_MODELS.put(stringLocation, bakedModel);
			return bakedModel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static IBakedModel getBakedModel(String stringLocation) {
		IBakedModel bakedModel = BAKED_MODELS.get(stringLocation);
		if (bakedModel == null)
			bakedModel = storeBakedModel(stringLocation);
		return bakedModel;
	}
}