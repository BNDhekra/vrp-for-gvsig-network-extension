package org.gvsig.graph.vrp.gui;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.styles.ArrowDecoratorStyle;
import com.iver.cit.gvsig.fmap.core.styles.ILineStyle;
import com.iver.cit.gvsig.fmap.core.styles.SimpleLineStyle;
import com.iver.cit.gvsig.fmap.core.symbols.ArrowMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleLineSymbol;
import com.iver.cit.gvsig.fmap.drivers.MemoryDriver;
import com.iver.cit.gvsig.fmap.layers.FLayerStatus;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;
import com.iver.cit.gvsig.project.documents.view.gui.View;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JTextField;

import org.cresques.cts.ProjectionPool;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.solvers.Route;
import org.gvsig.graph.solvers.ShortestPathSolverAStar;
import org.gvsig.graph.vrp.support.DrawRoutes;
import org.gvsig.graph.vrp.support.Nodes;
import org.metavrp.GA.Chromosome;
import org.metavrp.GA.Gene;

public class Results_Preview extends JPanel implements IWindow, Runnable {

	// The VRP Control Panel that called this object
	private VRPControlPanel controlPanel;
	
	// The window that this panel uses (where it is painted)
	private WindowInfo wi;
	
	/*
	 * Constructor
	 */
	public Results_Preview(VRPControlPanel controlPanel) {
		this.controlPanel = controlPanel;
		initComponents();
	}

	/*
	 * Initialize this JPanel
	 */
	private void initComponents() {
		setSize(new Dimension(490, 340));
		setPreferredSize(new Dimension(490, 340));				//Preferred Size
		setLayout(null);
	}

	@Override
	public WindowInfo getWindowInfo() {
		if (wi == null) {
			wi = new WindowInfo(WindowInfo.RESIZABLE | WindowInfo.MAXIMIZABLE | WindowInfo.ICONIFIABLE | WindowInfo.MODELESSDIALOG | WindowInfo.NOTCLOSABLE);
			wi.setWidth((int) this.getPreferredSize().getWidth());
			wi.setHeight((int) this.getPreferredSize().getHeight());
			refreshWindowInfo();
			wi.setTitle("VRP Solution Preview");
		}
		return wi;
	}
	
	/*
	 * Refreshes the current WindowInfo with the current coordinates of the main window
	 */
	public void refreshWindowInfo(){
		// Get the sizes of the mother window
		int width = controlPanel.getWindowInfo().getWidth();
//		int height = controlPanel.getWindowInfo().getHeight();
		int X = controlPanel.getWindowInfo().getX() + width;
		int Y = controlPanel.getWindowInfo().getY();
		// Put them on this window
		wi.setX(X);
		wi.setY(Y);
	}

	@Override
	public Object getWindowProfile() {
//		return WindowInfo.EDITOR_PROFILE;
//		return WindowInfo.TOOL_PROFILE;
//		return WindowInfo.PROJECT_PROFILE;
//		return WindowInfo.PROPERTIES_PROFILE;
		return WindowInfo.DIALOG_PROFILE;
	}
	
	
	/*
	 * Runs this Preview window, showing the best result of the population(non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
        // From time to time, update the statistics and the graphic
        while(true){
        	synchronized (this){
	        	try{
	            	showBestResult();	// Shows the best element of the population
	            	wait(10000);		// Wait
	        	}
	        	catch(InterruptedException e){
	        		e.printStackTrace();
	        	}
        	}
        }
	}
	
	/*
	 * Shows a specified solution (a chromosome) on the map
	 */
	public void showResult(Chromosome chromosome){
		//1. Get the network
		Network net = controlPanel.getNetwork();
		
		//2. Save the original flags
		GvFlag[] flagsBackup = net.getFlags();

		//3. Remove all the flags from the network
		net.removeFlags();
		
		// TODO:
		//4. Draw the point layer on the map
		
		
		//5. Draw the approximated routes (straight lines) on the map
		// TODO: Verificar o caso do veículo ter que regressar à base antes do próximo veículo.
		// TODO: Verificar também o caso do veiculo regressar à base para descarregar, por estar cheio.
		// TODO: Desenhar com cores diferentes para veículos diferentes.
		drawStraightRoutes(chromosome);
		
		//6. Rebuild the original flags
		net.removeFlags();
		for (GvFlag flag : flagsBackup){
			net.addFlag(flag);
		}
		
//4. Measure the best routes (the shortest paths) between every two pairs of points
//		ArrayList<Route> routes = calculateRoutes(bestChromosome, net);
//5. Draw this routes on the map
//		draw(routes);
	}
	
	/*
	 * Shows the best element of the population in the content of the window
	 */
	public void showBestResult(){
		//1. Get the best element from the population
		Chromosome bestChromosome = controlPanel.getRun().getBestElement();
		
		//2. Show this element on the map
		showResult(bestChromosome);
	}
	
	
	/*
	 *  Draw straight lines between the adjacent nodes of the chromosome
	 */
	public void drawStraightRoutes (Chromosome bestChromosome){
		// Create the list of lines
		ArrayList<IFeature> geometries = null;
		// Calculate the lines between all the adjacent nodes of the chromosome
		geometries = DrawRoutes.calculateStraightRoutes(bestChromosome, controlPanel);

		// Add a new map to this JPanel and draw the lines
//		MapControl mapCtrl = new MapControl();
		MapControl mapCtrl = controlPanel.getMapControl();
		this.add(mapCtrl, java.awt.BorderLayout.CENTER);
//		mapCtrl.setProjection(controlPanel.getProjection());
//		mapCtrl.setSize(this.getWidth(), this.getHeight());
//		mapCtrl.commandRepaint();
		// Draw the features on the map
		DrawRoutes.createGraphicsFrom(geometries, mapCtrl);
	}
	
	

}
