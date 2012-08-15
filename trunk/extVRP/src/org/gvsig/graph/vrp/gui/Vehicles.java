// TODO: Description of file
// TODO: Date

package org.gvsig.graph.vrp.gui;

//Needed imports
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import org.gvsig.graph.vrp.support.Nodes;
import org.metavrp.Problem;
import org.metavrp.problem.Customer;
import org.metavrp.problem.Depot;
import org.metavrp.problem.Vehicle;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.project.documents.view.gui.View;


//TODO: description of class
public class Vehicles implements Tab {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private Nodes nodes;
	private JPanel tabVehicles;
	private JLabel lblNOfVehicles;
	private JTextField nrHomogeneousVehicles;
	private JLabel lblputIn;
	private JComboBox depot;
	private int depotNumber;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	private JLabel lblVehicleCapacity;
	private JTextField homogeneousVehiclesCapacity;
	private JLabel lblChooseVehiclesLayer;
	private JLabel lblVehicleIdentification;
	private JLabel lblVehiclesCapacities;
	private JRadioButton rdbtnHomogeneousFleet;
	private JRadioButton rdbtnHeterogeneousFleet;
	
	private JComboBox comboBoxVehiclesLayer;
	private FLyrVect selectedLayer;
	private JComboBox comboBoxVehiclesId;
	private String selectedIdField;
	private ArrayList<String> idFieldValues;
	private JComboBox comboBoxVehiclesCapacities;
	private String selectedCapacitiesField;
	private ArrayList<Float> capacitiesFieldValues;
	

	/**
	 * Constructor.
	 * Just initializes the Control Panel on witch this JPanel will be drawn.
	 * @param controlPanel The VRP Control Panel
	 */
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
	
		lblNOfVehicles = new JLabel("N\u00BA of vehicles:");
		lblNOfVehicles.setBounds(63, 84, 70, 14);
		tabVehicles.add(lblNOfVehicles);
		
		// TODO: Test that this is working properly
		nrHomogeneousVehicles = new JFormattedTextField();
		NumberFormatter nf = new NumberFormatter(); // in javax.swing.text
		nf.setMinimum(new Long(1));
		nf.setAllowsInvalid(false);
		nrHomogeneousVehicles = new JFormattedTextField(nf);
		nrHomogeneousVehicles.setText("1");
		nrHomogeneousVehicles.setBounds(176, 81, 86, 20);
		tabVehicles.add(nrHomogeneousVehicles);
		nrHomogeneousVehicles.setColumns(10);
		
		lblputIn = new JLabel("(Put 1 in case of a TSP)");
		lblputIn.setBounds(272, 84, 112, 14);
		tabVehicles.add(lblputIn);
		
		JLabel lblDepotNode = new JLabel("Depot node:");
		lblDepotNode.setBounds(52, 14, 70, 14);
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
		
		homogeneousVehiclesCapacity = new JTextField();
		homogeneousVehiclesCapacity.setBounds(176, 110, 86, 20);
		tabVehicles.add(homogeneousVehiclesCapacity);
		homogeneousVehiclesCapacity.setColumns(10);
		
		rdbtnHomogeneousFleet = new JRadioButton("Homogeneous fleet");
		buttonGroup.add(rdbtnHomogeneousFleet);
		rdbtnHomogeneousFleet.setBounds(31, 54, 176, 23);
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
		rdbtnHeterogeneousFleet.setBounds(31, 137, 176, 23);
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
		comboBoxVehiclesLayer = new JComboBox();
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
		
		comboBoxVehiclesId = new JComboBox();
		comboBoxVehiclesId.setBounds(176, 198, 220, 20);
		comboBoxVehiclesId.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					idFieldValues = getHeterogeneousVehiclesIds();
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
		
		comboBoxVehiclesCapacities = new JComboBox();
		comboBoxVehiclesCapacities.setBounds(176, 230, 220, 20);
		comboBoxVehiclesCapacities.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					capacitiesFieldValues = getHeterogeneousVehiclesCapacities();
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
	 * What should be done when the user comes from the previous tab.
	 */
	public void fromPreviousTab(){
		// TODO: If the user goes back to the previous tab and then again to this one,
		//the chosen options shouldn't be lost, so this method shouldn't be called
		initialSetup();
	}
	
	/**
	 * What should be done when the user comes from the next tab.
	 */
	public void fromNextTab(){
		
	}
	
	/**
	 * Initialize the options
	 */
	public void initialSetup(){
		// Update the list of options (the list of possible nodes) for the depot
		// TODO: Only if the user has changed the Customer's layer
		updateDepotOptions();
		
		// Fill the list of possible vehicle's layers
		fillVehiclesLayers();
		
		// Fill the list of possible vehicles ids
		cleanVehiclesIdsFields();
		
		// Fill the list of possible vehicles capacities
		cleanVehiclesCapacitiesFields();
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
				updateVehiclesIdsFields(selectedLayer);
				updateVehiclesCapacitiesFields(selectedLayer);
			
			// 3. If it is the default index just put the options to the defaults
			} else {
				cleanVehiclesIdsFields();
				cleanVehiclesCapacitiesFields();
				selectedLayer = null;
			}
			
		} catch (ReadDriverException ex){
			ex.printStackTrace();
		}

	}
	
	
	/**
	 * Update the list of possible fields for vehicle's identification
	 */
	public void updateVehiclesIdsFields(FLyrVect layer){
		// 1. Put the options to the default
		cleanVehiclesIdsFields();
		
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
	public void updateVehiclesCapacitiesFields(FLyrVect layer){
		// 1. Put the options to the default
		cleanVehiclesCapacitiesFields();
		
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
	public void cleanVehiclesIdsFields(){
		// 1. Remove all options
		comboBoxVehiclesId.removeAllItems();
		
		// 2. Put just the default one
		comboBoxVehiclesId.addItem(controlPanel._T("Choose_vehicles_id_field"));
	}

	/**
	 * Put the possible values for the vehicles capacities to the default ones
	 */
	public void cleanVehiclesCapacitiesFields(){
		// 3.1. Remove all options
		comboBoxVehiclesCapacities.removeAllItems();
		
		// 3.2. Put just the default one
		comboBoxVehiclesCapacities.addItem(controlPanel._T("Choose_vehicles_capacities_field"));
	}

	
	/**
	 * Gets a list of vehicle ids from the chosen Layer's Field.
	 * Returns an empty list (and shows user errors) in case of empty or repeated values.
	 * @throws ReadDriverException
	 */
	public ArrayList<String> getHeterogeneousVehiclesIds() throws ReadDriverException {
		
		// 0. If the user selected the first field or if the layer wasn't chosen, return empty list
		if (comboBoxVehiclesId.getSelectedIndex()<1 || selectedLayer==null){
			return new ArrayList<String>(0);
		}
		
		// 1. Get the chosen option as a string
		String idField = (String) comboBoxVehiclesId.getSelectedItem();
		
		// 2. Get the chosen field number
		int column = selectedLayer.getRecordset().getFieldIndexByName(idField);
		
		// 3. Verify if there are no repeated or empty values
		// 3.1) Get the number of lines in the layer
		long nrLines = selectedLayer.getRecordset().getRowCount();
		// 3.2) Create a HashSet to verify repetitions on the read elements 
		HashSet<String> bag = new HashSet<String>();
		// 3.3) Create a list to store the read elements
		ArrayList<String> idList = new ArrayList<String>();
		// 3.4) Get the elements one by one from the chosen field
		for (int i=0;i<nrLines;i++){
			String element = selectedLayer.getRecordset().getFieldValue(i,column).toString().trim();
			// If there are any empty value, send warning to the user and get out of this
			if (element.isEmpty()){
				// Select the first option on the ComboBox
				comboBoxVehiclesId.setSelectedIndex(0);
				// Show error message
				controlPanel.showMessageDialog ("Empty_values_in_id_field");
				// Return empty list
				return new ArrayList<String>(0);
			}
			// Add element to the bag and if there are repeated ids, show warning and get out of this
			if (!bag.add(element)){
				// Select the first option on the ComboBox
				comboBoxVehiclesId.setSelectedIndex(0);
				// Show error message
				controlPanel.showMessageDialog ("Repeated_values_in_id_field");
				// Return empty list
				return new ArrayList<String>(0);
			}
			// If the element wasn't empty or repeated, add it to the list of ids
			idList.add(element);
		}
		// Return the list
		return idList;
	}
	
	/**
	 * Gets the capacity values of all the vehicles.
	 * It verifies if the chosen Capacity field is a suitable one. It's suitable if there are no empty (or non-numeric) values.
	 * @throws ReadDriverException 
	 */
	public ArrayList<Float> getHeterogeneousVehiclesCapacities() throws ReadDriverException{
		// 0. If it was selected the first field or if the layer wasn't chosen, just return an empty list
		if (comboBoxVehiclesCapacities.getSelectedIndex()<1 || selectedLayer==null){
			return new ArrayList<Float>(0);
		}

		// 1. Get the chosen option as a string
		String capacityField = (String) comboBoxVehiclesCapacities.getSelectedItem();
		
		// 2. Get the chosen field number
		int column = selectedLayer.getRecordset().getFieldIndexByName(capacityField);
		
		// 3. Verify if there are no empty values
		// 3.1) Get the number of lines in the layer
		long nrLines = selectedLayer.getRecordset().getRowCount();
		// 3.2) Create a list to store the read elements
		ArrayList<Float> capacityList = new ArrayList<Float>();
		// 3.3) Get the elements one by one from the chosen field
		for (int i=0;i<nrLines;i++){
			String element = selectedLayer.getRecordset().getFieldValue(i,column).toString().trim();
			// If there are any empty value, send warning to the user.
			// The value will be treated as 0 and the vehicle will not be used.
			if (element.isEmpty()){
				// Show warning message
				// TODO: should be shown only on time, not on every occurrence of an empty value
				controlPanel.showMessageDialog ("Empty_values_in_capacity_field");
				// Add capacity 0 (zero) to the vehicle
				capacityList.add(0f);
			} else {
				// If the read element can't be cast to float, show alert window and return an empty list
				Float capacity;
				try{
					capacity = Float.valueOf(element);
				} catch (NumberFormatException ex){
					// Select the first option on the ComboBox
					comboBoxVehiclesCapacities.setSelectedIndex(0);
					// Show error message
					controlPanel.showMessageDialog ("Nonnumeric_values_in_capacity_field");
					// Return empty list
					return new ArrayList<Float>(0);
				}
				// If the read element is a float, add it to the list
				capacityList.add(capacity);
			}
		}
		// Return the list of capacities
		return capacityList;
	}
	
	
	/**
	 * Hides the options related to the homogeneous fleet
	 * @param enable Enable the various homogeneous fleet options?
	 */
	public void enableHomogeneousFleetOptions(boolean enable){
		lblVehicleCapacity.setEnabled(enable);
		homogeneousVehiclesCapacity.setEnabled(enable);
		nrHomogeneousVehicles.setEnabled(enable);
		lblNOfVehicles.setEnabled(enable);
		lblputIn.setEnabled(enable);
	}
	
	/**
	 * Hides the options related to the heterogeneous fleet
	 * @param enable Enable the various heterogeneous fleet options?
	 */
	public void enableHeterogeneousFleetOptions(boolean enable){
		lblChooseVehiclesLayer.setEnabled(enable);
		comboBoxVehiclesLayer.setEnabled(enable);
		lblVehicleIdentification.setEnabled(enable);
		comboBoxVehiclesId.setEnabled(enable);
		lblVehiclesCapacities.setEnabled(enable);
		comboBoxVehiclesCapacities.setEnabled(enable);
	}
	
	/**
	 * Set the customers, depots and vehicles on the problem definition.
	 * At the same time create the list of nodes.
	 * @param vehiclesIds
	 * @param vehiclesCapacities
	 */
	public void fillProblemAndNodes(ArrayList<String> vehiclesIds, ArrayList<Float> vehiclesCapacities){
		// Initialize the nodes
		nodes = new Nodes(controlPanel);
		
		// Get the Customers demands
		ArrayList<Float> customersDemands = controlPanel.getCustomers().getCustomersDemand();
		
		// Create an ArrayList of the customers.
		// The customers are all the nodes except the depot 
		ArrayList<Customer> customers = new ArrayList<Customer>(); 
		// Create an ArrayList of the vehicles.
		ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>(); 
		// Create an ArrayList of the depots.
		ArrayList<Depot> depots = new ArrayList<Depot>();
		
		// Iterate through every element of the Origin-Destination Matrix
		for (int i=0; i<controlPanel.getODMatrix().getCostMatrixSize(); i++){
			// Create a new node
			Nodes.Node node = nodes.new Node();
			// Add all the nodes to the customers list except the depot
			if (i != getDepotNumber()){
				Customer customer = new Customer(i,customersDemands.get(i));
				customers.add(customer);
				node.setGene(customer);
				node.setFlag(controlPanel.getODMatrix().getOriginFlags()[i]);
			} else {
				// Add all the vehicles to the vehicles list
				for (int j=0; j<vehiclesIds.size(); j++){
					// TODO: If the vehicle's capacity is 0 (zero) it shouldn't be used at all
					Vehicle vehicle = new Vehicle(getDepotNumber(),vehiclesCapacities.get(j));
					vehicles.add(vehicle);
				}
				
				// Create the depot
				Depot depot = new Depot(i);
				
				// Add the depot to the list of depots
				depots.add(depot);
				
				// Put the Depot's node on the nodes
				node.setGene(depot);
				node.setFlag(controlPanel.getODMatrix().getOriginFlags()[i]);
			}
			nodes.addNode(node);
		}
		
		// Put all this on the Problem class which already has the Cost Matrix added
		Problem problem = controlPanel.getMetavrpProblem();
		problem.setCustomers(customers);
		problem.setVehicles(vehicles);
		problem.setDepots(depots);
	}
	
	
	/*
	 * Getters and Setters
	 */
	
	// Get the number of the depot
	public int getDepotNumber() {
		return depotNumber;
	}

	/**
	 * Get the number of vehicles
	 * @return The number of vehicles
	 */
	// TODO: Remove this (apparently unnecessary) method
	public int getNumVehicles() {
		int num_vehicles=0;
		// 1. If the user chose an heterogeneous fleet, count the number of lines of the declared vehicle's table  
		if (!isHomogeneousFleet()){
			// 1.1. Get the selected layer index
			int selectedLayerIndex = comboBoxVehiclesLayer.getSelectedIndex();
			
			// 1.2. If it's not the default value 
			if (selectedLayerIndex > 0){
				// 2.1. Get the selected layer's name
				String selectedLayerName = comboBoxVehiclesLayer.getSelectedItem().toString();
				
				// 2.2. Get the selected layer
				FLyrVect selectedLayer = (FLyrVect) controlPanel.getMapContext().getLayers().getLayer(selectedLayerName);

				// 2.3. Get the number of rows
				long nrOfRows = 0;
				try {
					nrOfRows = selectedLayer.getRecordset().getRowCount();
				} catch (ReadDriverException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// 2.4. The number of rows is the number of vehicles
				num_vehicles = (int) nrOfRows;
			
			// 3. If it is the default index, return 0 (zero)
			} else {
				num_vehicles=0;
			}
		} else {
		// 2. If the user chose a homogeneous fleet, just return the declared number of vehicles
			try{
				num_vehicles = getNumHomogeneousVehicles();
			} catch (Exception ex){
				controlPanel.showMessageDialog("Empty_nr_homogeneous_vehicles");
			}
		}
		return num_vehicles;
	}
	
	/**
	 * Get the number of vehicles, as defined on the homogeneous fleet
	 * @return The number of vehicles, as defined on the homogeneous fleet
	 * @throws NumberFormatException
	 */
	public int getNumHomogeneousVehicles() throws NumberFormatException{
		return Integer.parseInt(homogeneousVehiclesCapacity.getText());
	}
	
	/**
	 * Get the capacity of the vehicles on the homogeneous fleet 
	 * @return The capacity of the vehicles, as defined on the homogeneous fleet
	 * @throws NumberFormatException
	 */
	public float getHomogeneousVehiclesCapacity() throws NumberFormatException{
		return Float.parseFloat(homogeneousVehiclesCapacity.getText());
	}
	
	/**
	 * Is the chosen vehicle fleet Homogeneous? 
	 * By homogeneous we mean that all the vehicles have the same capacity.
	 * @return True if the user chose an Homogeneous fleet. False otherwise.
	 */
	public boolean isHomogeneousFleet(){
		return rdbtnHomogeneousFleet.isSelected();
	}
	
	/*
	 * "Next" and "Back" Buttons
	 */
	/**
	 * The action performed by the "Next" button.
	 * Sets the Problem related variables (Cost Matrix, Customers, Vehicles and Depot) 
	 */
	private void btnNextVehiclesActionPerformed(){
		// The number of vehicles
		int num_vehicles;
		// The list of vehicle's IDs
		ArrayList<String> vehiclesIds = new ArrayList<String>();
		// The list of vehicle's capacities
		ArrayList<Float> vehiclesCapacities = new ArrayList<Float>();
		
		// 1. Get the depot's node
		depotNumber=0;
		try {
			depotNumber = Integer.parseInt((String)depot.getSelectedItem());
		} catch (NumberFormatException e) {
			controlPanel.showMessageDialog ("Enter_a_valid_depot_number");
			return;
		}
		
		// 2. If it's an homogeneous fleet
		if (isHomogeneousFleet()){
			
			// 2.1 Get the number of vehicles
			try{
				num_vehicles = getNumHomogeneousVehicles();
			} catch (Exception ex){
				// Show alert message
				controlPanel.showMessageDialog("Empty_nr_homogeneous_vehicles");
				// Return from this method. Continues only when the user corrects the input values
				return;
			}
			
			// 2.2 Get vehicle's capacity
			float homogeneousCapacity;
			try{
				homogeneousCapacity = getHomogeneousVehiclesCapacity();
			} catch (Exception ex){
				controlPanel.showMessageDialog("Empty_homogeneous_capacity_value");
				// Return from this method. Continues only when the user corrects the input values
				return;
			}
			
			// 2.3 Fill the 2 lists. One for the vehicle's IDs, other for the vehicle's capacities
			for (int i=0;i<num_vehicles;i++){
				vehiclesIds.add("Vehicle "+i);
				vehiclesCapacities.add(homogeneousCapacity);
			}
		} else {
		// 3. If it's an heterogeneous fleet
			
			// 3.1 If the list of IDs is empty it's because the user didn't chose a field from the list
			if (idFieldValues.isEmpty()){
				controlPanel.showMessageDialog ("Choose_vehicles_id_field");
				return;
			} 

			// 3.2 If the list of Capacities is empty it's because the user didn't chose a field from the list
			if (capacitiesFieldValues.isEmpty()){
				controlPanel.showMessageDialog ("Choose_vehicles_capacities_field");
				return;
			} 
		}
		
		// Create an object with the vrp problem definition
		// The customers and depot need to go to the Nodes too
		fillProblemAndNodes(vehiclesIds, vehiclesCapacities);
		controlPanel.setNodes(nodes);
		
		// Go to the next tab
		controlPanel.switchToNextTab();
	}

	/**
	 * The action performed by the "Back" or "Undo" button.
	 * Go to the previous tab.
	 */
	private void btnPreviousVehiclesActionPerformed(){
		controlPanel.switchToPreviousTab();
	}
}	
