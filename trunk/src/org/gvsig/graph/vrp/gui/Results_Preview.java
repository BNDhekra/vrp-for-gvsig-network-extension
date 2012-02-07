package org.gvsig.graph.vrp.gui;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.styles.ArrowDecoratorStyle;
import com.iver.cit.gvsig.fmap.core.styles.ILineStyle;
import com.iver.cit.gvsig.fmap.core.styles.SimpleLineStyle;
import com.iver.cit.gvsig.fmap.core.symbols.ArrowMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleLineSymbol;
import com.iver.cit.gvsig.fmap.drivers.MemoryDriver;
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

import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.solvers.Route;
import org.gvsig.graph.solvers.ShortestPathSolverAStar;
import org.gvsig.graph.vrp.support.Nodes;
import org.metavrp.GA.Chromosome;
import org.metavrp.GA.Gene;

public class Results_Preview extends JPanel implements IWindow, Runnable {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	
	// The window that this panel uses (where it is painted)
	private WindowInfo wi;
	
	// The thread that runs this window contents
	private Thread previewThread;
	private JTextField txtTeste;
	
	// The contents of this Panel
	
	
	// Constructor
	public Results_Preview(VRPControlPanel controlPanel) {
		this.controlPanel = controlPanel;
		initComponents();
	}

	// Initialize this JPanel
	private void initComponents() {
		setSize(new Dimension(490, 340));
		setPreferredSize(new Dimension(490, 340));				//Preferred Size
		setLayout(null);
		
		txtTeste = new JTextField();
		txtTeste.setBounds(192, 138, 86, 20);
		txtTeste.setText("teste");
		add(txtTeste);
		txtTeste.setColumns(10);
//	    setUndecorated(true);									//No decorations (border, buttons, etc)
//	    getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

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
	
	// Refreshes the current WindowInfo with the current coordinates of the main window
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
	
	
	// Runs this Preview window, showing the best result of the population
	public void run(){
System.out.println("O thread WindowInfo iniciou!");				
        // From 500ms to 500ms, update the statistics and the graphic
        while(true){
        	synchronized (this){
	        	try{
	            	showBestResult();	// Shows the best element of the population

	            	wait(500);			// Wait 500ms
	        	}
	        	catch(InterruptedException e){
	        		e.printStackTrace();
	        	}
        	}
        }
	}
	
	// Shows the best element of the population in the content of the window
	public void showBestResult(){
		//1. Get the network
		Network net = controlPanel.getNetwork();
		
		//2. Save the original flags
		GvFlag[] flagsBackup = net.getFlags();
		
		//3. Remove all the flags from the network
		net.removeFlags();
		
		//3. Get the best element from the population
		Chromosome bestChromosome = controlPanel.getResults().getBestElement();
		
		//4. Measure the best routes (the shortest paths) between every two pairs of points
		// TODO:Verificar o caso do veículo ter que regressar à base antes do próximo veículo
		// TODO:Verificar também o caso do veiculo regressar à base para descarregar, por estar cheio
		ArrayList<Route> routes = calculateRoutes(bestChromosome, net);
		
		//5. Draw this routes on the map
		// TODO: Desenhar com cores diferentes para veículos diferentes
		draw(routes);
		
		//6. Desenhar os pontos da layer de pontos nesse mapa
		// TODO:
		
		//7. Repôr flags originais na rede
		net.removeFlags();
		for (GvFlag flag : flagsBackup){
			net.addFlag(flag);
		}
	}
	
	// Calculates all the Routes between every two points on a Network
	public ArrayList<Route> calculateRoutes(Chromosome bestChromosome, Network net){
		ArrayList<Route> routes = new ArrayList<Route>();
		Nodes nodes = controlPanel.getNodes();
		
		Gene geneA, geneB;
		GvFlag flagA, flagB;
		for (int i=1; i<bestChromosome.getLenght(); i++){
			geneA = bestChromosome.getGene(i-1);
			geneB = bestChromosome.getGene(i);
			
			flagA = nodes.getNode(geneA).getFlag();
			flagB = nodes.getNode(geneB).getFlag();
			
			routes.add(calculateRoute(flagA, flagB, net));
		}

		return routes;
	}
	
	// Calculates the Route between two points on a Network
	// TODO: testar a performance do Dijkstra vs AStar
	public Route calculateRoute(GvFlag flag1, GvFlag flag2, Network net){
		ShortestPathSolverAStar solver = new ShortestPathSolverAStar();
		Route route = null;
		
		try{
			net.removeFlags();
			solver.setNetwork(net);
			net.addFlag(flag1);
			net.addFlag(flag2);
			// TODO: Verificar se a propriedade é mesmo esta...
			String fieldStreetName = (String) net.getLayer().getProperty("network_fieldStreetName");
			solver.setFielStreetName(fieldStreetName);
			route = solver.calculateRoute();
		} catch (GraphException ex){
			ex.printStackTrace();
		}
		if (route.getFeatureList().size() == 0)
		{
			JOptionPane.showMessageDialog((JComponent) PluginServices.getMDIManager().getActiveWindow(),
					PluginServices.getText(this, "shortest_path_not_found"));
		}
		
		return route;
	}
	
		
	// Given some collection of features, draw its graphics on a MapControl
	private void createGraphicsFrom(Collection featureList, MapControl mapControl) {
		Iterator it = featureList.iterator();
		GraphicLayer graphicLayer = mapControl.getMapContext().getGraphicsLayer();
		SimpleLineSymbol arrowSymbol = new SimpleLineSymbol();
		arrowSymbol.setLineWidth(3.0f);
		ILineStyle lineStyle = new SimpleLineStyle();
		ArrowDecoratorStyle arrowDecoratorStyle = new ArrowDecoratorStyle();
		ArrowMarkerSymbol marker = (ArrowMarkerSymbol) arrowDecoratorStyle.getMarker(); 
		marker.setSize(16);
		marker.setColor(Color.RED);
		arrowDecoratorStyle.setArrowMarkerCount(1);
		lineStyle.setArrowDecorator(arrowDecoratorStyle );
		lineStyle.setLineWidth(3.0f);
		arrowSymbol.setLineColor(Color.RED);
		arrowSymbol.setAlpha(120);
		arrowSymbol.setLineStyle(lineStyle);
		int idSymbolLine = graphicLayer.addSymbol(arrowSymbol);

//TODO:translate the following statement
		// Para evitar hacer reallocate de los elementos de la
		// graphicList cada vez, creamos primero la lista
		// y la insertamos toda de una vez.
		ArrayList<FGraphic> graphicsRoute = new ArrayList<FGraphic>();
		while (it.hasNext()) {
			IFeature feat = (IFeature) it.next();
			IGeometry gAux = feat.getGeometry();
			FGraphic graphic = new FGraphic(gAux, idSymbolLine);
			graphic.setTag("ROUTE");
			graphicsRoute.add(graphic);
//			graphicLayer.insertGraphic(0, graphic);
		}
		// Lo insertamos al principio de la lista para que los
		// pushpins se dibujen después.

		graphicLayer.inserGraphics(0, graphicsRoute);
		mapControl.drawGraphics();
	}
	
	// Draw the routes on the overview map
	public void draw(ArrayList<Route> routes){
		View v = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapCtrl = v.getMapControl();
		createGraphicsFrom(routes, mapCtrl);
		this.add(mapCtrl);
	}
}
