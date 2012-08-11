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
import org.metavrp.GA.operators.OperatorsAndParameters;
import org.metavrp.GA.support.*;
import org.metavrp.VRP.*;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import java.awt.FlowLayout;
import javax.swing.DefaultComboBoxModel;

//TODO: description of class
public class GA implements Tab {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private JPanel tabGA;
	private OperatorsAndParameters params;
	private ButtonGroup buttonGroup;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JLabel lblPopulationSize;
	private JLabel lblOperator;
	private JComboBox comboBox;
	private JLabel lblProbability;
	private JLabel label;
	private JComboBox comboBox_1;
	private JLabel lblProbability_1;
	private JLabel lblElitism;
	private JLabel lblInnerDepotPenalty;
	
	private int problemSize=1000;
	
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
		rdbtnDefaultSettings.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				enableOperatorsParameters(false);
			}
		});
		buttonGroup.add(rdbtnDefaultSettings);
		rdbtnDefaultSettings.setBounds(122, 17, 101, 23);
		panel.add(rdbtnDefaultSettings);
		
		JRadioButton rdbtnUserSelectedadvanced = new JRadioButton("User selected settings (Advanced)");
		rdbtnUserSelectedadvanced.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				enableOperatorsParameters(true);
			}
		});
		buttonGroup.add(rdbtnUserSelectedadvanced);
		rdbtnUserSelectedadvanced.setBounds(228, 17, 191, 23);
		panel.add(rdbtnUserSelectedadvanced);
		
		JButton btnNextTab3 = new JButton("Run! >>");
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
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Operators and Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(6, 65, 469, 206);
		tabGA.add(panel_1);
		panel_1.setLayout(null);
		
		lblPopulationSize = new JLabel("Population Size");
		lblPopulationSize.setEnabled(false);
		lblPopulationSize.setBounds(18, 22, 78, 22);
		panel_1.add(lblPopulationSize);
		
		textField = new JTextField();
		textField.setEnabled(false);
		textField.setText(Integer.toString(2*problemSize));
		textField.setBounds(100, 22, 90, 20);
		panel_1.add(textField);
		textField.setColumns(10);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Crossover", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(8, 55, 451, 46);
		panel_1.add(panel_2);
		panel_2.setLayout(null);
		
		lblOperator = new JLabel("Operator");
		lblOperator.setEnabled(false);
		lblOperator.setBounds(10, 21, 44, 14);
		panel_2.add(lblOperator);
		
		comboBox = new JComboBox();
		comboBox.setEnabled(false);
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Edge", "Order", "Partially Mapped"}));
		comboBox.setBounds(92, 18, 90, 20);
		panel_2.add(comboBox);
		
		lblProbability = new JLabel("Probability");
		lblProbability.setEnabled(false);
		lblProbability.setBounds(213, 21, 70, 14);
		panel_2.add(lblProbability);
		
		textField_1 = new JTextField();
		textField_1.setEnabled(false);
		textField_1.setText("0.8");
		textField_1.setBounds(316, 18, 58, 20);
		panel_2.add(textField_1);
		textField_1.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Mutation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(8, 112, 451, 46);
		panel_1.add(panel_3);
		panel_3.setLayout(null);
		
		label = new JLabel("Operator");
		label.setEnabled(false);
		label.setBounds(10, 21, 44, 14);
		panel_3.add(label);
		
		comboBox_1 = new JComboBox();
		comboBox_1.setEnabled(false);
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"Inversion", "Insertion", "Swap", "Swap with next"}));
		comboBox_1.setBounds(92, 18, 90, 20);
		panel_3.add(comboBox_1);
		
		lblProbability_1 = new JLabel("Probability");
		lblProbability_1.setEnabled(false);
		lblProbability_1.setBounds(213, 21, 64, 14);
		panel_3.add(lblProbability_1);
		
		textField_2 = new JTextField();
		textField_2.setEnabled(false);
		textField_2.setText(Float.toString(1f/problemSize));
		textField_2.setBounds(316, 18, 58, 20);
		textField_2.setColumns(10);
		panel_3.add(textField_2);
		
		lblElitism = new JLabel("Elitism");
		lblElitism.setEnabled(false);
		lblElitism.setBounds(18, 178, 46, 14);
		panel_1.add(lblElitism);
		
		textField_3 = new JTextField();
		textField_3.setEnabled(false);
		textField_3.setText("0.1");
		textField_3.setBounds(100, 175, 90, 20);
		panel_1.add(textField_3);
		textField_3.setColumns(10);
		
		textField_4 = new JTextField();
		textField_4.setEnabled(false);
		textField_4.setText("0.0");
		textField_4.setBounds(324, 175, 58, 20);
		panel_1.add(textField_4);
		textField_4.setColumns(10);
		
		lblInnerDepotPenalty = new JLabel("Inner Depot Penalty");
		lblInnerDepotPenalty.setEnabled(false);
		lblInnerDepotPenalty.setBounds(220, 178, 97, 14);
		panel_1.add(lblInnerDepotPenalty);

		return tabGA;
	}
	
	/**
	 * What should be done when the user comes from the previous tab.
	 */
	public void fromPreviousTab(){
		// Update the problem size
		setProblemSize(controlPanel.getODMatrix().getCostMatrix().getSize());
	}
	
	/**
	 * What should be done when the user comes from the next tab.
	 */
	public void fromNextTab(){
		
	}
	
	// Enables / Disables the operators and parameters definitions
	public void enableOperatorsParameters(boolean enable){
		if (enable){
			lblPopulationSize.setEnabled(true);
			textField.setEnabled(true);
			lblOperator.setEnabled(true);
			comboBox.setEnabled(true);
			lblProbability.setEnabled(true);
			textField_1.setEnabled(true);
			label.setEnabled(true);
			comboBox_1.setEnabled(true);
			lblProbability_1.setEnabled(true);
			textField_2.setEnabled(true);
			lblElitism.setEnabled(true);
			textField_3.setEnabled(true);
			lblInnerDepotPenalty.setEnabled(true);
			textField_4.setEnabled(true);
		} else {
			lblPopulationSize.setEnabled(false);
			textField.setEnabled(false);
			lblOperator.setEnabled(false);
			comboBox.setEnabled(false);
			lblProbability.setEnabled(false);
			textField_1.setEnabled(false);
			label.setEnabled(false);
			comboBox_1.setEnabled(false);
			lblProbability_1.setEnabled(false);
			textField_2.setEnabled(false);
			lblElitism.setEnabled(false);
			textField_3.setEnabled(false);
			lblInnerDepotPenalty.setEnabled(false);
			textField_4.setEnabled(false);
		}
	}
	
	// Generate an object with the desired GA parameters
	public OperatorsAndParameters generateGAParameters(){
		int popSize=2*problemSize;
        float elitism=0.1f;
        float mutationProb=1f/problemSize;
        float crossoverProb=0.8f;
        int generations=10000;
		
        // Generate an object with the desired parameters
        OperatorsAndParameters operators = new OperatorsAndParameters();
        
        operators.setPopulationSize(popSize);
        operators.setCrossoverOperator("Edge3.Edge3");
        operators.setCrossoverProb(crossoverProb);
        operators.setMutationOperator("InversionMutation.inversionMutation");
        operators.setMutationProb(mutationProb);
        operators.setReplacementElitism(elitism);
        operators.setInnerDepotPenalty(0.00f);
        
        return operators;
	}
	
	// Set the problem size
	public void setProblemSize(int problemSize) {
		this.problemSize = problemSize;
		textField.setText(Integer.toString(2*problemSize));
	}


	// Get the GA options and run the GA with them
	private void btnNextGAActionPerformed(){
		// Insist that the button has the text "Run!"
		controlPanel.getRun().btnNextTab4.setText("Stop!");
		
		// Create an object with the GA parameters
		params = generateGAParameters();
		
		// Get the list of genes from the Vehicles class
		GeneList geneList = controlPanel.getVehicles().getGeneList();
		
		// Get the cost matrix from the ODMatrix class
		CostMatrix costMatrix = controlPanel.getODMatrix().getCostMatrix();
		
        // Run the genetic algorithm
		Run run = controlPanel.getRun();	// Get the run object
        run.setVRPGARun(new VRPGARun(params, geneList, costMatrix, "", 1, 0));	// Create the object with all we need
        controlPanel.switchToNextTab();		// Switch to the Run tab
        run.go();	// Can go! Can run the GA!
	}
	
	// Just go to the previous tab
	private void btnPreviousGAActionPerformed(){
		controlPanel.switchToPreviousTab();
	}
}
