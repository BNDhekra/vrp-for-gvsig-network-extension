// TODO: Description of file
// TODO: Date

package org.gvsig.graph.vrp.gui;

// Needed imports
import java.awt.Component;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;
import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.util.LayerListCellRenderer;
import org.gvsig.graph.IODMatrixFileWriter;
import org.gvsig.graph.ODMatrixExtension;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.graph.gui.ODMatrixTask;
import org.gvsig.graph.vrp.VRPExtension;
import org.gvsig.graph.vrp.support.Layers;
import org.metavrp.problem.CostMatrix;


import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

// TODO: description of class
public class ODMatrix extends JPanel implements Tab {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private static IODMatrixFileWriter selectedWriter;	// The kind of file to write (there are 3 types)
	private ArrayList<IODMatrixFileWriter> odMatrixWriters;
	private FLyrVect customersLayer;
	private GvFlag[] originFlags;
	private JComboBox<String> customersLayerList;
	private JLabel jLblCustomersLayer;
	private JPanel layerJPanel;
	private JRadioButton opt1, opt2;
	private ButtonGroup bgroup;
	private JLabel fileFormatJLabel;
	private JComboBox fileFormat;
	private JLabel jLblFile;
	private JTextField txtGeneratedFile;
	private File selectedFile;	// The file with the ODMatrix
	private JButton btnSelectFile;
	private JLabel jLblTolerance = null;
	
	// TODO: Set this field to a JFormattedTextField, to prevent the insertion of non-numbers
	private JTextField txtTolerance = null;
	private JLabel jLblToleranceUnits = null;
	private JCheckBox chckbxAuto;
	private JPanel odmatrixJPanel;
	private JButton btnNextTab1;
	private JPanel tabLayers;
	private FileInputStream fstream;
	private double maxTolerance=100000;
	private CostMatrix costMatrix;
	private int costMatrixSize;
	private boolean flagsOnNet=false;
	
	static Logger logger = Logger.getLogger(NetworkUtils.class);
	
	
	// Constructor.
	// Just initializes the Control Panel on witch this JPanel will be drawn and the list of matrix writers.
	public ODMatrix (VRPControlPanel controlPanel, ArrayList<IODMatrixFileWriter> odMatrixWriters){
		this.controlPanel = controlPanel;
		this.odMatrixWriters = odMatrixWriters;
	}
	
	/**
	 * Initialize ODMatrix tab.
	 * @wbp.parser.entryPoint
	 */
	public JPanel initTab() {
		
		// Layer section
		// POINT or MULTIPOINT Layer 
		customersLayerList = new javax.swing.JComboBox<String>();
		customersLayerList.setBounds(new Rectangle(151, 23, 259, 22));
		customersLayerList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateCustomersLayers();
			}
		});
		jLblCustomersLayer = new javax.swing.JLabel();
		jLblCustomersLayer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLblCustomersLayer.setBounds(new Rectangle(10, 24, 135, 20));
		jLblCustomersLayer.setText("Customers Layer:");
		
		layerJPanel = new javax.swing.JPanel();
		layerJPanel.setLayout(null);
		layerJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Layer"));
		layerJPanel.setBounds(new Rectangle(14, 13, 468, 59));
		layerJPanel.add(jLblCustomersLayer, null);
		layerJPanel.add(customersLayerList, null);
		
		// Origin - Destination Matrix section
		// Selection options
		opt1 = new JRadioButton("Use existing file", true);
		opt1.setBounds(new Rectangle(10, 19, 400, 20));
		opt1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				enableAutoTolerance(false);
				enableTolerance(false);
			}
		});
		opt2 = new JRadioButton("Create new file");
		opt2.setBounds(new Rectangle(10, 42, 400, 20));
		opt2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				enableAutoTolerance(true);
				chckbxAuto.setSelected(true);
				//enableTolerance(true);
			}
		});

		// File format
		fileFormatJLabel = new javax.swing.JLabel();
		fileFormatJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		fileFormatJLabel.setBounds(new Rectangle(10, 83, 134, 16));
		fileFormatJLabel.setText("File Format:");
		fileFormat = new javax.swing.JComboBox();
			IODMatrixFileWriter[] writers = ODMatrixExtension.getOdMatrixWriters();
			String[] formats = new String[writers.length];
			for (int i = 0; i < formats.length; i++) {
				formats[i] = controlPanel._T(writers[i].getFormatDescription());
			}
		fileFormat.setModel(new DefaultComboBoxModel(formats));
		fileFormat.setSelectedIndex(0);
		fileFormat.setBounds(new Rectangle(151, 80, 259, 22));
		
		// File name and address
		jLblFile = new javax.swing.JLabel();
		jLblFile.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLblFile.setBounds(new Rectangle(10, 117, 135, 20));
		jLblFile.setText("File:");
		txtGeneratedFile = new javax.swing.JTextField();
		txtGeneratedFile.setText("");
		txtGeneratedFile.setBounds(new Rectangle(151, 116, 259, 22));
		btnSelectFile = new javax.swing.JButton();
		btnSelectFile.setText("...");
		btnSelectFile.setBounds(new Rectangle(418, 116, 28, 21));
		btnSelectFile.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFileChooser dlg = new JFileChooser();
				if (dlg.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION)
				{
					txtGeneratedFile.setText(dlg.getSelectedFile().getPath());
				}
			}
		});
		
		// Tolerance
		jLblTolerance = new JLabel();
		jLblTolerance.setBounds(new Rectangle(10, 148, 135, 20));
		jLblTolerance.setText("Tolerance:");
		jLblTolerance.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTolerance = new JTextField();
		txtTolerance.setBounds(new Rectangle(152, 148, 94, 22));
		txtTolerance.setText("1000");
		jLblToleranceUnits = new JLabel();
		jLblToleranceUnits.setBounds(new Rectangle(258, 148, 49, 20));
		jLblToleranceUnits.setText("meters");
		chckbxAuto = new JCheckBox("Auto tolerance");
		chckbxAuto.setSelected(true);
		chckbxAuto.setBounds(313, 147, 97, 23);
		chckbxAuto.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
		        AbstractButton abstractButton = (AbstractButton) e.getSource();
		        boolean selected = abstractButton.getModel().isSelected();
				if (selected) enableTolerance(false);
				else enableTolerance(true);
			}
		});
		//TODO: Shouldn't the tolerance buttons be present all the time, instead of only when the user wants to create a distance matrix?
		enableAutoTolerance(false);
		enableTolerance(false);
		
		// The section (JPanel)
		odmatrixJPanel = new javax.swing.JPanel();
		odmatrixJPanel.setLayout(null);
		odmatrixJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Origin - Destination Matrix"));
		odmatrixJPanel.setBounds(new Rectangle(14, 83, 468, 181));
		
		// Add everything
		odmatrixJPanel.add(opt1, null);
		odmatrixJPanel.add(opt2, null);
		odmatrixJPanel.add(fileFormat, null);
		odmatrixJPanel.add(fileFormatJLabel, null);
		odmatrixJPanel.add(jLblFile, null);
		odmatrixJPanel.add(txtGeneratedFile, null);
		odmatrixJPanel.add(btnSelectFile, null);
		odmatrixJPanel.add(jLblTolerance, null);
		odmatrixJPanel.add(txtTolerance, null);
		odmatrixJPanel.add(jLblToleranceUnits, null);
		odmatrixJPanel.add(chckbxAuto);

		// Group radio buttons
	    bgroup = new ButtonGroup();
	    bgroup.add(opt1);
	    bgroup.add(opt2);

	    // Button 'Next'
		btnNextTab1 = new javax.swing.JButton();
		btnNextTab1.setText("Next >>");
		btnNextTab1.setBounds(new Rectangle(386, 278, 89, 23));
		btnNextTab1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnNextODMatrixActionPerformed(evt);
			}
		});

		// The OD Matrix tab 
		tabLayers = new JPanel();
		tabLayers.setLayout(null);
		tabLayers.add(layerJPanel, null);
		tabLayers.add(odmatrixJPanel, null);
		tabLayers.add(btnNextTab1, null);
		
		return tabLayers;
	}
	
	/**
	 * What should be done when the user comes from the previous tab.
	 */
	public void fromPreviousTab(){
		try {
			// Updates the list of POINT Layers which represent the customers 
			initializeCustomersLayers(Layers.getPointLayers(controlPanel.getMapContext()));
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * What should be done when the user comes from the next tab.
	 */
	public void fromNextTab(){
		// TODO: Is there anything that needs to be done?
	}
	
	// Enable or disable the checkbox with auto tolerance
	private void enableAutoTolerance (Boolean enabled){
		chckbxAuto.setEnabled(enabled);
	}
	
	/**
	 * Enable or disable the tolerance related fields
	 * @param enabled
	 */
	private void enableTolerance (Boolean enabled){
		jLblTolerance.setEnabled(enabled);
		txtTolerance.setEnabled(enabled);
		jLblToleranceUnits.setEnabled(enabled);
	}
	
	/**
	 * Updates the list of layers shown on the JComboBox. Layers of type POINT or MULTIPOINT
	 */
	public void initializeCustomersLayers(Vector<FLyrVect> arrayLayers){
//		DefaultComboBoxModel cboLayerOriginsModel = new DefaultComboBoxModel(arrayLayers);
//		customersLayerList.setModel(cboLayerOriginsModel);
//		customersLayerList.setRenderer(new LayerListCellRenderer());
		
		// Add the message to the user
		customersLayerList.addItem(controlPanel._T("Choose_customers_layer"));
		for (int i=0; i<arrayLayers.size(); i++){
			// TODO: This doesn't work if the user has two layers with the same name.
			// Add the layer's names
			customersLayerList.addItem(arrayLayers.get(i).getName());
		}
	}
	
	/**
	 * Change the selected customers layer
	 */
	private void updateCustomersLayers(){
		int selectedLayerIndex = 0;
		try{
			// 1. Get the selected element
			selectedLayerIndex = customersLayerList.getSelectedIndex();
			
			// 2. If it's not the default value 
			if (selectedLayerIndex > 0){
				// 2.1. Get the selected layer's name
				String selectedLayerName = customersLayerList.getSelectedItem().toString();
				
				// 2.2. Verify that the selected layer has one or more fields, otherwise give an alert to the user 
				customersLayer = (FLyrVect) controlPanel.getMapContext().getLayers().getLayer(selectedLayerName);

				int nrOfFields = customersLayer.getRecordset().getFieldCount();
				if (nrOfFields<1){
					controlPanel.showMessageDialog ("Customers_layer_with_fewer_than_one_field");
					// Then go to the default (first) option
					// TODO: Should go to the previous selected layer
					customersLayerList.setSelectedIndex(0);
				} 
			
			// 3. If it is the default index just remove the previously selected layer
			} else {
				customersLayer = null;
			}
			
		} catch (ReadDriverException ex){
			ex.printStackTrace();
		}

	}
	
    //Opens a cost matrix on a text file
    public CostMatrix openCostMatrix(File fileName){
        return new CostMatrix(fileName.toString(), false);
    }
	
	// Returns true if the given file can be written
	public boolean canWriteFile(File file) {
		if (file.exists()) {
			if (file.canWrite()){
				controlPanel.showMessageDialog("File_already_exists");
				return true;
			}
			else {
				controlPanel.showMessageDialog("File_cannot_be_written");
				return false;
			}
		}
		else {
			try {
				file.createNewFile();
				file.delete();
				return true;
			}
			catch (Exception e) {
				controlPanel.showMessageDialog("File_cannot_be_written");
				return false;
			}
		}
	}
	
	
	// The functionality of the "Next >>" button on the first tab
	// Verifies the file and tolerance validity, then creates or opens ODMatrix file
	private void btnNextODMatrixActionPerformed(java.awt.event.ActionEvent evt) {
		
		// Try to get the tolerance
		maxTolerance = getTolerance();			
		
		// Get the file's address
		selectedFile = new File(getFileAddress());
		
		// If the user wants to create a new ODMatrix file, do it, otherwise open an existing one
		if (opt2.isSelected()){
			// Verify that the file exists and can be written
			if (!canWriteFile(selectedFile)){			// Can the file be written? If no, show an alert
				controlPanel.showMessageDialog("Please_select_a_valid_file");
				return;
			}
			
			// Verify if the user chooses auto tolerance 
			if (chckbxAuto.isSelected()){ 			// Generate the ODMatrix using auto tolerance value
				if (!generateODMatrixFile(true)){ 
					controlPanel.showMessageDialog("Please_select_a_valid_file");
					return;
				}
			}
			else { 									// The user chooses his value of tolerance
				if (!generateODMatrixFile(false)){	// Execute only if the ODMatrix file was correctly generated 
					controlPanel.showMessageDialog("Please_select_a_valid_file");
					return;
				}
			}
		}
		else if (opt1.isSelected()){				// Open previously generated ODMatrix
			if (!openODMatrixFile()){
				controlPanel.showMessageDialog("Please_select_a_valid_file"); 
				return;
			}
		}
		
		// Create the Distance Matrix
		setCostMatrix(openCostMatrix(selectedFile));
		
		// Update the list of possible fields of customer demands on the Customers tab.
		// If it isn't successful, stay on this tab.
		if (!controlPanel.getCustomers().fillCustomersDemandComboBox()){
			return;
		}
		
		// Go to the next tab
		controlPanel.switchToNextTab();
		
	}

	// Generates a Origin-Destination File, with the cost to go from any point to any point
	public boolean generateODMatrixFile(boolean autoTolerance){

		//Get the associated network
		Network net = VRPExtension.getNetwork();
		
		//Get the selected layer
		customersLayer = (FLyrVect) customersLayerList.getSelectedItem();
		
		//Put the flags on the network
		if (autoTolerance==true){
			try {
				originFlags = putFlagsOnNetwork(customersLayer, net); 
				flagsOnNet=true;
			} catch (BaseException ex){
				ex.printStackTrace();
			}
		} else {
			originFlags = putFlagsOnNetwork(customersLayer, net, maxTolerance); 
			flagsOnNet=true;
		}

		// For the VRP the origin flags are the same as the destination ones
		GvFlag[] destinationFlags = originFlags;

		selectedWriter = odMatrixWriters.get(getFileFormat());
		
		ODMatrixTask task = new ODMatrixTask(net, originFlags, destinationFlags,
				selectedFile, selectedWriter);
		PluginServices.cancelableBackgroundExecution(task);
		
		// calculateOdMatrix(net, originFlags, destinationFlags, selectedFile);
		
		try {
			while (!task.isFinished())
				//Wait a little for his completion
				Thread.sleep(300);
		} catch (InterruptedException e) {
			// Print Stack Trace
			e.printStackTrace();
		}
		
		// TODO: ASK THE USER IF HE WANTS TO SAVE FLAGS TO AVOID PUTTING POINTS ON NETWORK AGAIN
		
		// If the user cancels the task we don't open the file
		if (task.isCanceled())
			return false;
		
		openODMatrixFile();	// Opens the file so that no-one can change it in between
		
		return true;	// Return ok
	}
	
	
	// Open existing ODMatrix file
	public boolean openODMatrixFile(){
		if (!selectedFile.exists()){	// If the file doesn't exist
			controlPanel.showMessageDialog("File_doesnt_exist");
			return false;
		}
		if (!selectedFile.canRead()){	// If the file can't be read
			controlPanel.showMessageDialog("File_cannot_be_read");
			return false;
		}
		
		// Open the file with a stream to guarantee that no one changes it in the meanwhile
		try {
		fstream = new FileInputStream(selectedFile);
		}catch (Exception e){	//The program never gets here (I hope!)
            e.printStackTrace();
        }
		
		// If the net has no flags:
		if (!flagsOnNet){
			// Get the associated network
			Network net = VRPExtension.getNetwork();
			
			// Get the format of the file
			selectedWriter = odMatrixWriters.get(getFileFormat());
			
			// Put the flags on the network
			customersLayer = (FLyrVect) customersLayerList.getSelectedItem();
			try {
				originFlags = putFlagsOnNetwork(customersLayer, net); 
			} catch (BaseException ex){
				ex.printStackTrace();
			}
		}
		return true;	// Everything went fine
	}
	
	
	// Put the flags (the nearest points from a point layer) on the network
	// This is an automated method. No need to define the tolerance
	public GvFlag[] putFlagsOnNetwork(FLyrVect layer, Network net) throws BaseException{

		if ( net != null) // If there is a layer with an associated network:
		{
long start = System.currentTimeMillis();
			setSpatialIndex(net);

			int tolerance=1;
			while (true){
				try {
					System.out.println("\nTrying to put flags on the network with a tolerance of "+tolerance);	
					customersLayer = (FLyrVect) customersLayerList.getSelectedItem();
					originFlags = NetworkUtils.putFlagsOnNetwork(customersLayer, net, tolerance);
					long stop = System.currentTimeMillis();
					System.out.println("Took "+ (stop-start) + "ms and it Worked!\n\n");	
					return originFlags;
				} catch (BaseException e) {
					long stop = System.currentTimeMillis();
					System.out.println("\nTook "+ (stop-start) + "ms but didn't work.");					
					tolerance = tolerance * 10;
					if (tolerance>maxTolerance){
						e.printStackTrace();
						if (e.getCode() == GraphException.FLAG_OUT_NETWORK) {
							controlPanel.showMessageDialog("The automated tolerance chooser couldn't find a suitable tolerance value." +
									" Please choose a value bigger than "+maxTolerance);
//							NotificationManager.addError(e.getFormatString(), e);
							
						}
						return null;
					}
				}
			}
		}
		return null;
	}
	
	// Put the flags (the nearest points from a point layer) on the network using a given tolerance
	public GvFlag[] putFlagsOnNetwork(FLyrVect layer, Network net, double tolerance){
		if ( net != null) // If there is a layer with an associated network:
		{
			setSpatialIndex(net);

				long start = System.currentTimeMillis();
				try {
					System.out.println("\nTrying to put flags on the network with a tolerance of "+tolerance);	
					customersLayer = (FLyrVect) customersLayerList.getSelectedItem();
					originFlags = NetworkUtils.putFlagsOnNetwork(customersLayer, net, tolerance);
					long stop = System.currentTimeMillis();
					System.out.println("Took "+ (stop-start) + "ms and to put all the flags with a tolerance of "+tolerance+"!\n\n");	
					return originFlags;
				} catch (BaseException e) {
					long stop = System.currentTimeMillis();
					System.out.println("\nTook "+ (stop-start) + "ms but didn't work.");					
					e.printStackTrace();
					if (e.getCode() == GraphException.FLAG_OUT_NETWORK) {
						controlPanel.showMessageDialog("there_are_points_outside_the_tolerance");
//							NotificationManager.addError(e.getFormatString(), e);
					}
					return null;
				}
			}
		return null;
	}
	
	// Sets the spatial index
	public static void setSpatialIndex(Network net){
		if ( net != null) // If there is a layer with an associated network:
		{
			if (net.getLayer().getISpatialIndex() == null)
			{
				try {
					System.out.println("Measuring spatial index (QuadTree)...");
					net.getLayer().setISpatialIndex(NetworkUtils.createJtsQuadtree(net.getLayer()));
					System.out.println("Spatial index calculated.");
				} catch (BaseException e){
					e.printStackTrace();
					// TODO: Show (i18n) error message
				}
			}
		}
	}
	
	/*
	 * Getters and setters
	 */
	// Returns the chosen tolerance value
	public double getTolerance() {
		try {
			return Double.parseDouble(txtTolerance.getText());
		} catch (NumberFormatException e) {
			controlPanel.showMessageDialog ("Please_enter_a_valid_number");
			return maxTolerance;
		}
	}
	
	// Returns the file address
	public String getFileAddress() {
		return txtGeneratedFile.getText();
	}
	
	// Returns the selected layer
	public FLyrVect getLayerOrigins() {
		return customersLayer;
	}
	
	// Returns the index of the file format
	public int getFileFormat() {
		return fileFormat.getSelectedIndex();
	}
	
	// Set the Cost Matrix and its size
	public void setCostMatrix (CostMatrix costMatrix) {
		this.costMatrixSize = costMatrix.getSize();
		this.costMatrix = costMatrix;
	}
	
	// Get the Cost Matrix
	public CostMatrix getCostMatrix () {
		return this.costMatrix;
	}
	
	// Get the Size of the Cost Matrix
	public int getCostMatrixSize() {
		return this.costMatrixSize;
	}

	// Get the flags of the layer
	public GvFlag[] getOriginFlags() {
		return originFlags;
	}
	
}
