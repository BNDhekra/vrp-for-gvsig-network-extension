package org.gvsig.graph.vrp.gui;

import javax.swing.JPanel;

/**
 * This interface defines the minimum methods that any tab should have.
 * @author David Pinheiro
 */
public interface Tab {
	
	/**
	 * Initializes the tab. Puts all the needed graphical elements on their places.
	 * This method should be called at the start of the plugin.
	 * @return Returns a JPanel with the initialized graphical elements
	 */
	public JPanel initTab();

	/**
	 * What should be done when the user comes from the previous tab.
	 */
	public void fromPreviousTab();
	
	/**
	 * What should be done when the user comes from the next tab.
	 */
	public void fromNextTab();

}
