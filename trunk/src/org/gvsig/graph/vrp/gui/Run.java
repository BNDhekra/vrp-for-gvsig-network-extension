// TODO: Description of file
// TODO: Date

package org.gvsig.graph.vrp.gui;

//Needed imports
import javax.swing.JButton;
import javax.swing.JPanel;

//TODO: description of class
public class Run {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private JPanel tabRun;

	// Constructor.
	// Just initializes the Control Panel on witch this JPanel will be drawn.
	public Run(VRPControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}
	
	
	/**
	 * Initialize Run tab.
	 * @wbp.parser.entryPoint
	 */
	public JPanel initTab() {

		tabRun = new JPanel();
		tabRun.setLayout(null);
		JButton btnNextTab4 = new JButton("Next >>");
		btnNextTab4.setBounds(386, 278, 89, 23);
		btnNextTab4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnNextRunActionPerformed();
			}
		});
		tabRun.add(btnNextTab4);
		
		JButton btnPreviousTab4 = new JButton("<< Undo");
		btnPreviousTab4.setBounds(287, 278, 89, 23);
		btnPreviousTab4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnPreviousRunActionPerformed();
			}
		});
		tabRun.add(btnPreviousTab4);
		
		return tabRun;
	}
	
	// Get the GA options and run the GA with them
	private void btnNextRunActionPerformed(){
		controlPanel.switchToNextTab();
	}
		
	// Just go to the previous tab
	private void btnPreviousRunActionPerformed(){
		controlPanel.switchToPreviousTab();
	}
}
