// TODO: Description of file
// TODO: Date

package org.gvsig.graph.vrp.gui;

//Needed imports
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gvsig.graph.core.Network;
import org.gvsig.graph.vrp.support.Nodes;
import org.metavrp.GA.*;
import org.metavrp.GA.support.*;
import org.metavrp.VRP.*;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.view.gui.View;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;


//TODO: description of class
public class Vehicles {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private GeneList geneList;
	private Nodes nodes;
	private JPanel tabVehicles;
	private JTextField nVehicles;
	private JComboBox<String> depot;
	private int depotNumber;
	private int numVehicles;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	private JLabel lblVehicleCapacity;
	private JTextField textField;
	private JLabel lblChooseVehiclesLayer;
	private JLabel lblVehicleIdentification;
	private JLabel lblVehiclesCapacities;
	private JRadioButton rdbtnHomogeneousFleet;
	private JRadioButton rdbtnHeterogeneousFleet;
	
	private JComboBox<String> comboBoxVehiclesLayer;
	private FLyrVect selectedLayer;
	private JComboBox<String> comboBoxVehiclesId;
	private String selectedIdField;
	private JComboBox<String> comboBoxVehiclesCapacities;
	private String selectedCapacitiesField;
	
	// Constructor.
	// Just initializes the Control Panel on witch this JPanel will be drawn.
	public Vehicles(VRPControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}
	
	/**
	 * Initialize Vehicles tab.
	 * @wbp.parser.entryPoint
	 */
	public JPanel initTab() {

		tabVehicles = new JPanel();
		tabVehicles.setLayout(null);
	
		JLabel lblNOfVehicles = new JLabel("N\u00BA of vehicles:");
		lblNOfVehicles.setBounds(63, 45, 70, 14);
		tabVehicles.add(lblNOfVehicles);
		
		// TODO: Test that this is working properly
		nVehicles = new JFormattedTextField();
		NumberFormatter nf = new NumberFormatter(); // in javax.swing.text
		nf.setMinimum(new Long(1));
		nf.setAllowsInvalid(false);
		nVehicles = new JFormattedTextField(nf);
		nVehicles.setText("1");
		nVehicles.setBounds(176, 42, 86, 20);
		tabVehicles.add(nVehicles);
		nVehicles.setColumns(10);
		
		JLabel lblputIn = new JLabel("(Put 1 in case of a TSP)");
		lblputIn.setBounds(272, 45, 112, 14);
		tabVehicles.add(lblputIn);
		
		JLabel lblDepotNode = new JLabel("Depot node:");
		lblDepotNode.setBounds(63, 14, 70, 14);
		tabVehicles.add(lblDepotNode);
		
		depot = new JComboBox();
		depot.setEditable(true);
		depot.setBounds(176, 11, 86, 20);
		tabVehicles.add(depot);
		
		JButton btnNextTab2 = new JButton("Next >>");
		btnNextTab2.setBounds(386, 278, 89, 23);
		btnNextTab2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnNextVehiclesActionPerformed();
			}
		});
		tabVehicles.add(btnNextTab2);
		
		JButton btnPreviousTab2 = new JButton("<< Undo");
		btnPreviousTab2.setBounds(287, 278, 89, 23);
		btnPreviousTab2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnPreviousVehiclesActionPerformed();
			}
		});
		tabVehicles.add(btnPreviousTab2);
		
		lblVehicleCapacity = new JLabel("Vehicle Capacity:");
		lblVehicleCapacity.setBounds(62, 112, 86, 17);
		tabVehicles.add(lblVehicleCapacity);
		
		textField = new JTextField();
		textField.setBounds(176, 110, 86, 20);
		tabVehicles.add(textField);
		textField.setColumns(10);
		
		rdbtnHomogeneousFleet = new JRadioButton("Homogeneous fleet");
		buttonGroup.add(rdbtnHomogeneousFleet);
		rdbtnHomogeneousFleet.setBounds(42, 83, 176, 23);
		rdbtnHomogeneousFleet.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				enableHomogeneousFleetOptions(true);
				enableHeterogeneousFleetOptions(false);
			}
		});
		enableHomogeneousFleetOptions(true);
		rdbtnHomogeneousFleet.setSelected(true);
		tabVehicles.add(rdbtnHomogeneousFleet);
		
		rdbtnHeterogeneousFleet = new JRadioButton("Heterogeneous fleet");
		buttonGroup.add(rdbtnHeterogeneousFleet);
		rdbtnHeterogeneousFleet.setBounds(42, 137, 176, 23);
		rdbtnHeterogeneousFleet.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				enableHomogeneousFleetOptions(false);
				enableHeterogeneousFleetOptions(true);
			}
		});
		tabVehicles.add(rdbtnHeterogeneousFleet);
		
		lblChooseVehiclesLayer = new JLabel("Vehicle's layer:");
		lblChooseVehiclesLayer.setBounds(62, 167, 86, 20);
		tabVehicles.add(lblChooseVehiclesLayer);
		
		// ComboBox with all the layers.
		// TODO: what about getting the vehicles information from a table? (In the start menu)
		comboBoxVehiclesLayer = new JComboBox<String>();
		comboBoxVehiclesLayer.setBounds(176, 167, 220, 20);
		comboBoxVehiclesLayer.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					updateVehiclesLayers();
				}
			});
		tabVehicles.add(comboBoxVehiclesLayer);
		
		lblVehicleIdentification = new JLabel("Vehicle's Id:");
		lblVehicleIdentification.setBounds(62, 200, 89, 17);
		tabVehicles.add(lblVehicleIdentification);
		
		comboBoxVehiclesId = new JComboBox<String>();
		comboBoxVehiclesId.setBounds(176, 198, 220, 20);
		comboBoxVehiclesId.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					verifyIdField(comboBoxVehiclesId);
				} catch (ReadDriverException e) {
					controlPanel.showMessageDialog ("Error_accessing_layer_data");
					e.printStackTrace();
				}
			}
		});
		tabVehicles.add(comboBoxVehiclesId);
		
		lblVehiclesCapacities = new JLabel("Vehicle's capacities:");
		lblVehiclesCapacities.setBounds(62, 233, 100, 14);
		tabVehicles.add(lblVehiclesCapacities);
		
		comboBoxVehiclesCapacities = new JComboBox<String>();
		comboBoxVehiclesCapacities.setBounds(176, 230, 220, 20);
		comboBoxVehiclesCapacities.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					verifyCapacitiesField(comboBoxVehiclesCapacities);
				} catch (ReadDriverException e) {
					controlPanel.showMessageDialog ("Error_accessing_layer_data");
					e.printStackTrace();
				}
			}
		});
		tabVehicles.add(comboBoxVehiclesCapacities);
		
		enableHeterogeneousFleetOptions(false);
	
		return tabVehicles;
	}
	
	/**
	 * Initialize the options
	 * 
	 */
	// TODO: Verify if we can memorize the users chosen options. If he goes back to the previous tab and then again to this one,
	//the chosen options shouldn't be lost 
	public void initialSetup(){
		// Update the list of options (the list of possible nodes) for the depot
		updateDepotOptions();
		
		// Fill the list of possible vehicle's layers
		// TODO: If the user goes back from a next tab, to the previous one, without changing any option,
		// these comboBoxes should stay with the already chosen options. They shouldn't be initalized again.
		fillVehiclesLayers();
		
		// Fill the list of possible vehicles ids
		cleanVehiclesIds();
		
		// Fill the list of possible vehicles capacities
		cleanVehiclesCapacities();
	}
	
	
	/**
	 *  Create the list of options for the depot
	 */
	public void updateDepotOptions(){
		for (int i=0;i<controlPanel.getODMatrix().getCostMatrixSize();i++){
			depot.addItem(""+i+"");
		}
		depot.setEditable(true);
		depot.setSelectedIndex(0);
	}
	
	/**
	 * Fill the list of possible layers for the vehicles
	 */
	private void fillVehiclesLayers(){
		// 1. Remove all previous items
		comboBoxVehiclesLayer.removeAllItems();
		
		// 2. Add a first (and default) option
		comboBoxVehiclesLayer.addItem(controlPanel._T("Choose_vehicles_layer"));
		
		// 3. Get the windows
		IWindow[] windows = PluginServices.getMDIManager().getAllWindows();
		
		// 4. For every window
		for (int i=0; i<windows.length; i++){
			// 4. a). If it is a view,
			if (windows[i] instanceof View){
				View v = (View) windows[i];
				MapContext map = v.getMapControl().getMapContext();
				SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
				// Iterate throught the layers putting their names on the combo box 
				while (it.hasNext()){
					FLayer layer = it.next();
					comboBoxVehiclesLayer.addItem(layer.getName());
				}
			}
			
			// 4. b). If it's a table,
			// TODO
//			if (windows[i] instanceof Table){
//				Table t = (Table) windows[i];
//				t.getModel().getAssociatedTable().getRecordset();
//
//			}
		}
	}
	
	/**
	 * Change the selected layer for the vehicles
	 */
	private void updateVehiclesLayers(){
		int selectedLayerIndex = 0;
		try{
			// 1. Get the selected element
			selectedLayerIndex = comboBoxVehiclesLayer.getSelectedIndex();
			
			// 2. If it's not the default value 
			if (selectedLayerIndex > 0){
				// 2.1. Get the selected layer's name
				String selectedLayerName = comboBoxVehiclesLayer.getSelectedItem().toString();
				
				// 2.2. Verify that the selected layer has two or more fields, otherwise give an alert to the user 
//				String selectedLayerName = (String) comboBoxVehiclesLayer.getSelectedItem();
				selectedLayer = (FLyrVect) controlPanel.getMapContext().getLayers().getLayer(selectedLayerName);

				int nrOfFields = selectedLayer.getRecordset().getFieldCount();
				if (nrOfFields<2){
					controlPanel.showMessageDialog ("Layer_with_fewer_than_two_fields");
					// Then go to the default (first) option
					// TODO: Should go to the previous selected layer
					comboBoxVehiclesLayer.setSelectedIndex(0);
				} 
				
				// 2.2. Update the fields
				updateVehiclesIds(selectedLayer);
				updateVehiclesCapacities(selectedLayer);
			
			// 3. If it is the default index just put the options to the defaults
			} else {
				cleanVehiclesIds();
				cleanVehiclesCapacities();
				selectedLayer = null;
			}
			
		} catch (ReadDriverException ex){
			ex.printStackTrace();
		}

	}
	
	
	/**
	 * Update the list of possible fields for vehicle's identification
	 */
	public void updateVehiclesIds(FLyrVect layer){
		// 1. Put the options to the default
		cleanVehiclesIds();
		
		// 2. Fill the options with the fields of the selected layer
		try{
			String[] fieldNames = layer.getRecordset().getFieldNames();
			for (int i=0; i<fieldNames.length; i++){
				comboBoxVehiclesId.addItem(fieldNames[i]);
			}
		} catch (ReadDriverException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Update the list of possible fields for the vehicles capacities
	 */
	public void updateVehiclesCapacities(FLyrVect layer){
		// 1. Put the options to the default
		cleanVehiclesCapacities();
		
		// 2. Fill the options with the fields of the selected layer
		try{
			String[] fieldNames = layer.getRecordset().getFieldNames();
			for (int i=0; i<fieldNames.length; i++){
				comboBoxVehiclesCapacities.addItem(fieldNames[i]);
			}
		} catch (ReadDriverException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Put the possible values for the vehicles Ids to the default ones 
	 */
	public void cleanVehiclesIds(){
		// 1. Remove all options
		comboBoxVehiclesId.removeAllItems();
		
		// 2. Put just the default one
		comboBoxVehiclesId.addItem(controlPanel._T("Choose_vehicles_id_field"));
	}

	/**
	 * Put the possible values for the vehicles capacities to the default ones
	 */
	public void cleanVehiclesCapacities(){
		// 3.1. Remove all options
		comboBoxVehiclesCapacities.removeAllItems();
		
		// 3.2. Put just the default one
		comboBoxVehiclesCapacities.addItem(controlPanel._T("Choose_vehicles_capacities_field"));
	}
	
	
	/**
	 * Verifies if the chosen ID field is a suitable one.
	 * It's suitable if there are no repeated values and no empty (or non-numeric) ones.
	 * @throws ReadDriverException 
	 */
	public void verifyIdField(JComboBox<String> comboBoxVehiclesId) throws ReadDriverException {
		// 0. If it was selected the first field or if the layer wasn't chosen, just exit
		if (comboBoxVehiclesId.getSelectedIndex()<1 || selectedLayer==null){
			return;
		}
		
		// 1. Get the chosen option as a string
		String idField = (String) comboBoxVehiclesId.getSelectedItem();
		
		// 2. Get the chosen field number
		int column = selectedLayer.getRecordset().getFieldIndexByName(idField);
		
		// 3. Verify if there are no repeated or empty values
		// 3.1) Get the number of lines in the layer
		long nrLines = selectedLayer.getRecordset().getRowCount();
		// 3.2) Create a HashSet to store the read elements
		HashSet<String> elements = new HashSet<String>();
		// 3.3) get the elements one by one from the chosen field
		for (int i=0;i<nrLines;i++){
			String element = selectedLayer.getRecordset().getFieldValue(i,column).toString().toLowerCase().trim();
			// If there are any empty value, send warning to the user and get out of this
			if (element.isEmpty()){
				// Select the first option on the ComboBox
				comboBoxVehiclesId.setSelectedIndex(0);
				// Show error message
				controlPanel.showMessageDialog ("Empty_values_in_id_field");
				// Return
				return;
			}
			// Add element and if there are repeated ids, show warning and get out of this
			if (!elements.add(element)){
				// Select the first option on the ComboBox
				comboBoxVehiclesId.setSelectedIndex(0);
				// Show error message
				controlPanel.showMessageDialog ("Repeated_values_in_id_field");
				// Return
				return;
			}
		}
	}
	
	/**
	 * Verifies if the chosen Capacity field is a suitable one.
	 * It's suitable if there are no empty (or non-numeric) values
	 * @throws ReadDriverException 
	 */
	public void verifyCapacitiesField(JComboBox<String> comboBoxVehiclesCapacities) throws ReadDriverException{
		// 0. If it was selected the first field or if the layer wasn't chosen, just exit
		if (comboBoxVehiclesCapacities.getSelectedIndex()<1 || selectedLayer==null){
			return;
		}
		
		// 1. Get the chosen option as a string
		String capacityField = (String) comboBoxVehiclesCapacities.getSelectedItem();
		
		// 2. Get the chosen field number
		int column = selectedLayer.getRecordset().getFieldIndexByName(capacityField);
		
		// 3. Verify if there are no empty values
		// 3.1) Get the number of lines in the layer
		long nrLines = selectedLayer.getRecordset().getRowCount();
		// 3.2) get the elements one by one from the chosen field
		for (int i=0;i<nrLines;i++){
			String element = selectedLayer.getRecordset().getFieldValue(i,column).toString().toLowerCase().trim();
			// If there are any empty value, send warning to the user.
			// The value will be treated as 0 and the vehicle will not be used.
			if (element.isEmpty()){
				// Show warning message
				controlPanel.showMessageDialog ("Empty_values_in_capacity_field");
			}
			// If the read element can't be cast to float, show alert window.
			try{
				Float.valueOf(element);
			} catch (NumberFormatException ex){
				// Select the first option on the ComboBox
				comboBoxVehiclesCapacities.setSelectedIndex(0);
				// Show error message
				controlPanel.showMessageDialog ("Nonnumeric_values_in_capacity_field");
				// Return
				return;
			}
		}
	}
	
	
	/**
	 * Hides the options related to the homogeneous fleet
	 */
	public void enableHomogeneousFleetOptions(boolean enable){
		lblVehicleCapacity.setEnabled(enable);
		textField.setEnabled(enable);
	}
	
	/**
	 * Hides the options related to the heterogeneous fleet
	 */
	public void enableHeterogeneousFleetOptions(boolean enable){
		lblChooseVehiclesLayer.setEnabled(enable);
		comboBoxVehiclesLayer.setEnabled(enable);
		lblVehicleIdentification.setEnabled(enable);
		comboBoxVehiclesId.setEnabled(enable);
		lblVehiclesCapacities.setEnabled(enable);
		comboBoxVehiclesCapacities.setEnabled(enable);
	}
	
	// Create the list of possible genes (vehicles and customers) 
	// At the same time create the list of nodes.
	public void createGeneListAndNodes(){
		// Initialize the nodes
		nodes = new Nodes(controlPanel);
		
		// Create an ArrayList of the customers.
		// The customers are all the nodes except the depot 
		ArrayList<Customer> customers = new ArrayList<Customer>(); 
		// Create an ArrayList of the vehicles.
		ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>(); 
		for (int i=0; i<controlPanel.getODMatrix().getCostMatrixSize(); i++){
			Nodes.Node node = nodes.new Node();
			// Add all the nodes to the customers list except the depot
			if (i != getDepotNumber()){
				Customer customer = new Customer(i,i);
				customers.add(customer);
				node.setGene(customer);
				node.setFlag(controlPanel.getODMatrix().getOriginFlags()[i]);
			} else {
				for (int j=1; j<=getNumVehicles(); j++){
					// TODO: Change this to accommodate depots natively on the representation of the chromosome (in metaVRP).
					Vehicle vehicle = new Vehicle(-j, getDepotNumber());
					vehicles.add(vehicle);
					node.setGene(vehicle);
					node.setFlag(controlPanel.getODMatrix().getOriginFlags()[i]);
				}
			}
			nodes.addNode(node);
		}
		
		// The list of genes is now initialized
		geneList = new GeneList(customers, vehicles);
	}
	
	
	/*
	 * Getters and Setters
	 */
	// Get the number of the depot
	public int getDepotNumber() {
		return depotNumber;
	}

	// Get the number of vehicles
	public int getNumVehicles() {
		return numVehicles;
	}
	
	// Get the list of genes (vehicles and customers)
	public GeneList getGeneList(){
		return this.geneList;
	}
	
	/*
	 * "Next" and "Back" Buttons
	 */
	
	// Get the variables (vehicles and depot) 
	private void btnNextVehiclesActionPerformed(){
		// Get number of vehicles
		numVehicles=1;
		try {
			numVehicles = Integer.parseInt(nVehicles.getText());
		} catch (NumberFormatException e) {
			controlPanel.showMessageDialog ("Please_enter_a_valid_number");
			return;
		}
		// Get the depot's node
		depotNumber=0;
		try {
			depotNumber = Integer.parseInt((String)depot.getSelectedItem());
		} catch (NumberFormatException e) {
			controlPanel.showMessageDialog ("Please_enter_a_valid_number");
			return;
		}

		// Create an object with the list of genes (vehicles and customers) to use on the Genetic Algorithm
		// The customers and depot need to go to the Nodes too
		createGeneListAndNodes();
		controlPanel.setNodes(nodes);
		
		// Update the problem size
		controlPanel.getGA().setProblemSize(controlPanel.getODMatrix().getCostMatrix().getSize());
		
		// Go to the next tab
		controlPanel.switchToNextTab();
	}

	// Just go to the previous tab
	private void btnPreviousVehiclesActionPerformed(){
		controlPanel.switchToPreviousTab();
	}
}	
