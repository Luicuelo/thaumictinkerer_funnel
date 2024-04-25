/*
 * Copyright (c) Luis Cuesta 2024 - 2020. Katrina Knight, 
 */

package es.luiscuesta.thaumictinkerer_funnel.client.rendering;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import es.luiscuesta.thaumictinkerer_funnel.common.blocks.BlockEssenceMeter.BlockColors;
import es.luiscuesta.thaumictinkerer_funnel.common.libs.LibMisc;
import es.luiscuesta.thaumictinkerer_funnel.common.tileentity.TileEntityEssentiaMeter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.lib.RenderCubes;
import thaumcraft.client.lib.UtilsFX;


@SideOnly(Side.CLIENT)
public class TileEntityEssentiaMeterRenderer extends TileEntitySpecialRenderer<TileEntityEssentiaMeter> {

	private static ResourceLocation TEX_LABEL = new ResourceLocation(LibMisc.MOD_ID, "textures/blocks/meter/label.png");

	@Override
	public void render(@Nullable TileEntityEssentiaMeter te, double x, double y, double z, float pticks, int digProgress, float unused) {

		if(te==null)return;
		if(!te.getWorld().isRemote)return;
		if(!te.getWorld().isBlockLoaded(te.getPos(), false)) return;

       	
        BlockColors blockColor=te.getBlockColor();
        if (blockColor==null) return;
        
        Color co = new Color(0);       
        int essenceAmount=te.getHooperAmmount();
    	int capacity=blockColor.getEssenceCapacity();
    	
        Tessellator tessellator = Tessellator.getInstance();	
      	BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (essenceAmount==0) {
        	renderFilter(te, bufferBuilder, x, y, z,capacity);
        	return;
        }
        co = new Color(te.getHopperAspect().getColor());
        
  
        
        GlStateManager.pushMatrix();
                
        	renderFilter(te, bufferBuilder, x, y, z,capacity);        	
			bufferBuilder.setTranslation(x+ 0.5F, y+ 0.1F, z+ 0.5F);		
	        RenderCubes renderBlocks = new RenderCubes();
	        
	        
	        double margen=0.06D;
	       
	        double level= ((double)essenceAmount/(double)capacity);
	        if (level>1D) level=1D;
	        level=level*(1D-(margen*2D));
	       
	        
	        renderBlocks.setRenderBounds(margen, 0, margen, 1D-margen,  level, 1D-margen);
	        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
	        TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("thaumcraft:blocks/animatedglow");			     
	        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);	
	        
	        float r= (float) co.getRed() / 160.0F;
	        float g= (float) co.getGreen() / 170.0F;
	        float b= (float) co.getBlue() / 170.0F;
	        if (r>1F) r=1F;
	        if (g>1F) g=1F;
	        if (b>1F) b=1F;

	        
	        enableMaxLighting();
	        GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, .5F);
	        
	        renderBlocks.renderFaceYNeg(BlocksTC.jarNormal, -0.5D, 0.0D, -0.5D, icon,r,g,b,240);
	        renderBlocks.renderFaceYPos(BlocksTC.jarNormal, -0.5D, 0.0D, -0.5D, icon,r,g,b,240);
	        renderBlocks.renderFaceZNeg(BlocksTC.jarNormal, -0.5D, 0.0D, -0.5D, icon,r,g,b,240);
	        renderBlocks.renderFaceZPos(BlocksTC.jarNormal, -0.5D, 0.0D, -0.5D, icon,r,g,b,240);
	        renderBlocks.renderFaceXNeg(BlocksTC.jarNormal, -0.5D, 0.0D, -0.5D, icon,r,g,b,240);
	        renderBlocks.renderFaceXPos(BlocksTC.jarNormal, -0.5D, 0.0D, -0.5D, icon,r,g,b,240);
	        tessellator.draw();		
	        
	
		GlStateManager.popMatrix();
		
		bufferBuilder.setTranslation(0, 0, 0);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
 	
	}
	
	@SuppressWarnings("incomplete-switch")
	private static void renderFilter(TileEntityEssentiaMeter tile, BufferBuilder bufferBuilder, double x, double y,
			double z, float capacity) {

		EnumFacing enumFacing = tile.getLabelFacing();
		if (enumFacing == null || enumFacing == EnumFacing.DOWN)
			return;
		Aspect aspect = tile.getHopperAspect();

		GlStateManager.pushMatrix();
				switch (enumFacing) {
				case NORTH: {
					GlStateManager.translate(x + 0.5F, y + 0.6F, z - 0.02F);
					UtilsFX.renderQuadCentered(TEX_LABEL, 0.5f, 1.0f, 1.0f, 1.0f, -99, 771, 1.0f);
					GlStateManager.translate(0, 0, +0.04F);
					GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
					UtilsFX.renderQuadCentered(TEX_LABEL, 0.5f, 1.0f, 1.0f, 1.0f, -99, 771, 1.0f);
					break;
				}
				case EAST: {
					GlStateManager.translate(x + 1F + 0.02F, y + 0.6F, z + 0.5F);
					GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
					UtilsFX.renderQuadCentered(TEX_LABEL, 0.5f, 1.0f, 1.0f, 1.0f, -99, 771, 1.0f);
					GlStateManager.translate(-0.04F, 0, 0);
					GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
					UtilsFX.renderQuadCentered(TEX_LABEL, 0.5f, 1.0f, 1.0f, 1.0f, -99, 771, 1.0f);
					break;
				}
				case WEST: {
					GlStateManager.translate(x - 0.02F, y + 0.6F, z + 0.5F);
					GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
					UtilsFX.renderQuadCentered(TEX_LABEL, 0.5f, 1.0f, 1.0f, 1.0f, -99, 771, 1.0f);
					GlStateManager.translate(+0.04F, 0, 0);
					GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
					UtilsFX.renderQuadCentered(TEX_LABEL, 0.5f, 1.0f, 1.0f, 1.0f, -99, 771, 1.0f);
					break;
				}
				case SOUTH: {
					GlStateManager.translate(x + 0.5F, y + 0.6F, z + 1 + 0.02F);
					GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
					UtilsFX.renderQuadCentered(TEX_LABEL, 0.5f, 1.0f, 1.0f, 1.0f, -99, 771, 1.0f);
					GlStateManager.translate(0, 0, -0.04F);
					GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
					UtilsFX.renderQuadCentered(TEX_LABEL, 0.5f, 1.0f, 1.0f, 1.0f, -99, 771, 1.0f);
					break;
				}
				case UP: {
					GlStateManager.translate(x + 0.5F, y + 1F + 0.02F, z + 0.5F);
					GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
					UtilsFX.renderQuadCentered(TEX_LABEL, 0.5f, 1.0f, 1.0f, 1.0f, -99, 771, 1.0f);
					GlStateManager.translate(0, -0.04F, 0);
					GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
					UtilsFX.renderQuadCentered(TEX_LABEL, 0.5f, 1.0f, 1.0f, 1.0f, -99, 771, 1.0f);
					break;
				}
				}
		
				bufferBuilder.setTranslation(0, 0, 0);
				GL11.glRotatef(0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.popMatrix();

		if (aspect == null) return;									
		GlStateManager.pushMatrix();
		
			GlStateManager.rotate(0, 1F, 1F, 1F);
			GL11.glBlendFunc(770, 771);

			switch (enumFacing) {
			case NORTH: {
				// sur z mira hacia a ti
				GlStateManager.translate(x + 0.5F, y + 0.6F, z - 0.03F);
				GlStateManager.rotate(180, 0, 0, 1F); // eso hay que hacerlo siempre
				break;
			}
			case EAST: {
				// Mirando al sur
				// este el 0 de las x esta en el borde izquierdo mirando al sur, sumar hacia la
				// derecha
				// el 0 de las z esta en el borde más cercano, sumar hacia el centro
				// la y , sumar para subir
				GlStateManager.translate(x + 1F + 0.03F, y + 0.6F, z + 0.5F);
				GlStateManager.rotate(180, 0, 0, 1F);
				GlStateManager.rotate(90, 0, 1F, 0);
				break;
			}
			case WEST: {
				GlStateManager.translate(x - 0.03F, y + 0.6F, z + 0.5F);
				GlStateManager.rotate(180, 0, 0, 1F);
				GlStateManager.rotate(270, 0, 1F, 0);
				break;
			}
			case SOUTH: {
				GlStateManager.translate(x + 0.5F, y + 0.6F, z + 1 + 0.03F);
				GlStateManager.rotate(180, 0, 0, 1F);
				GlStateManager.rotate(180, 0, 1F, 0);
				break;
			}
			case UP: {
				GlStateManager.translate(x + 0.5F, y + 1F + 0.03F, z + 0.5F);
				GlStateManager.rotate(180, 0, 0, 1F);
				GlStateManager.rotate(90, 1F, 0, 0);
				GlStateManager.rotate(180, 0, 1F, 0);
				break;
			}
			}

			GlStateManager.scale(0.015F, 0.015F, 0.015F);
			UtilsFX.drawTag(-8, -8, aspect);

			// UtilsFX.drawTag(-8, -8, aspect, 1000f, 0, 0.0, 771, 1.0f, true);

			GlStateManager.translate(0, 0, -0.04F);
			capacity = (capacity / 1000F);
			DecimalFormat decimalFormat = new DecimalFormat("#.0");
			String am = (String.format("%15c", ' ') + decimalFormat.format(capacity) + "K");
			am = am.substring(am.length() - 15);

			renderText(-8, -3, am);

		
		GlStateManager.scale(1F, 1F, 1F);
		GlStateManager.translate(0, 0, 0);
		GlStateManager.popMatrix();

	}

	private static void renderText(double x,double y, String am) {
		Minecraft mc=Minecraft.getMinecraft();
        GL11.glPushMatrix();
        GL11.glColor4f(0.0f, 0.0f, 0.0f,  0.4f);
        
        float q = 0.5f;
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        q = 1.0f;
        int sw = mc.fontRenderer.getStringWidth(am);
        // 0x804000
        
        //mc.fontRenderer.drawString(am, (32 - sw + (int)x * 2) * q - 0.2F, (32 - mc.fontRenderer.FONT_HEIGHT + (int)y * 2) * q - 0.2F, 0, false);
        mc.fontRenderer.drawString(am, (32 - sw + (int)x * 2) * q, (32 - mc.fontRenderer.FONT_HEIGHT + (int)y * 2) * q, 0x633a34, false);
        GL11.glPopMatrix();

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
}
	
