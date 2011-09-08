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

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.IODMatrixFileWriter;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;

import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;
import com.iver.cit.gvsig.fmap.layers.FLayer;


/**
 * 
 * @author David Pinheiro
 */
public class VRPControlPanel extends JPanel implements IWindow {

	private MapContext mapContext;
	private ArrayList<IODMatrixFileWriter> odMatrixWriters;
	private WindowInfo wi;
	
	// TODO: Remove this?
	private boolean btnNext1Pressed = false;
	
	/*
	 * Variables declaration
	 */
	// Panels
	private JTabbedPane tabs;
	private JPanel tabLayers, tabVehicles, tabGA, tabRun, tabResults;
	
	// Panel Objects
	private ODMatrix odmatrix;
	private Vehicles vehicles;
	private GA ga;
	private Run run;
	private Results results;


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

		// Second tab: Vehicles
		vehicles = new Vehicles(this);
		tabVehicles = vehicles.initTab();
		tabs.addTab("Vehicles", null, tabVehicles, null);
		
		// Third tab: Genetic Algorithm
		ga = new GA(this);
		tabGA = ga.initTab();
		tabs.addTab("GA", null, tabGA, null);
		
		// Fourth tab: Run the GA
		run = new Run(this);
		tabRun = run.initTab();
		tabs.addTab("Run", null, tabRun, null);

		// Fifth tab: Best Results
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
	}

	private void closeWindow() {
		if (PluginServices.getMainFrame() != null) {
			PluginServices.getMDIManager().closeWindow(this);
		}
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


	/*
	 * Getters and Setters
	 */
	
	// TODO: Is this method really needed?
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
		
		// If the object tabLayers is instantiated, update its layers
		// TODO: First detect that the ODMatrix JPanel has been initialized with initTab, otherwise
		// this will throw an exception? Or try to find a workaround
			odmatrix.updateOriginsLayers(arrayLayers);
	}
	

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
	
	// Returns the ODMatrix object
	public ODMatrix getODMatrix(){
		return odmatrix;
	}
	
	// Returns the Vehicles object
	public Vehicles getVehicles(){
		return vehicles;
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
	

}
