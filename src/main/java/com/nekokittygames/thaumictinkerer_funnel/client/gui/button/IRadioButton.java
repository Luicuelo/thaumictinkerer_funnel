/*
 * Copyright (c) 2020. Katrina Knight
 */

package com.nekokittygames.thaumictinkerer_funnel.client.gui.button;

/**
 * Radio button interface
 */
public interface IRadioButton {

    /**
     * enable a single button from a button click
     */
    void enableFromClick();

    /**
     * Updates the button based on another's changed status
     *
     * @param otherButton button who's status changed
     */
    void updateStatus(IRadioButton otherButton);

    /**
     * @return is this button enabled?
     */
    boolean isEnabled();

    /**
     * @param enabled Is the button enabled?
     */
    void setEnabled(boolean enabled);

    /**
     * @return what group is this button attached to?
     */
    default String getGroup()
    {
        return "default";
    }
}
