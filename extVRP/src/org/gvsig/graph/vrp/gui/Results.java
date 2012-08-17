// TODO: Description of file
// TODO: Date

package org.gvsig.graph.vrp.gui;

//Needed imports
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.graph.solvers.Route;
import org.gvsig.graph.vrp.support.DrawRoutes;
import org.metavrp.algorithm.GA.Chromosome;
import org.metavrp.algorithm.GA.Gene;
import org.metavrp.algorithm.GA.phenotype.CVRPTours;
import org.metavrp.algorithm.GA.phenotype.Tours;
import org.metavrp.algorithm.GA.phenotype.Tours.Tour;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.IFeature;

//TODO: description of class
public class Results implements Tab {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private JPanel tabResults;
	private JTextField textField;
	
	private JTable resultsTable;						// The table with the results
	private DefaultTableModel tableModel;				// The table model. Used to update the table values

	private Results_Preview preview;					// The Preview window
	private Thread previewThread;						// The Preview window's thread (that updates his content)
	
	private Chromosome[] finalPop;						// The final population
	

	/**
	 * Constructor.
	 * Just initializes the Control Panel on witch this JPanel will be drawn.
	 * @param controlPanel
	 */
	public Results(VRPControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}
	
	/**
	 * Initialize Results tab.
	 * @wbp.parser.entryPoint
	 */
	@SuppressWarnings("serial")
	public JPanel initTab() {

		tabResults = new JPanel();
		tabResults.setLayout(null);
		
		// Results Table
		// TODO: Add tooltips to the cells
		// TODO: Add tooltips to the column headers
		Object rowData[][] = {{"#","#","#"}};
		
		// TODO: Internationalize the column names
		String rank = controlPanel._T("Rank");
		String totalCost = controlPanel._T("Total_cost");
		String improvement = controlPanel._T("Improvement_percentage");		
		Object columnNames[] = {rank, totalCost, improvement};

		tableModel = new DefaultTableModel(rowData, columnNames) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //All cells false. No one is editable
		       return false;
		    }
		};

		resultsTable = new JTable(tableModel);
		resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultsTable.setColumnSelectionAllowed(false);
		
		// Put the table in a JScrollPane to permit scrolling
		JScrollPane scrollPane = new JScrollPane(resultsTable);
		resultsTable.setFillsViewportHeight(true);
		resultsTable.setRowSelectionInterval(0, 0);
		
		// Put the JScrollPane on the Results tab
		scrollPane.setBounds(10, 36, 465, 172);
		tabResults.add(scrollPane);
		
		// Button "Next >>"
		JButton btnNextTab5 = new JButton("Close");
		btnNextTab5.setBounds(386, 278, 89, 23);
		btnNextTab5.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCloseActionPerformed();
			}
		});
		
		JLabel lblShowTop = new JLabel("Show the top");
lblShowTop.setEnabled(false);
		lblShowTop.setBounds(10, 229, 64, 14);
// TODO: Add this (when its operational)
//		tabResults.add(lblShowTop);
		
		textField = new JTextField();
textField.setEnabled(false);
		textField.setBounds(76, 227, 30, 20);
// TODO: Add this (when its operational)
//		tabResults.add(textField);
		textField.setText("10");
		textField.setColumns(3);
		
		JLabel lblResults = new JLabel("results");
		lblResults.setBounds(108, 230, 32, 14);
// TODO: Add this (when its operational)
//		tabResults.add(lblResults);
		tabResults.add(btnNextTab5);
		
		// Button "<< Undo"
		JButton btnPreviousTab5 = new JButton("<< Back");
		btnPreviousTab5.setBounds(287, 278, 89, 23);
		btnPreviousTab5.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnPreviousResultsActionPerformed();
			}
		});
		tabResults.add(btnPreviousTab5);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Export as a Layer of type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setToolTipText("");
		panel.setBounds(20, 217, 204, 53);
		tabResults.add(panel);
		panel.setLayout(null);
		
		JButton exportPointsButton = new JButton("Point");
		exportPointsButton.setBounds(10, 19, 89, 23);
		exportPointsButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// Get the index of the selected row
				int rowIndex = resultsTable.getSelectedRow();
				if (rowIndex < 0){
					// If the index of the selected row is -1, is because the user didn't chose any row
					controlPanel.showMessageDialog("First_choose_row");
				} else {
					controlPanel.showMessageDialog("Selected_solution_will_be_exported");
					exportPoints(finalPop[rowIndex]);
				}
			}
		});
		panel.add(exportPointsButton);
		
		JButton exportLinesButton = new JButton("Line");
		exportLinesButton.setBounds(109, 19, 89, 23);
		exportLinesButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exportLines(controlPanel.getRun().getBestElement());
			}
		});
		panel.add(exportLinesButton);
		
		JButton showDetails = new JButton("Show Details");
		showDetails.setBounds(386, 233, 89, 23);
		showDetails.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// Get the index of the selected row
				int rowIndex = resultsTable.getSelectedRow();
				if (rowIndex < 0){
					// If the index of the selected row is -1, is because the user didn't chose any row
					controlPanel.showMessageDialog("First_choose_row");
				} else {
					showDetails(finalPop[rowIndex]);
				}
			}
		});
		tabResults.add(showDetails);
		
		final JToggleButton tglbtnPreview = new JToggleButton("Preview >>");
tglbtnPreview.setEnabled(false);		
		tglbtnPreview.setBounds(386, 233, 89, 23);
		tglbtnPreview.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (tglbtnPreview.isSelected()){
					previewThread = new Thread (preview, "preview");
					previewThread.start();
					PluginServices.getMDIManager().addWindow(preview);
//					preview.refreshWindowInfo();	// To force a refresh of the WindowInfo
//					preview.repaint();
					tglbtnPreview.setText("Preview <<");
				}
				else {
					previewThread.stop();
					PluginServices.getMDIManager().closeWindow(preview);
					tglbtnPreview.setText("Preview >>");
				}
			}
		});
// TODO: Add this button
//		tabResults.add(tglbtnPreview);
		
		preview = new Results_Preview(controlPanel);
		
		return tabResults;
	}
	
	/**
	 * What should be done when the user comes from the previous tab.
	 * Create the data that will show on the table.
	 */
	public void fromPreviousTab(){
		generateRowData();
	}
	
	/**
	 * What should be done when the user comes from the next tab.
	 */
	public void fromNextTab(){
		
	}

	/**
	 * Generate the data that will be shown on the result's table
	 */
	public void generateRowData(){
	    // Get the cost of the best individual from the first (randomly generated) population
        double costBestInitialChromosome = controlPanel.getRun().getInitialBestElementCost();

        // Get the Chromosomes from the last population. Get them properly sorted
        int numberChromosomes = controlPanel.getRun().getVrpLastPopulation().popSize();
        finalPop = controlPanel.getRun().getVrpLastPopulation().getTop(numberChromosomes);

		// Delete all rows of the table
		tableModel.setRowCount(0);
		
		// The rows to be added to the table will be String Arrays
		String[] row = new String[3];
        
		// Go throught every chromosome, measure the needed values and put them on a bidimentional array to be shown on the table
        for (int i=0; i<numberChromosomes; i++){
        	// The first column shows the rank
        	row[0]=String.valueOf(i+1);
        	
        	// The second column shows the cost
        	double cost = finalPop[i].getFitness();
        	row[1]=String.valueOf(cost);
        	
        	// The third column shows the improvement percentage rounded to two decimal plates
        	double improvement = (1 - (cost / costBestInitialChromosome)) * 100;
        	DecimalFormat twoDForm = new DecimalFormat("#.##");
        	row[2]=String.valueOf(twoDForm.format(improvement));
        	
        	// Finally add the row to the table
        	tableModel.addRow(row);
         }
	}
	
	/**
	 * Show the details of the selected solution.
	 * @param chromosome
	 */
	public void showDetails(Chromosome chromosome){
		// 1. Get the tours of this chromosome
		Tours tours = new CVRPTours(chromosome).getTours();

		// 2. Create the html table and the fist row with the column names
		String html = "<table border=\"1\" cellpadding=\"2\" cellspacing=\"2\"> <tbody>"+
		"<tr><td>Vehicle</td><td>Customer / Depot</td><td>Layer Row</td><td>Load</td><td>Load %</td><td>Distance</td><td>Total Distance</td></tr>";
		
		// 3. Get the various tours and create their html
		for (int t=0;t<tours.nTours();t++){
			// Get the tour
			Tour tour = tours.getTour(t);
			// Get the vehicle's identification
			String vehicleId = tour.getVehicle().getId();
			// TODO: Make the table more beautiful, joining the same vehicle cells
			
			// The necessary variables
			char type = 'C';
			int layerRow=0;
			float load=0f;
			float loadPercentage=0f;
			float distance=0f;
			float totalDistance=0f;
			// At the beginning, the first gene is the depot
			int previousGeneIndex = tour.getVehicle().getDepot();
			// For every gene on the tour, build a new row on the table with all the necessary information
			for (Gene gene:tour.getCustomers()){
				// The type (C for customer, D for depot)
				if (gene.getIsCustomer()){
					type = 'C';
				} else {
					type = 'D';
				}
				// The layer row number
				layerRow = gene.getNode();
				// The current load and load %
				if (gene.getIsCustomer()){
					load += gene.getSize();
					loadPercentage = load*100f/tour.getVehicle().getCapacity();
				} else {
					load = 0f;
					loadPercentage = 0f;
				}
				// The distance from the previous node
				distance=chromosome.getCost(previousGeneIndex, gene.getNode());
				// The distance travelled by this vehicle
				totalDistance += distance;
				
				// Update the previous gene index by this gene's index
				previousGeneIndex = gene.getNode();
				
				// Fill the table row
				html += "<tr><td>"+vehicleId+"</td><td>"+type+"</td><td>"+layerRow+"</td><td>"+load+"</td><td>"+loadPercentage+
						"</td><td>"+distance+"</td><td>"+totalDistance+"</td></tr>";
			}
		}
		
		// 4. Finish the table's html
		html += "</tbody></table>";
		
		// Show the solution details on a new window
        try
        {
        	// Create the window
            JFrame frame = new JFrame("Solution Details");
            // Create an editor pane
            JEditorPane editor = new JEditorPane();
            // Use scroll
            JScrollPane scroll = new JScrollPane(editor);
            frame.getContentPane().add(scroll);
            // Close operation
            // TODO: This closes gvSIG and should be changed
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            
            // The editor uses HTML
            editor.setContentType("text/html");
            
            // Insert the contents
            editor.setText(html);
            
            // Show the window
            frame.pack();
            frame.setVisible(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
		
	}
	
	
	/**
	 * Exports the Chromosome to a point layer
	 * @param chromosome
	 */
	public void exportPoints(Chromosome chromosome){
		//1. Get the network
		Network net = controlPanel.getNetwork();
		
		//2. Save the original flags
		GvFlag[] flagsBackup = net.getFlags();

		//3. Remove all the flags from the network
		net.removeFlags();
		
		//4. Draw the straight lines on the layer
		// TODO: Verificar o caso do veículo ter que regressar à base antes do próximo veículo.
		// TODO: Verificar também o caso do veiculo regressar à base para descarregar, por estar cheio.

		// Create the list of lines
		ArrayList<IFeature> geometries = null;
		// Calculate the lines between all the adjacent nodes of the chromosome
		geometries = DrawRoutes.calculateStraightRoutes(chromosome, controlPanel);
// TODO: Another Option
//GvSession.getInstance().put(mapCtrl, "Route", routes);		
		// Clear the features from the GraphicLayer
		NetworkUtils.clearRouteFromGraphics(controlPanel.getMapControl());
		// Draw the features on the map
		DrawRoutes.createGraphicsFrom(geometries, controlPanel.getMapControl());
		
		//5. Rebuild the original flags
		net.removeFlags();
		for (GvFlag flag : flagsBackup){
			net.addFlag(flag);
		}
	}
	
	/**
	 * Exports the Chromosome to a Line layer
	 * @param bestChr
	 */
	public void exportLines(Chromosome bestChr){
		//1. Get the network
		Network net = controlPanel.getNetwork();
		
		//2. Save the original flags
		GvFlag[] flagsBackup = net.getFlags();

		//3. Remove all the flags from the network
		net.removeFlags();
		
		//4. Draw the lines on the layer
		// TODO: Verificar o caso do veículo ter que regressar à base antes do próximo veículo.
		// TODO: Verificar também o caso do veiculo regressar à base para descarregar, por estar cheio.
		// TODO: Desenhar com cores diferentes para veículos diferentes.
		// Create the list of routes
		ArrayList<Route> routes = null;
		// Calculate the routes between all the adjacent nodes of the chromosome
		routes = DrawRoutes.calculateRoutes(bestChr, net, controlPanel, tabResults);
		// Draw the routes on the map
//		FLayer roadLayer = net.getLayer();
		// Create a memory driver
//		MemoryDriver driver = new ConcreteMemoryDriver();

//		FLayer roadLayer = LayerFactory.createLayer("VehileRouting", driver, net.getLayer().getProjection());

//		controlPanel.getMapControl().getMapContext().addToTrackLayer(vectorial);
		
		NetworkUtils.clearRouteFromGraphics(controlPanel.getMapControl());
		for (Route route:routes){
			NetworkUtils.drawRouteOnGraphics(controlPanel.getMapControl(), route);
		}
		
		//5. Rebuild the original flags
		net.removeFlags();
		for (GvFlag flag : flagsBackup){
			net.addFlag(flag);
		}
	}
		
	/**
	 * Returns the final population. All his chromosomes
	 * @return
	 */
	public Chromosome[] getFinalPop() {
		int numberChromosomes = controlPanel.getRun().getVrpLastPopulation().popSize();
		return controlPanel.getRun().getVrpLastPopulation().getTop(numberChromosomes);
	}

	/**
	 * Returns the best element of the final population. The chromosome with the best fitness
	 * @return
	 */
	public Chromosome getBestElement() {
		return controlPanel.getRun().getVrpLastPopulation().getTop(1)[0];
	}
	
	/**
	 * The button's "Close" functionality.
	 * Just closes the window and stops the threads.
	 */
	public void btnCloseActionPerformed(){
		// TODO: Show message alert "Are you sure you want to close this?"
		// Stop the thread.
		// TODO: the stop method is @deprecated. Create some boolean var in the run tab to detect that it should stop.
		controlPanel.getRun().getStatsThread().stop();
		// Close the application.
		controlPanel.closeWindow();
	}
	
	/**
	 * The button's "<< Back" functionality.
	 * Just goes back to the previous tab.
	 */
	public void btnPreviousResultsActionPerformed(){
		// Go back to the Run tab
		controlPanel.switchToPreviousTab();
	}
}