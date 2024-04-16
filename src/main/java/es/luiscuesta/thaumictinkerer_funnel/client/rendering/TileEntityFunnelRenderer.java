/*
 * Copyright (c) 2020. Katrina Knight, Luis Cuesta 2024
 */

package es.luiscuesta.thaumictinkerer_funnel.client.rendering;

import java.awt.Color;
import java.nio.FloatBuffer;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import es.luiscuesta.thaumictinkerer_funnel.common.blocks.BlockFunnel;
import es.luiscuesta.thaumictinkerer_funnel.common.blocks.BlockTileEntity.BakedModelCache;
import es.luiscuesta.thaumictinkerer_funnel.common.blocks.ModBlocks;
import es.luiscuesta.thaumictinkerer_funnel.common.tileentity.TileEntityFunnel;
import es.luiscuesta.thaumictinkerer_funnel.common.tileentity.TileEntityFunnel.JarAspect;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.lib.RenderCubes;


public class TileEntityFunnelRenderer extends TileEntitySpecialRenderer<TileEntityFunnel> {
	private static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(4);
	

	@SuppressWarnings("deprecation")
	@Override
	public void render(@Nullable TileEntityFunnel te, double x, double y, double z, float pticks, int digProgress, float unused) {

		if(te==null)return;
		if(!te.getWorld().isRemote)return;
		if(!te.getWorld().isBlockLoaded(te.getPos(), false)) return;

        BlockPos blockPos=te.getPos();
        if(blockPos==null) return;
        
        IBlockState blockState=te.getWorld().getBlockState(blockPos);
        if(blockState==null) return;
        
        Block block=blockState.getBlock();    
		if(block==null||block!= ModBlocks.funnel) return;
			
        blockState=block.getActualState(blockState, getWorld(), blockPos);
        if(blockState==null) return;
        
		boolean hasJar=false;
		ItemStack jar=null;
		
		if(blockState.getProperties().containsKey(BlockFunnel.JAR))
		    		hasJar= blockState.getValue(BlockFunnel.JAR).booleanValue();
		    	
	    if (hasJar && te.getInventory()!=null && te.getInventory().getStackInSlot(0) != ItemStack.EMPTY && te.getInventory().getStackInSlot(0).getItem() instanceof IEssentiaContainerItem) 		    		
		    		jar= te.getInventory().getStackInSlot(0);
	    
	    if(jar==null) {
	    	return;
	    }
	  	try {
	  		
	        GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	  		
	  		if(jar.getUnlocalizedName().equals("tile.jar_normal")) {	  					  		
	  			JarAspect jarAspect=te.getJarAspect();    			  			
				Aspect aspect=null;
				int amount=0;
				if (jarAspect!=null) {
					aspect=jarAspect.getAspect();
					amount=jarAspect.getAmount(); 
				}
		
	  			
    			if(aspect!=null&&amount>0) {
    				Color color = new Color(aspect.getColor());      		
    				int capacity=jarAspect.getCapacity();	
    				float maxLevel=0.525F;
    				float level=(float) amount / capacity * maxLevel;
    				if (level> maxLevel) level= maxLevel;
    				drawEssentia(te, x, y, z, blockPos, blockState, block,color,level);
    			}
    			
    			if (amount>0)
    				drawModel(te, x, y, z, blockPos, blockState,block,"block/jar_normal");
    			else
    				drawItem(x, y, z,jar);
	  		}
	  		else {
	  			drawItem(x, y, z,jar);
	  		}
	  		
	        GlStateManager.disableAlpha();
			GlStateManager.disableBlend();
						
			} catch (Exception e) {
					///e.printStackTrace();				
		}	 
	
	}
	private void drawItem(double x, double y, double z, ItemStack jar ) {
			GlStateManager.pushMatrix();
			GlStateManager.disableBlend(); 
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1F); 
			GlStateManager.translate(x+ 0.5F, y+ 0.7F, z+ 0.5F);
			RenderItem rendemItem=Minecraft.getMinecraft().getRenderItem();
			if (rendemItem!=null) rendemItem.renderItem(jar,  ItemCameraTransforms.TransformType.NONE);
		GlStateManager.popMatrix();	
	}
	
	private void drawModel(TileEntityFunnel te,double x, double y, double z, BlockPos blockPos, IBlockState blockState,Block block, String stringModel) {
		IBakedModel model =BakedModelCache.getBakedModel(stringModel);
		World world = te.getWorld();
		GlStateManager.pushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		BlockModelRenderer renderer=Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();		
		Tessellator tessellator = Tessellator.getInstance();	
	 	BufferBuilder bufferBuilder = tessellator.getBuffer();	
	 	
	 	float px=(float) (x - blockPos.getX());
	 	float py=(float) (y - blockPos.getY());
	 	float pz= (float) (z - blockPos.getZ());
	 	//enableStandardItemLighting( px,  py,  pz,1F,world.getLightBrightness(blockPos)); 	
	 	
		bufferBuilder.setTranslation(x - blockPos.getX(), y - blockPos.getY(), z - blockPos.getZ());
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);					
		renderer.renderModel(world, model, blockState, blockPos, bufferBuilder, true);
		tessellator.draw();
		bufferBuilder.setTranslation(0,0,0);
				
	 	//enableStandardItemLighting( px,  py,  pz,1F,1F); 	
		//disableStandardItemLighting();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GlStateManager.popMatrix();	
		
	}
	
	private  void drawEssentia(TileEntityFunnel te, double x, double y, double z, BlockPos blockPos,IBlockState blockState, Block block, Color co, Float level) {
		GlStateManager.pushMatrix();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.setTranslation(x + 0.5F, y + 0.1F, z + 0.5F);
			RenderCubes renderBlocks = new RenderCubes();
		
			renderBlocks.setRenderBounds(0.25D, 0.0625D, 0.25D, 0.75D, 0.1875D + (double) level, 0.75D);
			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("thaumcraft:blocks/animatedglow");
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			
			float r = ((float) co.getRed() / 255.0F) *1.45F;
			float g = ((float) co.getGreen() / 255.0F)*1.05F;
			float b = ((float) co.getBlue() / 255.0F);
			
	        if (r>1F) r=1F;
	        if (g>1F) g=1F;
	        if (b>1F) b=1F;
	        
				enableMaxLighting();
	        	GlStateManager.enableAlpha();
	  			GlStateManager.enableBlend();
	  			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	  			GL11.glColor4f(1.0F, 1.0F, 1.0F, .5F);
					
			renderBlocks.renderFaceYNeg(BlocksTC.jarNormal, -0.5D, 0.1D, -0.5D, icon, r, g, b, 190);
			renderBlocks.renderFaceYPos(BlocksTC.jarNormal, -0.5D, 0.1D, -0.5D, icon, r, g, b, 190);
			renderBlocks.renderFaceZNeg(BlocksTC.jarNormal, -0.5D, 0.1D, -0.5D, icon, r, g, b, 190);
			renderBlocks.renderFaceZPos(BlocksTC.jarNormal, -0.5D, 0.1D, -0.5D, icon, r, g, b, 190);
			renderBlocks.renderFaceXNeg(BlocksTC.jarNormal, -0.5D, 0.1D, -0.5D, icon, r, g, b, 190);
			renderBlocks.renderFaceXPos(BlocksTC.jarNormal, -0.5D, 0.1D, -0.5D, icon, r, g, b, 190);
			tessellator.draw();
			
		GlStateManager.popMatrix();	
		bufferBuilder.setTranslation(0, 0, 0);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	
	}
	
	@SideOnly(Side.CLIENT)
	public static void enableMaxLighting() {
		GlStateManager.disableLighting();
		final int lightmapCoords = 15728881;

		final int skyLight = lightmapCoords % 65536;
		final int blockLight = lightmapCoords / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, skyLight, blockLight);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	private static FloatBuffer setColorBuffer(float par0, float par1, float par2, float par3)
    {
        colorBuffer.clear();
        colorBuffer.put(par0).put(par1).put(par2).put(par3);
        colorBuffer.flip();
        return colorBuffer;
    }
	/*
	public static void enableStandardItemLighting(float x, float y, float z,float f , float f2)
    {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_LIGHT1);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, setColorBuffer(x,y,z, 0.0F));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, setColorBuffer(1F, 1F, 1F, 1.0F));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, setColorBuffer(0.1F, 0.1F, 0.1F, 1.0F));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, setColorBuffer(f2, f2, f2, 1.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, setColorBuffer(x,y,z, 0.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, setColorBuffer(1F, 1F, 1F, 1.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, setColorBuffer(0.1F, 0.1F, 0.1F, 1.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, setColorBuffer(f2, f2, f2, 1.0F));
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, setColorBuffer(f, f, f, 1.0F));
    }
	
	public static void disableStandardItemLighting()
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LIGHT0);
        GL11.glDisable(GL11.GL_LIGHT1);
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
    }*/
	

	

	
	
}
