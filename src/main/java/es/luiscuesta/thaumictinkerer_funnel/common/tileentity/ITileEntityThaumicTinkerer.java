package es.luiscuesta.thaumictinkerer_funnel.common.tileentity;

public interface ITileEntityThaumicTinkerer {
	
	
	  public boolean getRedstonePowered();
      public void setRedstonePowered(boolean b);
      public boolean respondsToPulses();
      public boolean canRedstoneConnect();

}
