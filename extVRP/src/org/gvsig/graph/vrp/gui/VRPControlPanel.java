/*
 * OdMatrixControlPanel.java
 *
 * Created on 20 de octubre de 2008, 13:19
 */

package org.gvsig.graph.vrp.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.cresques.cts.IProjection;
import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.IODMatrixFileWriter;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.vrp.VRPExtension;
import org.gvsig.graph.vrp.support.Costs;
import org.gvsig.graph.vrp.support.Nodes;
import org.metavrp.Problem;
import org.metavrp.algorithm.GeneticAlgorithm;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;


/**
 * 
 * @author David Pinheiro
 */
public class VRPControlPanel extends JPanel implements IWindow {
	
	private VRPExtension extension = null;
	private ArrayList<IODMatrixFileWriter> odMatrixWriters;
	private MapContext mapContext;
	private Network network;
	private WindowInfo wi;
	private Nodes nodes;
	private Costs costs;
	
	// TODO: Remove this?
	private boolean btnNext1Pressed = false;
	
	/*
	 * Variables declaration
	 */
	// Panels
	private JTabbedPane tabs;
	private JPanel tabLayers, tabCustomers, tabVehicles, tabGA, tabRun, tabResults;
	
	// Panel Objects
	private ODMatrix odmatrix;
	private Customers customers;
	private Vehicles vehicles;
	private GA ga;
	private Run run;
	private Results results;

	// metaVRP library objects
	private Problem metavrpProblem;
	private GeneticAlgorithm metavrpGA;

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
	public VRPControlPanel(ArrayList<IODMatrixFileWriter> odMatrixWriters, Network net, VRPExtension extension) {
		this.extension=extension;
		this.odMatrixWriters=odMatrixWriters;
		this.network=net;
		// Initializes the graphical interface
		initComponents();
		// Starts up the first tab
		odmatrix.fromPreviousTab();
	}

	/**
	 * Initialize the GUI of the plugin.
	 */
	private void initComponents() {

		tabs = new JTabbedPane(JTabbedPane.TOP);
		tabs.setBounds(5, 5, 490, 340);
		tabs.setPreferredSize(new Dimension(490, 340));
		tabs.setMinimumSize(new Dimension(100, 100));
		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		// First tab: Cost Matrix
		odmatrix = new ODMatrix(this, odMatrixWriters);
		tabLayers = odmatrix.initTab();
		tabs.addTab("OD Matrix", null, tabLayers, null);
		
		// Second tab: Customers
		customers = new Customers(this);
		tabCustomers = customers.initTab();
		tabs.addTab("Customers", null, tabCustomers, null);

		// Third tab: Vehicles
		vehicles = new Vehicles(this);
		tabVehicles = vehicles.initTab();
		tabs.addTab("Vehicles", null, tabVehicles, null);
		
		// Fourth tab: Genetic Algorithm
		ga = new GA(this);
		tabGA = ga.initTab();
		tabs.addTab("GA", null, tabGA, null);
		
		// Fifth tab: Run the GA
		run = new Run(this);
		tabRun = run.initTab();
//		run.run();	// Prepare the thread to run when the problem is correctly defined 
		tabs.addTab("Run", null, tabRun, null);

		// Sixth tab: Best Results
		results = new Results(this);
		tabResults = results.initTab();
		tabs.addTab("Results", null, tabResults, null);
		
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
		tabs.setEnabledAt(5, false);
	}

	public void closeWindow() {
		if (PluginServices.getMainFrame() != null) {
			PluginServices.getMDIManager().closeWindow(this);
		}
	}

	
	/**
	 * @return Returns the okPressed.
	 */
	public boolean isOkPressed() {
		return btnNext1Pressed;
	}


	/*
	 * Getters and Setters
	 */
	
	/**
	 * Sets the MapContext, that represents the model and a part of the control and view
	 * around graphical layers used by MapControl.
	 * @param mapContext
	 * @throws BaseException
	 */
	// Sets
	public void setMapContext(MapContext mapContext) throws BaseException {
		// Set the map context
		this.mapContext = mapContext;
	}
	
	/**
	 * Returns the map context
	 * 
	 * @return MapContext
	 */
	public MapContext getMapContext() {
		return mapContext;
	}

	public WindowInfo getWindowInfo() {
		if (wi == null) {
//			wi = new WindowInfo(WindowInfo.MODALDIALOG);
//			wi = new WindowInfo(WindowInfo.MODELESSDIALOG);
			wi = new WindowInfo(WindowInfo.PALETTE);
			wi.setWidth((int) this.getPreferredSize().getWidth());
			wi.setHeight((int) this.getPreferredSize().getHeight());
			wi.setTitle(_T("vrp_control_panel"));
		}
		return wi;
	}
	
	public IProjection getProjection(){
		return extension.getMapControl().getProjection();
	}
	
	public MapControl getMapControl(){
		return extension.getMapControl();
	}
	
	public Object getWindowProfile(){
		return WindowInfo.DIALOG_PROFILE;
	}
	
	
	public Network getNetwork() {
		return network;
	}
	
	public Nodes getNodes() {
		return nodes;
	}
	public void setNodes(Nodes nodes) {
		this.nodes = nodes;
	}
	public Costs getCosts() {
		return costs;
	}
	public void setCosts(Costs costs) {
		this.costs = costs;
	}

	// Returns the ODMatrix object
	public ODMatrix getODMatrix(){
		return odmatrix;
	}
	
	// Returns the Customers object
	public Customers getCustomers(){
		return customers;
	}
	
	// Returns the Vehicles object
	public Vehicles getVehicles(){
		return vehicles;
	}
	
	// Returns the GA object
	public GA getGA(){
		return ga;
	}
	
	// Returns the Run object
	public Run getRun(){
		return run;
	}
	
	// Returns the Results object
	public Results getResults(){
		return results;
	}
	
	// Switch to the next tab and hide the one before
	public void switchToNextTab(){
		int selectedTabIndex = tabs.getSelectedIndex();
		tabs.setEnabledAt(selectedTabIndex, false);
		tabs.setEnabledAt(selectedTabIndex+1, true);
		tabs.setSelectedIndex(selectedTabIndex+1);
	}
	
	// Switch to the previous tab and hide this one
	public void switchToPreviousTab(){
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
	public String _T(String str) {
		return PluginServices.getText(this, str);
	}

	public Problem getMetavrpProblem() {
		return metavrpProblem;
	}

	public GeneticAlgorithm getMetavrpGA() {
		return metavrpGA;
	}

}
