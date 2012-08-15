// TODO: Description of file
// TODO: Date

package org.gvsig.graph.vrp.gui;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.ValueWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

public class Customers implements Tab {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private JPanel tabCustomers;
	private JComboBox customersDemandComboBox;
	
	// The customer's demand
	private ArrayList<Float> customersDemand;
		
	// Constructor.
	// Just initializes the Control Panel on witch this JPanel will be drawn.
	public Customers(VRPControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}
	
	/**
	 * Initialize Vehicles tab.
	 * @wbp.parser.entryPoint
	 */
	public JPanel initTab() {

		tabCustomers = new JPanel();
		tabCustomers.setLayout(null);
	

		JButton btnNextTab2 = new JButton("Next >>");
		btnNextTab2.setBounds(386, 278, 89, 23);
		btnNextTab2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnNextCustomersActionPerformed();
			}
		});
		tabCustomers.add(btnNextTab2);
		
		JButton btnPreviousTab2 = new JButton("<< Undo");
		btnPreviousTab2.setBounds(287, 278, 89, 23);
		btnPreviousTab2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnPreviousCustomersActionPerformed();
			}
		});
		tabCustomers.add(btnPreviousTab2);
		
		JLabel lblCustomersDemand = new JLabel("Customer's demand:");
		lblCustomersDemand.setBounds(45, 12, 112, 14);
		tabCustomers.add(lblCustomersDemand);
		
		customersDemandComboBox = new JComboBox();
		customersDemandComboBox.setBounds(167, 8, 209, 23);
		customersDemandComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				int selectedFieldIndex = 0;
				try{
					String selectedField = customersDemandComboBox.getSelectedItem().toString();
					selectedFieldIndex = controlPanel.getODMatrix().getLayerOrigins().getRecordset().getFieldIndexByName(selectedField);
				} catch (ReadDriverException ex){
					ex.printStackTrace();
				}
			
				customersDemand = getCustomersDemand(selectedFieldIndex);
			}
		});
		tabCustomers.add(customersDemandComboBox);
		
		JLabel lblCustomersTimeWindow = new JLabel("Customer's time window 1:");
		lblCustomersTimeWindow.setEnabled(false);
		lblCustomersTimeWindow.setBounds(10, 48, 147, 23);
		tabCustomers.add(lblCustomersTimeWindow);
		
		JLabel lblCustomersTimeWindow_1 = new JLabel("Customer's time window 2:");
		lblCustomersTimeWindow_1.setEnabled(false);
		lblCustomersTimeWindow_1.setBounds(10, 99, 147, 23);
		tabCustomers.add(lblCustomersTimeWindow_1);
		
		JLabel lblCustomersTimeWindow_2 = new JLabel("Customer's time window 3:");
		lblCustomersTimeWindow_2.setEnabled(false);
		lblCustomersTimeWindow_2.setBounds(10, 148, 147, 23);
		tabCustomers.add(lblCustomersTimeWindow_2);
		
		JLabel lblFrom = new JLabel("From:");
		lblFrom.setEnabled(false);
		lblFrom.setBounds(20, 71, 46, 14);
		tabCustomers.add(lblFrom);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setEnabled(false);
		comboBox_1.setBounds(53, 68, 147, 20);
		tabCustomers.add(comboBox_1);
		
		JLabel lblTo = new JLabel("To:");
		lblTo.setEnabled(false);
		lblTo.setBounds(230, 71, 46, 14);
		tabCustomers.add(lblTo);
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setEnabled(false);
		comboBox_2.setBounds(250, 68, 165, 20);
		tabCustomers.add(comboBox_2);
		
		JLabel label_2 = new JLabel("From:");
		label_2.setEnabled(false);
		label_2.setBounds(20, 121, 46, 14);
		tabCustomers.add(label_2);
		
		JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setEnabled(false);
		comboBox_3.setBounds(53, 118, 147, 20);
		tabCustomers.add(comboBox_3);
		
		JLabel label_3 = new JLabel("To:");
		label_3.setEnabled(false);
		label_3.setBounds(230, 121, 46, 14);
		tabCustomers.add(label_3);
		
		JComboBox comboBox_4 = new JComboBox();
		comboBox_4.setEnabled(false);
		comboBox_4.setBounds(250, 118, 165, 20);
		tabCustomers.add(comboBox_4);
		
		JLabel label_4 = new JLabel("From:");
		label_4.setEnabled(false);
		label_4.setBounds(20, 174, 46, 14);
		tabCustomers.add(label_4);
		
		JComboBox comboBox_5 = new JComboBox();
		comboBox_5.setEnabled(false);
		comboBox_5.setBounds(53, 171, 147, 20);
		tabCustomers.add(comboBox_5);
		
		JLabel label_5 = new JLabel("To:");
		label_5.setEnabled(false);
		label_5.setBounds(230, 174, 46, 14);
		tabCustomers.add(label_5);
		
		JComboBox comboBox_6 = new JComboBox();
		comboBox_6.setEnabled(false);
		comboBox_6.setBounds(250, 171, 165, 20);
		tabCustomers.add(comboBox_6);
		
	
		return tabCustomers;
	}
	
	/**
	 * What should be done when the user comes from the previous tab.
	 */
	public void fromPreviousTab(){
		
	}
	
	/**
	 * What should be done when the user comes from the next tab.
	 */
	public void fromNextTab(){
		
	}
	
	/**
	 * Grab the customers demands from the chosen table column and put them on an array.
	 */
	// TODO: Show to the user the list of registers who couldn't be cast to double.
	public ArrayList<Float> getCustomersDemand(int column){
		// The customers demands
		ArrayList<Float> customersDemand = new ArrayList<Float>();
		// The values that couldn't be cast do numeric ones
		ArrayList<Integer> wrongRegisters = new ArrayList<Integer>();
		
		//1. Get the selected layer
		if (customersDemandComboBox.getSelectedIndex()==0){
			// If the selected layer is the first, the user didn't choose anything
			return customersDemand;
		}
		// Otherwise get the selected layer
		FLyrVect layer = controlPanel.getODMatrix().getLayerOrigins();
		SelectableDataSource dataSource = null;
		try {
			dataSource = layer.getRecordset();
		} catch (ReadDriverException ex){
			ex.printStackTrace();
		}
		
		//2. Get the number of values
		long count = this.getRowCount(layer);
		if (count<1){
			// Alert the user that this field doesn't have any value
			controlPanel.showMessageDialog("Field_without_values");
		} else {
			// Get the customer's demand values as Doubles
			for (int i=0; i<count ;i++){
				String value = null;
				try {
					value = dataSource.getFieldValue(i,column).getStringValue(ValueWriter.internalValueWriter);
					value = value.replaceAll("'", "");
					customersDemand.add(Float.parseFloat(value));
				} catch (NumberFormatException ex){
					// If there are wrong values, get those items and add 0 to the customer demand
					wrongRegisters.add(i);
					customersDemand.add((float) 0);
					// TODO: 
					System.out.println("Row " + i + " gave an error. It's original value was " + value);
				} catch (ReadDriverException e) {
					System.out.println("Read Driver Exception");
					e.printStackTrace();
				}
			}
			
			// If there are uncasted values, show an alert
			if (wrongRegisters.size()>0){
				controlPanel.showMessageDialog("Field_with_non_numeric_values");
			}
		}
		
		return customersDemand;
	}
	
	/**
	 * Gets the list of numerical columns (int, longint, float, double) from a given layer on the gvSIG's workspace
	 */
	public String[] getNumericalColumnList(){
		FLyrVect selectedLayer = controlPanel.getODMatrix().getLayerOrigins(); // This is the user selected point layer
		ArrayList<String> columnList = new ArrayList<String>();
		SelectableDataSource dataSource;
		try {
			dataSource = selectedLayer.getRecordset();
			int nrFields = dataSource.getFieldCount();
			for (int i=0; i<nrFields; i++){
				if (dataSource.getFieldType(i) == dataSource.FIELD_TYPE_INT ||
					dataSource.getFieldType(i) == dataSource.FIELD_TYPE_LONGINT ||
					dataSource.getFieldType(i) == dataSource.FIELD_TYPE_FLOAT ||
					dataSource.getFieldType(i) == dataSource.FIELD_TYPE_DOUBLE ){
					String fieldName = dataSource.getFieldName(i);
					columnList.add(fieldName);
				}
			}
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
		return columnList.toArray(new String[0]);
	}
	
	/**
	 * Gets the list of numeric or string columns from a given layer on the gvSIG's workspace
	 */
	// TODO: Put this method on Support Class
	public String[] getColumnList(FLyrVect selectedLayer){
		ArrayList<String> columnList = new ArrayList<String>();
		SelectableDataSource dataSource;
		try {
			dataSource = selectedLayer.getRecordset();
			int nrFields = dataSource.getFieldCount();
			for (int i=0; i<nrFields; i++){
				String fieldName = dataSource.getFieldName(i);
				columnList.add(fieldName);
			}
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
		return columnList.toArray(new String[0]);
	}
	
	/**
	 * Returns the number of rows of a given layer
	 */
	// TODO: Put this method on Support Class
	public long getRowCount(FLyrVect selectedLayer){
		SelectableDataSource dataSource;
		long nrFields = 0;
		try {
			dataSource = selectedLayer.getRecordset();
			nrFields = dataSource.getRowCount();
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
		return nrFields;
	}
	
	
	/**
	 * Fill the customers demand ComboBox with the suitable fields from the clients layer. 
	 */
	public boolean fillCustomersDemandComboBox(){
		FLyrVect selectedLayer = controlPanel.getODMatrix().getLayerOrigins();
		String[] columns = getColumnList(selectedLayer);
		if (columns.length == 0){
			controlPanel.showMessageDialog("Layer_without_fields");
			return false;
		} else {
			//1. First empty the comboBox
			customersDemandComboBox.removeAllItems();
			
			//2. Then put a default value
			// TODO: put this value on italics, for the user to grab the difference to the other options
			customersDemandComboBox.addItem(controlPanel._T("Choose_suitable_field"));
			
			//3. Then fill it
			for (int i=0;i<columns.length;i++){
				customersDemandComboBox.addItem(columns[i]);
			}
			customersDemandComboBox.setSelectedIndex(0);
			
			return true;
		}
	}
	
	
	/*
	 * Getters and Setters
	 */
	
	/**
	 * Get the customers demands as doubles
	 */
	public ArrayList<Float> getCustomersDemand() {
		return customersDemand;
	}
	
	/**
	 * The button "Next" guarantees that we have the customer's demands and goes to the next tab
	 */
	private void btnNextCustomersActionPerformed(){
		// If the user didn't choose any option, show an alert  
		if (customersDemandComboBox.getSelectedIndex()==0){
			controlPanel.showMessageDialog("No_field_selected");
			return;
		}
		
		// Go to the next tab
		controlPanel.switchToNextTab();
	}

	// Just go to the previous tab
	private void btnPreviousCustomersActionPerformed(){
		controlPanel.switchToPreviousTab();
	}
}	
