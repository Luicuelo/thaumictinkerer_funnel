package es.luiscuesta.thaumictinkerer_funnel.common.tileentity;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TileEntityBase extends TileEntity {

    private boolean redstonePowered;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return false;
    }
    
    public void sendUpdates() {
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        markDirty();
    }

    public TileEntityBase() {
    	super();
    }
  
    //-------------------------------------------------------------
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        writeExtraNBT(compound);
        return super.writeToNBT(compound);
    }

    public void writeExtraNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("redstone", redstonePowered);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        readExtraNBT(compound);
        super.readFromNBT(compound);
    }

    public void readExtraNBT(NBTTagCompound nbttagcompound) {
        // todo: remove if in a couple versions time
        if (nbttagcompound.hasKey("redstone"))
            redstonePowered = nbttagcompound.getBoolean("redstone");
        else
            redstonePowered = false;
    }
    //-------------------------------------------------------------
    
    
    //envia
    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 5, this.getUpdateTag());
    }

    
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound cmp = super.getUpdateTag();
        writeExtraNBT(cmp);
        return cmp;
    }

    //recibe
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getNbtCompound());
        sendUpdates();
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        readExtraNBT(tag);
    }



    public abstract boolean respondsToPulses();

    public boolean getRedstonePowered() {
        return redstonePowered;
    }

    public void setRedstonePowered(boolean b) {
        boolean oldRedstone = redstonePowered;
        redstonePowered = b;
        if (redstonePowered != oldRedstone)
            this.sendUpdates();
    }

    public boolean canRedstoneConnect() {
        return false;
    }
}
