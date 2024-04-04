/*
 * Copyright (c) 2020. Katrina Knight, Luis Cuesta 2024
 */

package com.nekokittygames.thaumictinkerer_funnel.client.rendering.tileentities;

import javax.annotation.Nullable;
import com.nekokittygames.thaumictinkerer_funnel.common.blocks.BlockFunnel;
import com.nekokittygames.thaumictinkerer_funnel.common.blocks.ModBlocks;
import com.nekokittygames.thaumictinkerer_funnel.common.tileentity.TileEntityFunnel;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.IEssentiaContainerItem;


public class TileEntityFunnelRenderer extends TileEntitySpecialRenderer<TileEntityFunnel> {


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
	    
	    if(jar==null)return;
	  	try {
	
			GlStateManager.pushMatrix();
			GlStateManager.translate(x+ 0.5F, y+ 0.6F, z+ 0.5F);
			RenderItem rendemItem=Minecraft.getMinecraft().getRenderItem();
			if (rendemItem!=null) rendemItem.renderItem(jar,  ItemCameraTransforms.TransformType.NONE);
			GlStateManager.popMatrix();	
		    			
			} catch (Exception e) {
					///e.printStackTrace();				
		}	 
	
	}
	
	
	//Draw model, not needed
	/*
	 * 
	IBakedModel model =BakedModelCache.getBakedModel("block/funnel");
	GlStateManager.enableRescaleNormal();		    		
	GlStateManager.enableBlend(); 
	GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA); 
	GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F); 
	
	BlockModelRenderer renderer=Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();				
	bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);					
	bufferBuilder.setTranslation(x - blockPos.getX(), y - blockPos.getY(), z - blockPos.getZ());
	World world = te.getWorld();
	renderer.renderModel(world, model, blockState, blockPos, bufferBuilder, true);
	tessellator.draw();											
	*/
	
	
	
	

	/* Old Method , rendering Jar Model and box with essentia
	@Override
	public void render(@Nullable TileEntityFunnel te, double x, double y, double z, float pticks, int digProgress, float unused) {

		if(te==null)return;
		if(!te.getWorld().isRemote)return;
		if(!te.getWorld().isBlockLoaded(te.getPos(), false)) return;

        BlockPos blockPos=te.getPos();
        IBlockState blockState=te.getWorld().getBlockState(blockPos);
        Block block=blockState.getBlock();    
		if(block!= ModBlocks.funnel) return;
				
        		blockState=block.getActualState(blockState, getWorld(), blockPos);
				Aspect aspect = null;				
				int amount=0;
				int capacity=250;
			    float level =0F;
		        Color co = new Color(0);
		       	    
		    	
		    	boolean hasJar=false;
		    	boolean hasEssentia=false;
		    	
		    	if(blockState.getProperties().containsKey(BlockFunnel.JAR))
		    		hasJar= blockState.getValue(BlockFunnel.JAR).booleanValue();
		    			  
    		
    			JarAspect jarAspect=te.getJarAspect();    			
    			if(jarAspect!=null) {
    				aspect=jarAspect.getAspect();
    				amount=jarAspect.getAmount();    			
    				capacity=jarAspect.getCapacity();	
    				level=(float) amount / capacity * 0.625F;
    				if (level> 0.625F) level= 0.625F;
    				if (aspect != null) co = new Color(aspect.getColor());
    				hasEssentia=true;
    			}
    			
    					    
    			
				try {
					
					IBakedModel model;
					
					 if  (hasJar)model =BakedModelCache.getBakedModel("block/funnel_jar");
					 else model =BakedModelCache.getBakedModel("block/funnel");
					
					Tessellator tessellator = Tessellator.getInstance();				
					BufferBuilder bufferBuilder = tessellator.getBuffer();
					World world = te.getWorld();
					if (world == null)
						world = Minecraft.getMinecraft().world;					
					
					if(hasJar && hasEssentia) { 
						GlStateManager.pushMatrix();
						bufferBuilder.setTranslation(x+ 0.5F, y+ 0.1F, z+ 0.5F);		
				        RenderCubes renderBlocks = new RenderCubes();
				        renderBlocks.setRenderBounds(0.25D, 0.0625D, 0.25D, 0.75D, 0.1875D + (double) level, 0.75D);
				        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
				        TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("thaumcraft:blocks/animatedglow");			     
				        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);			        
				        float r= (float) co.getRed() / 255.0F;
				        float g= (float) co.getGreen() / 255.0F;
				        float b= (float) co.getBlue() / 255.0F;			        
				        renderBlocks.renderFaceYNeg(BlocksTC.jarNormal, -0.5D, 0.0D, -0.5D, icon,r,g,b,200);
				        renderBlocks.renderFaceYPos(BlocksTC.jarNormal, -0.5D, 0.0D, -0.5D, icon,r,g,b,200);
				        renderBlocks.renderFaceZNeg(BlocksTC.jarNormal, -0.5D, 0.0D, -0.5D, icon,r,g,b,200);
				        renderBlocks.renderFaceZPos(BlocksTC.jarNormal, -0.5D, 0.0D, -0.5D, icon,r,g,b,200);
				        renderBlocks.renderFaceXNeg(BlocksTC.jarNormal, -0.5D, 0.0D, -0.5D, icon,r,g,b,200);
				        renderBlocks.renderFaceXPos(BlocksTC.jarNormal, -0.5D, 0.0D, -0.5D, icon,r,g,b,200);
				        tessellator.draw();							
						GlStateManager.popMatrix();
					}
						

					GlStateManager.enableBlend();
					BlockModelRenderer renderer=Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();				
					GlStateManager.pushMatrix();
					bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);					
					bufferBuilder.setTranslation(x - blockPos.getX(), y - blockPos.getY(), z - blockPos.getZ());
					renderer.renderModel(world, model, blockState, blockPos, bufferBuilder, true);
					tessellator.draw();											
					GlStateManager.popMatrix();
					
	
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);		
					bufferBuilder.setTranslation(0, 0, 0);
					GlStateManager.disableBlend();
	
				} catch (Exception e) {
					e.printStackTrace();
					GlStateManager.popMatrix();
				}	
									
	}*/
	

	
	
}
