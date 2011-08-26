/*
 * OdMatrixControlPanel.java
 *
 * Created on 20 de octubre de 2008, 13:19
 */

package org.gvsig.graph.vrp.gui;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.util.LayerListCellRenderer;
import org.gvsig.graph.IODMatrixFileWriter;
import org.gvsig.graph.ODMatrixExtension;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.graph.gui.ODMatrixTask;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.project.documents.view.gui.View;

import org.metavrp.GA.*;
import org.metavrp.GA.support.*;
import org.metavrp.VRP.*;

/**
 * 
 * @author Fjp
 */
public class VRPControlPanel extends JPanel implements IWindow {

//	public static int TIME_SECONDS = 0;
//	public static int TIME_MINUTES = 1;
//	public static int LENGTH_METERS = 0;
//	public static int LENGTH_KILOMETERS = 1;
//	public static int LENGTH_YARDS = 2;
//	public static int LENGTH_MILES = 3;
//	public static int FILE_FORMAT_2_COLS = 0;
//	public static int FILE_FORMAT_MANY_COLS = 1;

	private MapContext mapContext;
	
	private DefaultComboBoxModel cboLayerOriginsModel;
//	private DefaultComboBoxModel cboLayerDestinationsModel;
	
	private ArrayList<IODMatrixFileWriter> odMatrixWriters = new ArrayList<IODMatrixFileWriter>();

	private static IODMatrixFileWriter selectedWriter;

	private WindowInfo wi;
	private boolean btnNext1Pressed = false;
	
	private double tolerance=100;
	
	// Variables declaration
// TODO: remover o endereço completo, deixar apenas o nome do tipo de classe	
//	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnNextTab1;
	private javax.swing.JButton btnSelectFile;
	private javax.swing.JComboBox fileFormat;
//	private javax.swing.JComboBox cboLayerDestinations;
	private javax.swing.JComboBox cboLayerOrigins;
	private javax.swing.JLabel fileFormatJLabel;
	private javax.swing.JLabel jLblFile;
//	private javax.swing.JLabel jLblLayerDestinations;
	private javax.swing.JLabel jLblLayerOrigins;
	private javax.swing.JPanel odmatrixJPanel;
	private javax.swing.JPanel layerJPanel;
	private javax.swing.JTextField txtGeneratedFile;
	private JLabel jLblTolerance = null;
	private JTextField txtTolerance = null;
	private JLabel jLblToleranceUnits = null;
	private JCheckBox chckbxAuto;
	private JRadioButton opt1, opt2;
	private JTabbedPane tabs;
	private JPanel tabLayers, tabVehicles, tabGA, tabRun, tabResults;
	private JTextField nVehicles;
	private JComboBox depot;
	private File selectedFile;	// The file with the ODMatrix
//	private JTextArea txtFormatAreaDescription = null;
	private FileInputStream fstream;
	// metaVRP objects
	private GeneList geneList;
	private GAParameters params;
	private CostMatrix distanceMatrix;
	private int distanceMatrixSize;
	private int depotNumber;
	private int numVehicles;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	// End of variables declaration

//	/**
//	 * This method initializes txtTolerance	
//	 * 	
//	 * @return javax.swing.JTextField	
//	 */
//	private JTextField getTxtTolerance() {
//		if (txtTolerance == null) {
//			txtTolerance = new JTextField();
//			txtTolerance.setBounds(new Rectangle(152, 148, 94, 22));
//			txtTolerance.setText("50");
//		}
//		return txtTolerance;
//	}


	//	private JTextArea getTxtFormatAreaDescription() {
//		if (txtFormatAreaDescription == null) {
//			txtFormatAreaDescription = new JTextArea();
//			txtFormatAreaDescription.setBounds(new Rectangle(15, 47, 438, 32));
//			txtFormatAreaDescription.setRows(3);
//			txtFormatAreaDescription.setFont(new Font("Arial", Font.PLAIN, 10));
//			txtFormatAreaDescription.setLineWrap(true);
//			txtFormatAreaDescription.setEditable(false);
//		}
//		return txtFormatAreaDescription;
//	}

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		try {

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		VRPControlPanel panel = new VRPControlPanel();
		JFrame test = new JFrame();

		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		test.getContentPane().add(panel);
//		test.setBounds(panel.getBounds());
		test.setVisible(true);
	}

	/** Creates new form OdMatrixControlPanel */
	public VRPControlPanel(ArrayList<IODMatrixFileWriter> odMatrixWriters) {
		this.odMatrixWriters=odMatrixWriters;
		initComponents();
	}

	/**
	 * Initialize the GUI of the plugin.
	 */
//	@SuppressWarnings("unchecked")
	private void initComponents() {
			
		tabs = new JTabbedPane(JTabbedPane.TOP);
		tabs.setBounds(5, 5, 490, 340);
		tabs.setPreferredSize(new Dimension(490, 340));
		tabs.setMinimumSize(new Dimension(100, 100));
		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		// -------------------- //
		// 1. ODMatrix tab		//
		// -------------------- //
		
		// Layer section
		// Point Layer 
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
				formats[i] = _T(writers[i].getFormatDescription());
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
		txtTolerance.setText("100");
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
	    ButtonGroup bgroup = new ButtonGroup();
	    bgroup.add(opt1);
	    bgroup.add(opt2);
		
//		cboLayerDestinations = new javax.swing.JComboBox();
//		jLblLayerDestinations = new javax.swing.JLabel();

		btnNextTab1 = new javax.swing.JButton();
		btnNextTab1.setText("Next >>");
		btnNextTab1.setBounds(new Rectangle(284, 275, 136, 26));
		btnNextTab1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnNextODMatrixActionPerformed(evt);
			}
		});

//		btnCancel = new javax.swing.JButton();
//		btnCancel.setText(_T("Cancel"));
//		btnCancel.setBounds(new Rectangle(257, 275, 136, 26));
//		btnCancel.addActionListener(new java.awt.event.ActionListener() {
//			public void actionPerformed(java.awt.event.ActionEvent evt) {
//				btnCancelActionPerformed(evt);
//			}
//		});

//		jPanel1.add(getTxtFormatAreaDescription(), null);

//		cboFileFormat.addItemListener(new java.awt.event.ItemListener() {
//			public void itemStateChanged(java.awt.event.ItemEvent e) {
//				System.out.println(e.getItem());
//				
//				txtFormatAreaDescription.setText(e.getItem().toString());
//			}
//		});


//		cboLayerDestinations.setBounds(new Rectangle(158, 61, 259, 22));
//		jLblLayerDestinations
//				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
//		jLblLayerDestinations.setBounds(new Rectangle(17, 59, 135, 20));
//		jLblLayerDestinations.setText(_T("Layer_Destinations") + ":");

//		jPanel2.add(jLblLayerDestinations, null);
//		jPanel2.add(cboLayerDestinations, null);
		
		// The OD Matrix tab 
		tabLayers = new JPanel();
		tabLayers.setLayout(null);
		tabLayers.add(layerJPanel, null);
		tabLayers.add(odmatrixJPanel, null);
		tabLayers.add(btnNextTab1, null);
		tabs.addTab("OD Matrix", null, tabLayers, null);

		
		// -------------------- //
		// 2. Vehicles tab		//
		// -------------------- //		
		tabVehicles = new JPanel();
		tabVehicles.setLayout(null);
		tabs.addTab("Vehicles", null, tabVehicles, null);
		
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
		
		// ------------------------ //
		// 3. Genetic Algorithm tab //
		// ------------------------ //
		tabGA = new JPanel();
		tabGA.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Genetic Algorithm settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(6, 7, 469, 47);
		tabGA.add(panel);
		panel.setLayout(null);
		
		JRadioButton rdbtnDefaultSettings = new JRadioButton("Default settings");
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
		
		tabs.addTab("GA", null, tabGA, null);
		
		// -------------------- //
		// 4. Run tab			//
		// -------------------- //
		tabRun = new JPanel();
		tabRun.setLayout(null);
		JButton btnNextTab4 = new JButton("Next >>");
		btnNextTab4.setBounds(386, 278, 89, 23);
		btnNextTab4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnNextRunActionPerformed();
			}
		});
		tabRun.add(btnNextTab4);
		
		JButton btnPreviousTab4 = new JButton("<< Undo");
		btnPreviousTab4.setBounds(287, 278, 89, 23);
		btnPreviousTab4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnPreviousRunActionPerformed();
			}
		});
		tabRun.add(btnPreviousTab4);
		
		tabs.addTab("Run", null, tabRun, null);
		
		// -------------------- //
		// 5. Results tab		//
		// -------------------- //
		tabResults = new JPanel();
		tabResults.setLayout(null);
		tabs.addTab("Results", null, tabResults, null);
		
		JList list = new JList();
		list.setBounds(10, 11, 465, 290);
		tabResults.add(list);
		
		// -------------------- //
		//  The container		//
		// -------------------- //
		this.setLayout(null);
		this.setSize(new Dimension(499, 350));
		this.add(tabs);
		
		// Disable all tabs except the first
		tabs.setEnabledAt(1, false);
		tabs.setEnabledAt(2, false);
		tabs.setEnabledAt(3, false);
		tabs.setEnabledAt(4, false);
	}

	private void closeWindow() {
		if (PluginServices.getMainFrame() != null) {
			PluginServices.getMDIManager().closeWindow(this);
		}
	}

	// The functionality of the "Next >>" button on the first tab
	// Verifies the file and tolerance validity, then creates or opens ODMatrix file
	private void btnNextODMatrixActionPerformed(java.awt.event.ActionEvent evt) {
		selectedFile = new File(getFileAddress());
		if (!canWriteFile(selectedFile))			// Can the file be written? If no, show an alert
		{	
			showMessageDialog("Please_select_a_valid_file");
			return;
		}
		tolerance = getTolerance();					// Try to get the tolerance
		
//		btnNext1Pressed = true;
//		closeWindow();
		
		// If the user wants to create a new ODMatrix file, do it, otherwise open an existing one
		if (opt2.isSelected()){
			if (chckbxAuto.isSelected()){ 			// Generate the ODMatrix using auto tolerance value
				// TODO: escolher tolerancia automáticamente
				if (!generateODMatrixFile(true)){ 
					showMessageDialog("Please_select_a_valid_file"); // TODO: error message
					return;
				}
			}
			else { 									// The user chooses his value of tolerance
				if (!generateODMatrixFile(false)){	// Execute only if the ODMatrix file was correctly generated 
					showMessageDialog("Please_select_a_valid_file"); // TODO: error message
					return;
				} ;
			}
		}
		else if (opt1.isSelected()){				// Open previously generated ODMatrix
			if (!openODMatrixFile())
				showMessageDialog("Please_select_a_valid_file"); // TODO: error message
				return;
		}
		
		// Create the Distance Matrix
		distanceMatrix=openDistanceMatrix(selectedFile);
		
		// Measure the Distance Matrix size from the file
		distanceMatrixSize=distanceMatrix.getSize();
		
		// Create the list of options for the depot
		for (int i=0;i<distanceMatrixSize;i++){
			depot.addItem(""+i+"");
		}
		depot.setEditable(true);
		depot.setSelectedIndex(0);
		
		switchToNextTab(); // Hide this tab and go to the next one
	}
	
	// Get the variables (vehicles and depot) and create the distance matrix
	private void btnNextVehiclesActionPerformed(){
		// Get number of vehicles
		numVehicles=1;
		try {
			numVehicles = Integer.parseInt(nVehicles.getText());
		} catch (NumberFormatException e) {
			showMessageDialog ("Please_enter_a_valid_number");
			return;
		}
		// Get the depot's node
		depotNumber=0;
		try {
			depotNumber = Integer.parseInt((String)depot.getSelectedItem());
		} catch (NumberFormatException e) {
			showMessageDialog ("Please_enter_a_valid_number");
			return;
		}
		switchToNextTab();
	}
	
    //Opens a distance matrix on a text file
    public CostMatrix openDistanceMatrix(File fileName){

        return new CostMatrix(fileName.toString(), false);
    }

	// Just go to the previous tab
	private void btnPreviousVehiclesActionPerformed(){
		switchToPreviousTab();
	}
	
	// Get the GA options and run the GA with them
	private void btnNextGAActionPerformed(){
		switchToNextTab();
		
		// Create an object with the GA parameters
		params = generateGAParameters();
		
		// Create an object with the list of genes (customers and vehicles)
		geneList = createGeneList();
        
        // Run the genetic algorithm
        VRPGARun run = new VRPGARun(params, geneList, distanceMatrix);;
        
	}
	
	// Just go to the previous tab
	private void btnPreviousGAActionPerformed(){
		switchToPreviousTab();
	}
	
	// Get the GA options and run the GA with them
	private void btnNextRunActionPerformed(){
		switchToNextTab();
	}
		
	// Just go to the previous tab
	private void btnPreviousRunActionPerformed(){
		switchToPreviousTab();
	}
	
	// Returns true if the given file can be written
	public boolean canWriteFile(File file) {
		if (file.exists()) {
			if (file.canWrite()){
				showMessageDialog("File_already_exists");
				return true;
			}
			else {
				showMessageDialog("File_cannot_be_written");
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
				showMessageDialog("File_cannot_be_written");
				return false;
			}
		}
	}
	
	public GAParameters generateGAParameters(){
		int popSize = 50;
        float elitism=0.1f;
        float mutationProb=0.2f;
        float crossoverProb=0.8f;
        int generations=40;
		
        // Generate an object with the desired parameters
        return new GAParameters(popSize, elitism, mutationProb, crossoverProb, generations);
        
	}
	
	// Create the list of possible genes (vehicles and customers)
	public GeneList createGeneList(){
		// Create an ArrayList of the customers.
		// The customers are all the nodes except the depot 
		ArrayList<Customer> customers = new ArrayList<Customer>(); 
		for (int i=0; i<this.distanceMatrixSize; i++){
			// Add all the nodes to the customers list except the depot
			if (i != this.depotNumber) customers.add(new Customer(i,i));	
		}
		
		// Create an ArrayList of the vehicles.
		ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>(); 
		for (int i=1; i<=this.numVehicles; i++){
			vehicles.add(new Vehicle(i, this.depotNumber));
		}
		return new GeneList(customers, vehicles);
	}
	
//	private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
//		closeWindow();
//	}
	
	/**
	 * @return Returns the okPressed.
	 */
	public boolean isOkPressed() {
		return btnNext1Pressed;
	}


	public void setMapContext(MapContext mapContext) throws BaseException {
		this.mapContext = mapContext;
		FLayers layers = mapContext.getLayers();
		LayersIterator it = new LayersIterator(layers);
		Vector<FLyrVect> arrayLayers = new Vector<FLyrVect>();
		while (it.hasNext()) {
			FLayer lyr = it.nextLayer();
			if (!lyr.isAvailable())
				continue;
			if (lyr instanceof FLyrVect) {
				FLyrVect lyrVect = (FLyrVect) lyr;
				if ((lyrVect.getShapeType() == FShape.POINT)
						|| (lyrVect.getShapeType() == FShape.MULTIPOINT))
					arrayLayers.add(lyrVect);
					
			}
		}
		cboLayerOriginsModel = new DefaultComboBoxModel(arrayLayers);
//		cboLayerDestinationsModel = new DefaultComboBoxModel(arrayLayers);
		cboLayerOrigins.setModel(cboLayerOriginsModel);
//		cboLayerDestinations.setModel(cboLayerDestinationsModel);
		cboLayerOrigins.setRenderer(new LayerListCellRenderer());
//		cboLayerDestinations.setRenderer(new LayerListCellRenderer());
		
	}

	public int getFileFormat() {
		return fileFormat.getSelectedIndex();
	}
	
	public String getFileAddress() {
		return txtGeneratedFile.getText();
	}
	
	public FLyrVect getOriginsLayer() {
		return (FLyrVect) cboLayerOrigins.getSelectedItem();
	}

//	public FLyrVect getDestinationsLayer() {
//		return (FLyrVect) cboLayerDestinations.getSelectedItem();
//	}

	public WindowInfo getWindowInfo() {
		if (wi == null) {
			wi = new WindowInfo(WindowInfo.MODALDIALOG);
			wi.setWidth((int) this.getPreferredSize().getWidth());
			wi.setHeight((int) this.getPreferredSize().getHeight());
			wi.setTitle(_T("vrp_control_panel"));
		}
		return wi;
	}
	
	public Object getWindowProfile(){
		return WindowInfo.DIALOG_PROFILE;
	}

	public double getTolerance() {
		try {
			return Double.parseDouble(txtTolerance.getText());
		} catch (NumberFormatException e) {
			showMessageDialog ("Please_enter_a_valid_number");
			return tolerance;
		}
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
	
	// Switch to the next tab and hide the one before
	private void switchToNextTab(){
		int selectedTabIndex = tabs.getSelectedIndex();
		tabs.setEnabledAt(selectedTabIndex, false);
		tabs.setEnabledAt(selectedTabIndex+1, true);
		tabs.setSelectedIndex(selectedTabIndex+1);
	}
	
	// Switch to the previous tab and hide this one
	private void switchToPreviousTab(){
		int selectedTabIndex = tabs.getSelectedIndex();
		tabs.setEnabledAt(selectedTabIndex, false);
		tabs.setEnabledAt(selectedTabIndex-1, true);
		tabs.setSelectedIndex(selectedTabIndex-1);
	}
	
	// Shows a given message dialog (popup) on the screen in the correct language
	public void showMessageDialog(String message){
		JOptionPane.showMessageDialog((Component) PluginServices.getMDIManager().getActiveWindow(),	_T(message));
	};
	
	// Translates the given string to the language of the current locale
	private String _T(String str) {
		return PluginServices.getText(this, str);
	}
	

	public boolean generateODMatrixFile(boolean autoTolerance){
		
		View v = (View) PluginServices.getMDIManager().getActiveWindow();
		MapContext map = v.getMapControl().getMapContext();
		SingleLayerIterator it = new SingleLayerIterator(map.getLayers());

			while (it.hasNext())
			{
				FLayer aux = it.next();
				if (!aux.isActive())
					continue;
				Network net = (Network) aux.getProperty("network");

				if ( net != null)
				{
					try {
						setMapContext(map);

						if (!autoTolerance) { // TODO: Falta codificar a opção de auto tolerância
							if (net.getLayer().getISpatialIndex() == null)
							{
								System.out.println("Calculando índice espacial (QuadTree, que es más rápido)...");
								net.getLayer().setISpatialIndex(
										NetworkUtils.createJtsQuadtree(net.getLayer()));
								System.out.println("Indice espacial calculado.");
							}
							FLyrVect layerOrigins = getOriginsLayer();
//							FLyrVect layerDestinations = ctrlDlg.getDestinationsLayer();
//							boolean bSameLayer = false;
//							if (layerOrigins == layerDestinations)
//								bSameLayer = true;
							double tolerance = getTolerance();
							GvFlag[] originFlags = NetworkUtils.putFlagsOnNetwork(layerOrigins,
									net, tolerance);
							GvFlag[] destinationFlags = null; 
//							if (bSameLayer)
								destinationFlags = originFlags;
//							else
//								destinationFlags = NetworkUtils.putFlagsOnNetwork(layerDestinations, net, tolerance);

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
							
							// TODO: ASK THE USER IF HE WANTS TO SAVE FLAGS TO AVOID PUTTING POINTS
							// ON NETWORK AGAIN
							
							// If the user cancels the task we don't open the file
							if (task.isCanceled())
								return false;
							
							openODMatrixFile();	// Opens the file so that no-one can change it in between
							
							return true;	// Return ok
						} // isOkPressed
					} catch (BaseException e) {
						e.printStackTrace();
						if (e.getCode() == GraphException.FLAG_OUT_NETWORK) {
							showMessageDialog("there_are_points_outside_the_tolerance");
//							NotificationManager.addError(e.getFormatString(), e);
						return false; //Return Error
						}
					}

				}
			} 
		return false; 
	}
	
	// Open existing ODMatrix file
	public boolean openODMatrixFile(){
		if (!selectedFile.exists()){
			this.showMessageDialog("File_doesnt_exist");
			return false;
		}
		if (!selectedFile.canRead()){
			this.showMessageDialog("File_cannot_be_read");
			return false;
		}
		
		// Open the file with a stream to guarantee that no one changes it in the meanwhile
		try {
		fstream = new FileInputStream(selectedFile);
		}catch (Exception e){	//The program never gets here (I hope!)
            e.printStackTrace();
        }
		
		// Get the format of the file
		selectedWriter = odMatrixWriters.get(getFileFormat());
		
		return true;	// Everything went fine
	}
}
