// TODO: Description of file
// TODO: Date

package org.gvsig.graph.vrp.gui;

//Needed imports
import java.util.ArrayList;
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

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
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
	private JComboBox<String> comboBox;
	private JComboBox<String> comboBox_1;
	private JComboBox<String> comboBox_2;
		
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
		
		JRadioButton rdbtnHomogeneousFleet = new JRadioButton("Homogeneous fleet");
		buttonGroup.add(rdbtnHomogeneousFleet);
		rdbtnHomogeneousFleet.setBounds(42, 83, 176, 23);
		rdbtnHomogeneousFleet.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				enableHomogeneousFleetOptions(true);
				enableHeterogeneousFleetOptions(false);
			}
		});
		rdbtnHomogeneousFleet.setSelected(true);
		enableHomogeneousFleetOptions(true);
		tabVehicles.add(rdbtnHomogeneousFleet);
		
		JRadioButton rdbtnHeterogeneousFleet = new JRadioButton("Heterogeneous fleet");
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
		
		comboBox = new JComboBox<String>();
		comboBox.setBounds(176, 167, 220, 20);
		comboBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					int selectedFieldIndex = 0;
					try{
						String selectedField = comboBox.getSelectedItem().toString();
						selectedFieldIndex = controlPanel.getODMatrix().getLayerOrigins().getRecordset().getFieldIndexByName(selectedField);
					} catch (ReadDriverException ex){
						ex.printStackTrace();
					}
				
					grabCustomersDemand(selectedFieldIndex);
				}
			});
		tabVehicles.add(comboBox);
		
		lblVehicleIdentification = new JLabel("Vehicle's Id:");
		lblVehicleIdentification.setBounds(62, 200, 89, 17);
		tabVehicles.add(lblVehicleIdentification);
		
		comboBox_1 = new JComboBox<String>();
		comboBox_1.setBounds(176, 198, 220, 20);
		tabVehicles.add(comboBox_1);
		
		lblVehiclesCapacities = new JLabel("Vehicle's capacities:");
		lblVehiclesCapacities.setBounds(62, 233, 100, 14);
		tabVehicles.add(lblVehiclesCapacities);
		
		comboBox_2 = new JComboBox<String>();
		comboBox_2.setBounds(176, 230, 220, 20);
		tabVehicles.add(comboBox_2);
		
		enableHeterogeneousFleetOptions(false);
	
		return tabVehicles;
	}
	
	/**
	 * Initialize the options
	 */
	public void initialSetup(){
		// Update the list of options (the list of possible nodes) for the depot
		updateDepotOptions();
		
		// Fill the list of possible vehicle's layers
		// TODO: If the user goes back from a next tab, to the previous one, without changing any option,
		// these comboBoxes should stay with the already chosen options. They shouldn't be initalized again.
		fillVehiclesLayers();
		
		// Fill the list of possible vehicles ids
		fillVehiclesIds();
		
		// Fill the list of possible vehicles capacities
		fillVehiclesCapacities();
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
	public void fillVehiclesLayers(){
		// 1. Remove all previous items
		comboBox.removeAllItems();
		
		// 2. Add a first (and default) option
		comboBox.addItem(controlPanel._T("Choose_vehicles_layer"));
		
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
					comboBox.addItem(layer.getName());
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
	 * Fill the list of possible fields for vehicle's identification
	 */
	public void fillVehiclesIds(){
		comboBox_1.removeAllItems();
		TODO
	}
	
	/**
	 * Fill the list of possible fields for the vehicles capacities
	 */
	public void fillVehiclesCapacities(){
		comboBox_2.removeAllItems();
		TODO
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
		comboBox.setEnabled(enable);
		lblVehicleIdentification.setEnabled(enable);
		comboBox_1.setEnabled(enable);
		lblVehiclesCapacities.setEnabled(enable);
		comboBox_2.setEnabled(enable);
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
