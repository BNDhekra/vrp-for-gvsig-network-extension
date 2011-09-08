// TODO: Description of file
// TODO: Date

package org.gvsig.graph.vrp.gui;

//Needed imports
import javax.swing.JList;
import javax.swing.JPanel;

//TODO: description of class
public class Results {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private JPanel tabResults;

	// Constructor.
	// Just initializes the Control Panel on witch this JPanel will be drawn.
	public Results(VRPControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}
	
	
	/**
	 * Initialize Results tab.
	 */
	public JPanel initTab() {

		tabResults = new JPanel();
		tabResults.setLayout(null);
		
		JList list = new JList();
		list.setBounds(10, 11, 465, 290);
		tabResults.add(list);
		
		return tabResults;
	}
}
