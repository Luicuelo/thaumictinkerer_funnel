package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;

public class BlockRailPowered extends BlockRailBase{
	

    public static final PropertyBool POWERED = PropertyBool.create("powered");

    private final boolean isActivator;

    public BlockRailPowered()
    {
        this(false);
    }

    public BlockRailPowered(boolean isActivator)
    {
        super(true);
        this.isActivator = isActivator;
    }

	@Override
	public IProperty<EnumRailDirection> getShapeProperty() {
		// TODO Auto-generated method stub
		return null;
	}
}