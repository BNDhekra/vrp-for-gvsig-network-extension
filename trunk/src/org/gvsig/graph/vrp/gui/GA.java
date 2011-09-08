// TODO: Description of file
// TODO: Date

package org.gvsig.graph.vrp.gui;

//Needed imports
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.border.TitledBorder;
import org.metavrp.GA.*;
import org.metavrp.GA.support.*;
import org.metavrp.VRP.*;

//TODO: description of class
public class GA {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private JPanel tabGA;
	private GeneList geneList;
	private GAParameters params;
	private ButtonGroup buttonGroup;
	
	// Constructor.
	// Just initializes the Control Panel on witch this JPanel will be drawn.
	public GA(VRPControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}

	
	/**
	 * Initialize Genetic Algorithm tab.
	 * @wbp.parser.entryPoint
	 */
	public JPanel initTab() {
	
		tabGA = new JPanel();
		tabGA.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Genetic Algorithm settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(6, 7, 469, 47);
		tabGA.add(panel);
		panel.setLayout(null);
		
		buttonGroup = new ButtonGroup();
		
		JRadioButton rdbtnDefaultSettings = new JRadioButton("Default settings", true);
		buttonGroup.add(rdbtnDefaultSettings);
		rdbtnDefaultSettings.setBounds(122, 17, 101, 23);
		panel.add(rdbtnDefaultSettings);
		
		JRadioButton rdbtnUserSelectedadvanced = new JRadioButton("User selected settings (Advanced)");
		buttonGroup.add(rdbtnUserSelectedadvanced);
		rdbtnUserSelectedadvanced.setBounds(228, 17, 191, 23);
		panel.add(rdbtnUserSelectedadvanced);
		
		JButton btnNextTab3 = new JButton("Next >>");
		btnNextTab3.setBounds(386, 278, 89, 23);
		btnNextTab3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnNextGAActionPerformed();
			}
		});
		tabGA.add(btnNextTab3);
		
		JButton btnPreviousTab3 = new JButton("<< Undo");
		btnPreviousTab3.setBounds(287, 278, 89, 23);
		btnPreviousTab3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnPreviousGAActionPerformed();
			}
		});
		tabGA.add(btnPreviousTab3);

		return tabGA;
	}
	
	// Generate an object with the desired GA parameters
	public GAParameters generateGAParameters(){
		int popSize = 50;
        float elitism=0.1f;
        float mutationProb=0.2f;
        float crossoverProb=0.8f;
        int generations=40;
		
        // Generate an object with the desired parameters
        return new GAParameters(popSize, elitism, mutationProb, crossoverProb, generations);
	}
	
	
	// Get the GA options and run the GA with them
	private void btnNextGAActionPerformed(){
		
		// Create an object with the GA parameters
		params = generateGAParameters();
		
		controlPanel.switchToNextTab();
		
        // Run the genetic algorithm
		
        VRPGARun run = new VRPGARun(params, geneList, controlPanel.getODMatrix().getCostMatrix());
	}
	
	// Just go to the previous tab
	private void btnPreviousGAActionPerformed(){
		controlPanel.switchToPreviousTab();
	}

}
