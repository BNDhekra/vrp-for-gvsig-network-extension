package org.gvsig.graph.vrp.support;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.solvers.Route;
import org.gvsig.graph.solvers.ShortestPathSolverAStar;
import org.gvsig.graph.vrp.gui.VRPControlPanel;
import org.metavrp.algorithm.GA.Chromosome;
import org.metavrp.algorithm.GA.Gene;

import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
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
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;

public class DrawRoutes {

	/*
	 *  Calculate lines (Polyline2D's) between all the adjacent nodes of the chromosome  
	 */
	public static ArrayList<IFeature> calculateStraightRoutes(Chromosome chromosome, VRPControlPanel controlPanel){
		// Create the list of geometries
		ArrayList<IFeature> geometries = new ArrayList<IFeature>();
		try{
		// Get all the nodes (Customers and Vehicles)
		Nodes nodes = controlPanel.getNodes();
		// Create genes and flags		
		Gene geneA, geneB;
		GvFlag flagA, flagB;
		// Get the lenght of the chromosome
		int lenght = chromosome.getLenght();
		// With any two adjacent genes, draw a straight line between their nodes on the map
		for (int i=0; i<lenght; i++){
			// Get the genes and the respective flags
			if (i==0){
				geneA = chromosome.getGene(lenght-1);
			} else {
				geneA = chromosome.getGene(i-1);
			}
			geneB = chromosome.getGene(i);
			flagA = nodes.getNode(geneA).getFlag();
			flagB = nodes.getNode(geneB).getFlag();
			
			GeneralPathX line = new GeneralPathX();
			line.moveTo(flagA.getOriginalPoint().getX(), flagA.getOriginalPoint().getY());
			line.lineTo(flagB.getOriginalPoint().getX(), flagB.getOriginalPoint().getY());
			DefaultFeature feature = new DefaultFeature(ShapeFactory.createPolyline2D(line), new Value[0], Integer.toString(i));
			geometries.add(feature);
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return geometries;
	}
	
	/*
	 *  Given some collection of features, draw its graphics on a MapControl
	 */
	public static void createGraphicsFrom(Collection featureList, MapControl mapControl) {
		Iterator it = featureList.iterator();
		GraphicLayer graphicLayer = mapControl.getMapContext().getGraphicsLayer();
		SimpleLineSymbol arrowSymbol = new SimpleLineSymbol();
		arrowSymbol.setLineWidth(3.0f);
		ILineStyle lineStyle = new SimpleLineStyle();
		ArrowDecoratorStyle arrowDecoratorStyle = new ArrowDecoratorStyle();
		ArrowMarkerSymbol marker = (ArrowMarkerSymbol) arrowDecoratorStyle.getMarker(); 
		marker.setSize(16);
		marker.setColor(Color.BLUE);
		arrowDecoratorStyle.setArrowMarkerCount(1);
		lineStyle.setArrowDecorator(arrowDecoratorStyle );
		lineStyle.setLineWidth(2.0f);
		arrowSymbol.setLineColor(Color.BLUE);
		arrowSymbol.setAlpha(120);
		arrowSymbol.setLineStyle(lineStyle);
		int idSymbolLine = graphicLayer.addSymbol(arrowSymbol);

		ArrayList<FGraphic> graphicsRoute = new ArrayList<FGraphic>();
		while (it.hasNext()) {
			IFeature feat = (IFeature) it.next();
			IGeometry gAux = feat.getGeometry();
			FGraphic graphic = new FGraphic(gAux, idSymbolLine);
			graphic.setTag("ROUTE");
			graphicsRoute.add(graphic);
		}
		
		graphicLayer.clearAllGraphics();	// Remove all the features from the GraphicLayer
//		mapControl.commandRepaint();
//		mapControl.rePaintDirtyLayers();
		graphicLayer.inserGraphics(0, graphicsRoute);
		mapControl.drawMap(false);
		mapControl.drawGraphics();
	}
	
	/*
	 *  Calculates all the Routes between every two points on a Network
	 */
	public static ArrayList<Route> calculateRoutes(Chromosome bestChromosome, Network net, VRPControlPanel controlPanel, JPanel panel){
		ArrayList<Route> routes = new ArrayList<Route>();
		Nodes nodes = controlPanel.getNodes();
		
		Gene geneA, geneB;
		GvFlag flagA, flagB;
		int lenght = bestChromosome.getLenght();
		for (int i=1; i<lenght; i++){
			geneA = bestChromosome.getGene(i-1);
			geneB = bestChromosome.getGene(i);
			
			flagA = nodes.getNode(geneA).getFlag();
			flagB = nodes.getNode(geneB).getFlag();
			
			routes.add(calculateRoute(flagA, flagB, net, panel));
		}
		return routes;
	}
	
	/*
	 *  Calculates the Route between two points on a Network
	 */
	// TODO: testar a performance do Dijkstra vs AStar
	public static Route calculateRoute(GvFlag flag1, GvFlag flag2, Network net, JPanel panel){
		ShortestPathSolverAStar solver = new ShortestPathSolverAStar();
		Route route = null;
		
		try{
			net.removeFlags();
			solver.setNetwork(net);
			net.addFlag(flag1);
			net.addFlag(flag2);
			String fieldStreetName = (String) net.getLayer().getProperty("network_fieldStreetName");
			solver.setFielStreetName(fieldStreetName);
			route = solver.calculateRoute();
		} catch (GraphException ex){
			ex.printStackTrace();
		}
		if (route.getFeatureList().size() == 0)
		{
			JOptionPane.showMessageDialog((JComponent) PluginServices.getMDIManager().getActiveWindow(),
					PluginServices.getText(panel, "shortest_path_not_found"));
		}
		
		return route;
	}
	
	
	/*
	 *  Draw the routes on the overview map
	 */
	public static void draw(ArrayList<Route> routes, VRPControlPanel controlPanel, JPanel panel){
		MapControl mapCtrl = controlPanel.getMapControl();
		panel.add(mapCtrl, java.awt.BorderLayout.CENTER);
		for (Route route:routes){
			DrawRoutes.createGraphicsFrom(route.getFeatureList(), mapCtrl);
		}
//		mapCtrl.setProjection(controlPanel.getProjection());
//		mapCtrl.setSize(this.getWidth(), this.getHeight());
//		mapCtrl.commandRepaint();
	}

}
