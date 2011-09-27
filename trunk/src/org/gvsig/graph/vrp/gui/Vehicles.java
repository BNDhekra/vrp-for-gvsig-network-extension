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
import org.metavrp.GA.*;
import org.metavrp.GA.support.*;
import org.metavrp.VRP.*;


//TODO: description of class
public class Vehicles {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private GeneList geneList;
	private JPanel tabVehicles;
	private JTextField nVehicles;
	private JComboBox depot;
	private int depotNumber;
	private int numVehicles;
	
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
		lblNOfVehicles.setBounds(42, 34, 70, 14);
		tabVehicles.add(lblNOfVehicles);
		
		nVehicles = new JTextField();
		nVehicles.setText("1");
		nVehicles.setBounds(122, 31, 86, 20);
		tabVehicles.add(nVehicles);
		nVehicles.setColumns(10);
		
		JLabel lblputIn = new JLabel("(Put 1 in case of a TSP)");
		lblputIn.setBounds(218, 34, 191, 14);
		tabVehicles.add(lblputIn);
		
		JLabel lblDepotNode = new JLabel("Depot node:");
		lblDepotNode.setBounds(42, 77, 70, 14);
		tabVehicles.add(lblDepotNode);
		
		depot = new JComboBox();
		depot.setEditable(true);
		depot.setBounds(122, 74, 86, 20);
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
	
		return tabVehicles;
	}
	
	// Create the list of options for the depot
	public void updateDepotOptions(){
		for (int i=0;i<controlPanel.getODMatrix().getCostMatrixSize();i++){
			depot.addItem(""+i+"");
		}
		depot.setEditable(true);
		depot.setSelectedIndex(0);
	}
	
	// Create the list of possible genes (vehicles and customers)
	public GeneList createGeneList(){
		// Create an ArrayList of the customers.
		// The customers are all the nodes except the depot 
		ArrayList<Customer> customers = new ArrayList<Customer>(); 
		for (int i=0; i<controlPanel.getODMatrix().getCostMatrixSize(); i++){
			// Add all the nodes to the customers list except the depot
			if (i != getDepotNumber())
				customers.add(new Customer(i,i));	
		}
		
		// Create an ArrayList of the vehicles.
		ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>(); 
		for (int i=1; i<=getNumVehicles(); i++){
			vehicles.add(new Vehicle(-i, getDepotNumber()));
		}
		return new GeneList(customers, vehicles);
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
		this.geneList = createGeneList();
		
		// Go to the next tab
		controlPanel.switchToNextTab();
	}

	// Just go to the previous tab
	private void btnPreviousVehiclesActionPerformed(){
		controlPanel.switchToPreviousTab();
	}
	
}	
