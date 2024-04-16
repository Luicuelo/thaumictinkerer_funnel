package es.luiscuesta.thaumictinkerer_funnel.common.tileentity;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public interface ITileJarFillable  {

		//returns how can be added
		public int addToContainer(Aspect aspect, int quantity);
		public AspectList getAspects();
		
}
