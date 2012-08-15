// TODO: Description of file
// TODO: Date

package org.gvsig.graph.vrp.gui;

//Needed imports
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.metavrp.Problem;
import org.metavrp.algorithm.GeneticAlgorithm;
import org.metavrp.algorithm.GA.VRPGARun;
import org.metavrp.algorithm.GA.operators.OperatorsAndParameters;

//TODO: description of class
public class GA implements Tab {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private JPanel tabGA;
	private org.metavrp.algorithm.GA.operators.OperatorsAndParameters params;
	private JRadioButton gaDefaultSettingsRadioButton;
	private JRadioButton gaAdvancedSettingsRadioButton;
	private ButtonGroup buttonGroup;
	private JTextField populationSizeTextField;
	private JTextField crossoverProbabilityTextField;
	private JTextField mutationProbabilityTextField;
	private JTextField elitismTextField;
	private JTextField overCapacityPenaltyTextField;
	private JLabel populationSizeLabel;
	private JLabel crossoverOperatorLabel;
	private JComboBox crossoverOperatorComboBox;
	private JLabel crossoverProbabilityLabel;
	private JLabel mutationOperatorLabel;
	private JComboBox mutationOperatorComboBox;
	private JLabel mutationProbabilityLabel;
	private JLabel elitismLabel;
	private JLabel overCapacityPenaltyLabel;
	
	private int problemSize=1000;
	

	/**
	 * Constructor.
	 * Initializes the Control Panel on witch this JPanel will be drawn.
	 * @param controlPanel The control panel object
	 */
	public GA(VRPControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}

	
	/**
	 * Initialize Genetic Algorithm's tab.
	 * @wbp.parser.entryPoint
	 */
	public JPanel initTab() {
	
		tabGA = new JPanel();
		tabGA.setLayout(null);
		
		JPanel gaSettingsPanel = new JPanel();
		gaSettingsPanel.setBorder(new TitledBorder(null, "Genetic Algorithm settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		gaSettingsPanel.setBounds(6, 7, 469, 47);
		tabGA.add(gaSettingsPanel);
		gaSettingsPanel.setLayout(null);
		
		buttonGroup = new ButtonGroup();
		
		gaDefaultSettingsRadioButton = new JRadioButton("Default settings", true);
		gaDefaultSettingsRadioButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				enableOperatorsParameters(false);
			}
		});
		buttonGroup.add(gaDefaultSettingsRadioButton);
		gaDefaultSettingsRadioButton.setBounds(122, 17, 101, 23);
		gaSettingsPanel.add(gaDefaultSettingsRadioButton);
		
		gaAdvancedSettingsRadioButton = new JRadioButton("User selected settings (Advanced)");
		gaAdvancedSettingsRadioButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				enableOperatorsParameters(true);
			}
		});
		buttonGroup.add(gaAdvancedSettingsRadioButton);
		gaAdvancedSettingsRadioButton.setBounds(228, 17, 191, 23);
		gaSettingsPanel.add(gaAdvancedSettingsRadioButton);
		
		JButton btnNextGATab = new JButton("Run! >>");
		btnNextGATab.setBounds(386, 278, 89, 23);
		btnNextGATab.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnNextGAActionPerformed();
			}
		});
		tabGA.add(btnNextGATab);
		
		JButton btnPreviousGATab = new JButton("<< Undo");
		btnPreviousGATab.setBounds(287, 278, 89, 23);
		btnPreviousGATab.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnPreviousGAActionPerformed();
			}
		});
		tabGA.add(btnPreviousGATab);
		
		JPanel operatorsAndParametersPanel = new JPanel();
		operatorsAndParametersPanel.setBorder(new TitledBorder(null, "Operators and Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		operatorsAndParametersPanel.setBounds(6, 65, 469, 206);
		tabGA.add(operatorsAndParametersPanel);
		operatorsAndParametersPanel.setLayout(null);
		
		populationSizeLabel = new JLabel("Population Size");
		populationSizeLabel.setEnabled(false);
		populationSizeLabel.setBounds(18, 22, 78, 22);
		operatorsAndParametersPanel.add(populationSizeLabel);
		
		populationSizeTextField = new JTextField();
		populationSizeTextField.setEnabled(false);
		populationSizeTextField.setText(Integer.toString(2*problemSize));
		populationSizeTextField.setBounds(100, 22, 90, 20);
		populationSizeTextField.setColumns(10);
		operatorsAndParametersPanel.add(populationSizeTextField);
		
		JPanel crossoverPanel = new JPanel();
		crossoverPanel.setBorder(new TitledBorder(null, "Crossover", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		crossoverPanel.setBounds(8, 55, 451, 46);
		operatorsAndParametersPanel.add(crossoverPanel);
		crossoverPanel.setLayout(null);
		
		crossoverOperatorLabel = new JLabel("Operator");
		crossoverOperatorLabel.setEnabled(false);
		crossoverOperatorLabel.setBounds(10, 21, 44, 14);
		crossoverPanel.add(crossoverOperatorLabel);
		
		crossoverOperatorComboBox = new JComboBox();
		crossoverOperatorComboBox.setEnabled(false);
		crossoverOperatorComboBox.setModel(new DefaultComboBoxModel(new String[] {"Edge", "Order", "Partially Mapped"}));
		crossoverOperatorComboBox.setBounds(92, 18, 90, 20);
		crossoverPanel.add(crossoverOperatorComboBox);
		
		crossoverProbabilityLabel = new JLabel("Probability");
		crossoverProbabilityLabel.setEnabled(false);
		crossoverProbabilityLabel.setBounds(213, 21, 70, 14);
		crossoverPanel.add(crossoverProbabilityLabel);
		
		crossoverProbabilityTextField = new JTextField();
		crossoverProbabilityTextField.setEnabled(false);
		crossoverProbabilityTextField.setText("0.8");
		crossoverProbabilityTextField.setBounds(326, 18, 58, 20);
		crossoverPanel.add(crossoverProbabilityTextField);
		crossoverProbabilityTextField.setColumns(10);
		
		JPanel mutationPanel = new JPanel();
		mutationPanel.setBorder(new TitledBorder(null, "Mutation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mutationPanel.setBounds(8, 112, 451, 46);
		operatorsAndParametersPanel.add(mutationPanel);
		mutationPanel.setLayout(null);
		
		mutationOperatorLabel = new JLabel("Operator");
		mutationOperatorLabel.setEnabled(false);
		mutationOperatorLabel.setBounds(10, 21, 44, 14);
		mutationPanel.add(mutationOperatorLabel);
		
		mutationOperatorComboBox = new JComboBox();
		mutationOperatorComboBox.setEnabled(false);
		mutationOperatorComboBox.setModel(new DefaultComboBoxModel(new String[] {"Inversion", "Insertion", "Swap", "Swap with next"}));
		mutationOperatorComboBox.setBounds(92, 18, 90, 20);
		mutationPanel.add(mutationOperatorComboBox);
		
		mutationProbabilityLabel = new JLabel("Probability");
		mutationProbabilityLabel.setEnabled(false);
		mutationProbabilityLabel.setBounds(213, 21, 64, 14);
		mutationPanel.add(mutationProbabilityLabel);
		
		mutationProbabilityTextField = new JTextField();
		mutationProbabilityTextField.setEnabled(false);
		mutationProbabilityTextField.setText(Float.toString(1f/problemSize));
		mutationProbabilityTextField.setBounds(326, 18, 58, 20);
		mutationProbabilityTextField.setColumns(10);
		mutationPanel.add(mutationProbabilityTextField);
		
		elitismLabel = new JLabel("Elitism");
		elitismLabel.setEnabled(false);
		elitismLabel.setBounds(18, 178, 46, 14);
		operatorsAndParametersPanel.add(elitismLabel);
		
		elitismTextField = new JTextField();
		elitismTextField.setEnabled(false);
		elitismTextField.setText("0.1");
		elitismTextField.setBounds(100, 175, 90, 20);
		operatorsAndParametersPanel.add(elitismTextField);
		elitismTextField.setColumns(10);
		
		overCapacityPenaltyTextField = new JTextField();
		overCapacityPenaltyTextField.setEnabled(false);
		overCapacityPenaltyTextField.setText("0.0");
		overCapacityPenaltyTextField.setBounds(334, 175, 58, 20);
		operatorsAndParametersPanel.add(overCapacityPenaltyTextField);
		overCapacityPenaltyTextField.setColumns(10);
		
		overCapacityPenaltyLabel = new JLabel("Over Capacity Penalty");
		overCapacityPenaltyLabel.setEnabled(false);
		overCapacityPenaltyLabel.setBounds(220, 178, 110, 14);
		operatorsAndParametersPanel.add(overCapacityPenaltyLabel);

		return tabGA;
	}
	
	/**
	 * What should be done when the user comes from the previous tab.
	 */
	public void fromPreviousTab(){
		// Update the problem size
		// TODO: If the user was already on this tab, this value shouldn't be altered
		setProblemSize(controlPanel.getODMatrix().getCostMatrix().getSize());
	}
	
	/**
	 * What should be done when the user comes from the next tab.
	 */
	public void fromNextTab(){
		
	}
	

	/**
	 * Enables or Disables the GA's operators and parameters definitions
	 * @param enable Should the GA's operators and parameter be enabled?
	 */
	public void enableOperatorsParameters(boolean enable){
		if (enable){
			populationSizeLabel.setEnabled(true);
			populationSizeTextField.setEnabled(true);
			crossoverOperatorLabel.setEnabled(true);
			crossoverOperatorComboBox.setEnabled(true);
			crossoverProbabilityLabel.setEnabled(true);
			crossoverProbabilityTextField.setEnabled(true);
			mutationOperatorLabel.setEnabled(true);
			mutationOperatorComboBox.setEnabled(true);
			mutationProbabilityLabel.setEnabled(true);
			mutationProbabilityTextField.setEnabled(true);
			elitismLabel.setEnabled(true);
			elitismTextField.setEnabled(true);
			overCapacityPenaltyLabel.setEnabled(true);
			overCapacityPenaltyTextField.setEnabled(true);
		} else {
			populationSizeLabel.setEnabled(false);
			populationSizeTextField.setEnabled(false);
			crossoverOperatorLabel.setEnabled(false);
			crossoverOperatorComboBox.setEnabled(false);
			crossoverProbabilityLabel.setEnabled(false);
			crossoverProbabilityTextField.setEnabled(false);
			mutationOperatorLabel.setEnabled(false);
			mutationOperatorComboBox.setEnabled(false);
			mutationProbabilityLabel.setEnabled(false);
			mutationProbabilityTextField.setEnabled(false);
			elitismLabel.setEnabled(false);
			elitismTextField.setEnabled(false);
			overCapacityPenaltyLabel.setEnabled(false);
			overCapacityPenaltyTextField.setEnabled(false);
		}
	}
	
	/**
	 * Get the default operators and parameter values
	 * @return The default GA's operators and parameters
	 */
	public OperatorsAndParameters getDefaultOperatorsAndParameters(){
		int popSize=2*problemSize;
        float elitism=0.1f;
        float mutationProb=1f/problemSize;
        float crossoverProb=0.8f;
		
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

	
	/**
	 * Get the user specified operators and parameter values
	 * @return The user defined GA's operators and parameters
	 */
	public OperatorsAndParameters getUserDefinedOperatorsAndParameters(){
		// 1. Get all the necessary operators and parameter values 
		// 1.1 Get population size
		int popSize;
		try{
			popSize = Integer.parseInt(populationSizeTextField.getText());
		} catch (NumberFormatException ex){
			// If the text is malformed send error to user and use 2*problemSize
			controlPanel.showMessageDialog("Population_size_malformed");
			popSize = 2 * problemSize;
		}
		// If the population size is lower than 2, send alert to user and use the value 2*problemSize
		if(popSize<2){
			controlPanel.showMessageDialog("Population_size_lower_than_2");
			popSize = 2 * problemSize;
		}
		
		// 1.2 Get crossover operator
		String crossoverOperator = (String) crossoverOperatorComboBox.getSelectedItem();
		if (crossoverOperator.equalsIgnoreCase("Edge")){
			crossoverOperator="Edge3.Edge3";
		} else if (crossoverOperator.equalsIgnoreCase("Order")){
			crossoverOperator="Order1.Order1";
		} else {
			crossoverOperator="PMX.PMX";
		}
		
		// 1.3 Get crossover probability
		float crossoverProbability;
		try{
			crossoverProbability = Float.parseFloat(crossoverProbabilityTextField.getText());
		} catch(NumberFormatException ex){
			// If the text is malformed send error to user and use 0.8
			controlPanel.showMessageDialog("Crossover_probability_malformed");
			crossoverProbability = 0.8f;
		}
		// If the chosen value is lower than 0, show alert to the user and use the value 0
		if (crossoverProbability<0){
			controlPanel.showMessageDialog("Crossover_probability_lower_than_zero");
			crossoverProbability = 0f;
		}
		// If the chosen value is higher than 1, show alert to the user and use the value 1 
		if(crossoverProbability>1){
			controlPanel.showMessageDialog("Crossover_probability_higher_than_one");
			crossoverProbability = 1f;
		}
		
		// 1.4 Get mutation operator
		String mutationOperator = (String) mutationOperatorComboBox.getSelectedItem();
		if (mutationOperator.equalsIgnoreCase("Inversion")){
			mutationOperator="InversionMutation.inversionMutation";
		} else if (mutationOperator.equalsIgnoreCase("Insertion")){
			mutationOperator="InsertMutation.insertMutation";
		} else if (mutationOperator.equalsIgnoreCase("Swap")){
			mutationOperator="SwapMutation.swapMutation";
		} else {
			mutationOperator="SwapNextMutation.swapNextMutation";
		}
		
		// 1.5 Get mutation probability
		float mutationProbability;
		try{
			mutationProbability = Float.parseFloat(mutationProbabilityTextField.getText());
		} catch(NumberFormatException ex){
			// If the text is malformed send error to user and use the value 1/problemSize
			controlPanel.showMessageDialog("Mutation_probability_malformed");
			mutationProbability = 1f/problemSize;
		}
		// If the chosen value is lower than 0, show alert to the user and use the value 0
		if (mutationProbability<0){
			controlPanel.showMessageDialog("Mutation_probability_lower_than_zero");
			mutationProbability = 0f;
		}
		// If the chosen value is higher than 1, show alert to the user and use the value 1
		if(mutationProbability>1){
			controlPanel.showMessageDialog("Mutation_probability_highter_than_one");
			mutationProbability = 1f;
		}
		
		// 1.6 Get the elitism value
		float elitism;
		try{
			elitism = Float.parseFloat(elitismTextField.getText());
		} catch(NumberFormatException ex){
			// If the text is malformed send error to user and use the value 0.1
			controlPanel.showMessageDialog("Elitism_malformed");
			elitism = 0.1f;
		}
		// If the chosen value is lower than 0, show alert to the user and use the value 0
		if (elitism<0){
			controlPanel.showMessageDialog("Elitism_lower_than_zero");
			elitism = 0f;
		}
		// If the chosen value is higher or equal to 1, show alert to the user and use the value 1
		if(mutationProbability>1){
			controlPanel.showMessageDialog("Elitism_equal_or_highter_than_one");
			elitism = 1f;
		}
		
		// 1.7 Get the over capacity penalty value
		float overCapacityPenalty;
		try{
			overCapacityPenalty = Float.parseFloat(overCapacityPenaltyTextField.getText());
		} catch(NumberFormatException ex){
			// If the text is malformed send error to user and use the value 0
			controlPanel.showMessageDialog("Over_capacity_penalty_malformed");
			overCapacityPenalty = 0f;
		}
		// If the chosen value is lower than 0, show alert to the user and use the value 0
		if (elitism<0){
			controlPanel.showMessageDialog("Over_capacity_penalty_lower_than_zero");
			overCapacityPenalty = 0f;
		}
		
		// 2. All the values have been obtained. Now create the OperatorsAndParameters object and return it
        OperatorsAndParameters operators = new OperatorsAndParameters();
        
        operators.setPopulationSize(popSize);
        operators.setCrossoverOperator(crossoverOperator);
        operators.setCrossoverProb(crossoverProbability);
        operators.setMutationOperator(mutationOperator);
        operators.setMutationProb(mutationProbability);
        operators.setReplacementElitism(elitism);
        operators.setInnerDepotPenalty(overCapacityPenalty);

        return operators;
	}
	

	/**
	 * Set the problem size (nº of customers + nº of depots)
	 * @param problemSize Integer specifying the size of the problem (nº of customers + nº of depots)
	 */
	// TODO: Shouldn't be the nº of customers + nº of depots + nº of vehicles?
	public void setProblemSize(int problemSize) {
		this.problemSize = problemSize;
		populationSizeTextField.setText(Integer.toString(2*problemSize));
		mutationProbabilityTextField.setText(Float.toString(1f/problemSize));
	}


	/**
	 * The action performed by the "Next" button.
	 * Get all the GA options and run the GA.
	 */
	private void btnNextGAActionPerformed(){
		// 1. Get the problem definition from the Vehicles tab
		Problem problem = controlPanel.getVehicles().getProblem();
		
		// 2. Create the Genetic Algorithm Object
		GeneticAlgorithm ga;
		if (gaDefaultSettingsRadioButton.isSelected()){
			ga = new GeneticAlgorithm(getDefaultOperatorsAndParameters(), problem);
		} else {
			ga = new GeneticAlgorithm(getUserDefinedOperatorsAndParameters(), problem);
		}
		
        // 3. Run the Genetic Algorithm
		Run run = controlPanel.getRun();	// Get the run object
        run.setVRPGARun(new VRPGARun(ga, problem.getCostMatrix(), "", 1, 0));	// Create the object with all we need
        controlPanel.switchToNextTab();		// Switch to the Run tab
        run.go();	// Can go! Can run the GA!
	}
	
	/**
	 * The action performed by the "Back" or "Undo" button.
	 * Go to the previous tab
	 */
	private void btnPreviousGAActionPerformed(){
		controlPanel.switchToPreviousTab();
	}
}
