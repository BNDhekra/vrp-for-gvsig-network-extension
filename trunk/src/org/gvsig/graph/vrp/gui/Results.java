// TODO: Description of file
// TODO: Date

package org.gvsig.graph.vrp.gui;

//Needed imports
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

//TODO: description of class
public class Results {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private JPanel tabResults;
	private JTextField textField;

	// Constructor.
	// Just initializes the Control Panel on witch this JPanel will be drawn.
	public Results(VRPControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}
	
	/**
	 * Initialize Results tab.
	 * @wbp.parser.entryPoint
	 */
	public JPanel initTab() {

		tabResults = new JPanel();
		tabResults.setLayout(null);
		
		JList list = new JList();
		list.setBounds(10, 42, 465, 259);
		tabResults.add(list);
		
		// Button "Next >>"
		JButton btnNextTab4 = new JButton("Close");
		btnNextTab4.setBounds(386, 278, 89, 23);
		btnNextTab4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCloseActionPerformed();
			}
		});
		tabResults.add(btnNextTab4);
		
		// Button "<< Undo"
		JButton btnPreviousTab4 = new JButton("<< Undo");
		btnPreviousTab4.setBounds(287, 278, 89, 23);
		btnPreviousTab4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnPreviousResultsActionPerformed();
			}
		});
		tabResults.add(btnPreviousTab4);
		
		JLabel lblShowTop = new JLabel("Show the top");
		lblShowTop.setBounds(10, 11, 70, 14);
		tabResults.add(lblShowTop);
		
		textField = new JTextField();
		textField.setText("10");
		textField.setBounds(76, 8, 20, 20);
		tabResults.add(textField);
		textField.setColumns(3);
		
		JLabel lblResults = new JLabel("results");
		lblResults.setBounds(100, 11, 46, 14);
		tabResults.add(lblResults);
		
		return tabResults;
	}
	
	// The button's "Close" functionality.
	// Just closes the window and stops the threads
	public void btnCloseActionPerformed(){
		// TODO: Show message alert "Are you sure you want to close this?"
		// Stop the thread.
		// TODO: the stop method is @deprecated. Create some boolean var in the run tab to detect that it should stop.
		controlPanel.getRun().getStatsThread().stop();
		// Close the application.
		controlPanel.closeWindow();
	}
	
	// The button's "<< Back" functionality.
	// Just goes back to the previous tab.
	public void btnPreviousResultsActionPerformed(){
		// Go back to the Run tab
		controlPanel.switchToPreviousTab();
	}
}