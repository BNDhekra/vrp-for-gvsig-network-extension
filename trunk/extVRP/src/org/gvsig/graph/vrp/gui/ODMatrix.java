// TODO: Description of file
// TODO: Date

package org.gvsig.graph.vrp.gui;

// Needed imports
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.Rectangle;
import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import org.apache.log4j.Logger;
import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.util.LayerListCellRenderer;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.IODMatrixFileWriter;
import org.gvsig.graph.ODMatrixExtension;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.gui.ODMatrixTask;
import org.gvsig.graph.vrp.VRPExtension;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

import org.metavrp.VRP.*;

// TODO: description of class
public class ODMatrix extends JPanel {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private static IODMatrixFileWriter selectedWriter;	// The kind of file to write (there are 3 types)
	private ArrayList<IODMatrixFileWriter> odMatrixWriters;
	private DefaultComboBoxModel cboLayerOriginsModel;
	private FLyrVect layerOrigins;
	private GvFlag[] originFlags;
	private JComboBox cboLayerOrigins;
	private JLabel jLblLayerOrigins;
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
		cboLayerOrigins = new javax.swing.JComboBox();
		cboLayerOrigins.setBounds(new Rectangle(151, 23, 259, 22));
		jLblLayerOrigins = new javax.swing.JLabel();
		jLblLayerOrigins.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLblLayerOrigins.setBounds(new Rectangle(10, 24, 135, 20));
		jLblLayerOrigins.setText("Point Layer:");
		
		layerJPanel = new javax.swing.JPanel();
		layerJPanel.setLayout(null);
		layerJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Layer"));
		layerJPanel.setBounds(new Rectangle(14, 13, 468, 59));
		layerJPanel.add(jLblLayerOrigins, null);
		layerJPanel.add(cboLayerOrigins, null);
		
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
	
	
	// Enable or disable the checkbox with auto tolerance
	private void enableAutoTolerance (Boolean enabled){
		chckbxAuto.setEnabled(enabled);
	}
	
	// Enable or disable the tolerance related fields
	private void enableTolerance (Boolean enabled){
		jLblTolerance.setEnabled(enabled);
		txtTolerance.setEnabled(enabled);
		jLblToleranceUnits.setEnabled(enabled);
	}
	
	// Update the list of layers shown on the JComboBox
	// Layers of type POINT or MULTIPOINT
	// TODO: This method needs to be called when the user sees the JPanel to show the point layers
	public void updateOriginsLayers(Vector<FLyrVect> arrayLayers){
		cboLayerOriginsModel = new DefaultComboBoxModel(arrayLayers);
		cboLayerOrigins.setModel(cboLayerOriginsModel);
		cboLayerOrigins.setRenderer(new LayerListCellRenderer());
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
		layerOrigins = (FLyrVect) cboLayerOrigins.getSelectedItem();
		
		//Put the flags on the network
		if (autoTolerance==true){
			try {
				originFlags = putFlagsOnNetwork(layerOrigins, net); 
				flagsOnNet=true;
			} catch (BaseException ex){
				ex.printStackTrace();
			}
		} else {
			originFlags = putFlagsOnNetwork(layerOrigins, net, maxTolerance); 
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
			layerOrigins = (FLyrVect) cboLayerOrigins.getSelectedItem();
			try {
				originFlags = putFlagsOnNetwork(layerOrigins, net); 
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
					layerOrigins = (FLyrVect) cboLayerOrigins.getSelectedItem();
					originFlags = NetworkUtils.putFlagsOnNetwork(layerOrigins, net, tolerance);
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
					layerOrigins = (FLyrVect) cboLayerOrigins.getSelectedItem();
					originFlags = NetworkUtils.putFlagsOnNetwork(layerOrigins, net, tolerance);
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
		return layerOrigins;
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
