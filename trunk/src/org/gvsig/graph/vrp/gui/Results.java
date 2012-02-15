// TODO: Description of file
// TODO: Date

package org.gvsig.graph.vrp.gui;

//Needed imports
import java.text.DecimalFormat;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import org.metavrp.GA.Chromosome;
import org.metavrp.GA.Population;

import com.iver.andami.PluginServices;

import javax.swing.border.TitledBorder;
import javax.swing.JToggleButton;

//TODO: description of class
public class Results {
	
	private VRPControlPanel controlPanel;				// The VRP Control Panel that called this object
	private JPanel tabResults;
	private JTextField textField;
	
	private DefaultTableModel tableModel;				// The table model. Used to update the table values

	private Results_Preview preview;					// The Preview window
	private Thread previewThread;						// The Preview window's thread (that updates his content)
	
	private Chromosome[] finalPop;						// The final population
	
	// Constructor.
	// Just initializes the Control Panel on witch this JPanel will be drawn.
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
		
		// TODO: Implement the columns Cost by Vehicle and Nodes by Vehicle
		Object columnNames[] = { "Rank", "Cost", "Improvement %"};

		tableModel = new DefaultTableModel(rowData, columnNames) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //All cells false. No one is editable
		       return false;
		    }
		};

		JTable resultsTable = new JTable(tableModel);
		resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Put the table in a JScrollPane to permit scrolling
		JScrollPane scrollPane = new JScrollPane(resultsTable);
		resultsTable.setFillsViewportHeight(true);
		
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
		lblShowTop.setBounds(10, 229, 64, 14);
		tabResults.add(lblShowTop);
		
		textField = new JTextField();
		textField.setBounds(76, 227, 30, 20);
		tabResults.add(textField);
		textField.setText("10");
		textField.setColumns(3);
		
		JLabel lblResults = new JLabel("results");
		lblResults.setBounds(108, 230, 32, 14);
		tabResults.add(lblResults);
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
		panel.setBounds(150, 214, 204, 53);
		tabResults.add(panel);
		panel.setLayout(null);
		
		JButton button = new JButton("Point");
		button.setBounds(10, 19, 89, 23);
		panel.add(button);
		
		JButton button_1 = new JButton("Line");
		button_1.setBounds(109, 19, 89, 23);
		panel.add(button_1);
		
		final JToggleButton tglbtnPreview = new JToggleButton("Preview >>");
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
		tabResults.add(tglbtnPreview);
		
		preview = new Results_Preview(controlPanel);
		
		return tabResults;
	}
	
	
	// Generate the data that will be shown on the result's table
	public void generateRowData(){
	    // Get the cost of the best individual from the first (randomly generated) population
        double bestInitialCost = controlPanel.getRun().getInitialBestElementCost();

        // Get the Chromosomes from the last population. Get them properly sorted
        int numberChromosomes = controlPanel.getRun().getVrpLastPopulation().popSize();
        Chromosome[] finalPop = controlPanel.getRun().getVrpLastPopulation().getTop(numberChromosomes);

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
        	double improvement = (1 - (cost / bestInitialCost)) * 100;
        	DecimalFormat twoDForm = new DecimalFormat("#.##");
        	row[2]=String.valueOf(twoDForm.format(improvement));
        	
        	// Finally add the row to the table
        	tableModel.addRow(row);
         }
	}
	
	// Returns the final population. All his chromosomes
	public Chromosome[] getFinalPop() {
		int numberChromosomes = controlPanel.getRun().getVrpLastPopulation().popSize();
		return controlPanel.getRun().getVrpLastPopulation().getTop(numberChromosomes);
	}

	// Returns the best element of the final population. The chromosome with the best fitness
	public Chromosome getBestElement() {
		return controlPanel.getRun().getVrpLastPopulation().getTop(1)[0];
	}
	
	// The button's "Close" functionality.
	// Just closes the window and stops the threads
	public void btnCloseActionPerformed(){
		// TODO: Show message alert "Are you sure you want to close this?"
		// Stop the thread.
		// TODO: the stop method is @deprecated. Create some boolean var in the run tab to detect that it should stop.
		controlPanel.getRun().getStatsThread().stop();
		// Close the application.
		controlPanel.closeWindow();
	}
	
	// The button's "<< Back" functionality.
	// Just goes back to the previous tab.
	public void btnPreviousResultsActionPerformed(){
		// Go back to the Run tab
		controlPanel.switchToPreviousTab();
	}
}