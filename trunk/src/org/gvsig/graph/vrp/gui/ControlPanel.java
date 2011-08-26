package org.gvsig.graph.vrp.gui;

import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.Component;
import javax.swing.JTabbedPane;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;

import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;

import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.util.LayerListCellRenderer;
import javax.swing.JRadioButton;
import java.awt.Rectangle;

import org.gvsig.graph.gui.OdMatrixControlPanel;
import javax.swing.ButtonGroup;
import javax.swing.border.EtchedBorder;


public class ControlPanel extends JPanel implements IWindow {

	private MapContext mapContext;
	
	private WindowInfo wi;
	private JTextField tolerance;
	private JTextField textField_1;
	
	private DefaultComboBoxModel pointLayerModel;
	private JComboBox pointLayer;
	
	
	private boolean btnNextPressed = false;
	private JTextField textField;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	/**
	 * Create the panel.
	 */
	public ControlPanel() {
		setPreferredSize(new Dimension(500, 350));
		setMinimumSize(new Dimension(100, 100));
		setLayout(null);
		
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
		tabs.setBounds(5, 5, 490, 340);
		tabs.setPreferredSize(new Dimension(490, 340));
		tabs.setMinimumSize(new Dimension(100, 100));
		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		add(tabs);
		
		JPanel layers = new JPanel();
		tabs.addTab("OD Matrix", null, layers, null);
		layers.setLayout(null);
		
		JPanel layerTolerance = new JPanel();
		layerTolerance.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		layerTolerance.setBounds(10, 10, 465, 121);
		layers.add(layerTolerance);
		layerTolerance.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Point Layer:");
		lblNewLabel.setBounds(64, 9, 58, 14);
		layerTolerance.add(lblNewLabel);
		lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		
		JComboBox layer = new JComboBox();
		layer.setBounds(127, 6, 208, 20);
		layerTolerance.add(layer);
		
		JLabel lblNewLabel_1 = new JLabel("Tolerance:");
		lblNewLabel_1.setBounds(71, 40, 51, 14);
		layerTolerance.add(lblNewLabel_1);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.TRAILING);
		
		tolerance = new JTextField();
		tolerance.setBounds(127, 37, 86, 20);
		layerTolerance.add(tolerance);
		tolerance.setColumns(10);
		
		JLabel lblMeters = new JLabel("meters");
		lblMeters.setBounds(218, 40, 33, 14);
		layerTolerance.add(lblMeters);
		
		JPanel ODMatrix = new JPanel();
		ODMatrix.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		ODMatrix.setBounds(10, 138, 465, 121);
		layers.add(ODMatrix);
		ODMatrix.setLayout(null);
		
		JRadioButton rdbtnCreateNewOrigindestination = new JRadioButton("Create new Origin-Destination Matrix");
		rdbtnCreateNewOrigindestination.setBounds(16, 5, 203, 23);
		ODMatrix.add(rdbtnCreateNewOrigindestination);
		buttonGroup.add(rdbtnCreateNewOrigindestination);
		
		JRadioButton rdbtnOpenExistingOrigindestination = new JRadioButton("Open existing Origin-Destination Matrix");
		rdbtnOpenExistingOrigindestination.setBounds(16, 30, 213, 23);
		ODMatrix.add(rdbtnOpenExistingOrigindestination);
		buttonGroup.add(rdbtnOpenExistingOrigindestination);
		
		textField = new JTextField();
		textField.setBounds(85, 60, 214, 20);
		ODMatrix.add(textField);
		textField.setText("");
		
		JButton button = new JButton();
		button.setBounds(309, 59, 45, 23);
		ODMatrix.add(button);
		button.setText("...");
		
		JLabel lblFile = new JLabel("File:");
		lblFile.setBounds(38, 63, 37, 17);
		ODMatrix.add(lblFile);
		lblFile.setHorizontalAlignment(SwingConstants.TRAILING);
		
		JButton btnNext = new JButton("Next");
		btnNext.setBounds(420, 278, 55, 23);
		layers.add(btnNext);
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnNextActionPerformed(arg0);
			}
		});
		btnNext.setMaximumSize(new Dimension(105, 23));
		btnNext.setHorizontalAlignment(SwingConstants.RIGHT);
		btnNext.setVerticalAlignment(SwingConstants.BOTTOM);
		
		JPanel vehicles = new JPanel();
		vehicles.setEnabled(false);
		tabs.addTab("Vehicles", null, vehicles, null);
		vehicles.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("N\u00BA of vehicles:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel_2.setBounds(10, 14, 86, 14);
		vehicles.add(lblNewLabel_2);
		
		textField_1 = new JTextField();
		textField_1.setBounds(101, 11, 86, 20);
		vehicles.add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnNext_1 = new JButton("Run");
		btnNext_1.setBounds(386, 278, 89, 23);
		vehicles.add(btnNext_1);
		
		JButton btnBack = new JButton("Back");
		btnBack.setBounds(287, 278, 89, 23);
		vehicles.add(btnBack);
		
		JPanel run = new JPanel();
		run.setEnabled(false);
		tabs.addTab("Run", null, run, null);
		run.setLayout(null);
		
		JButton btnNewButton = new JButton("Stop");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton.setBounds(386, 278, 89, 23);
		run.add(btnNewButton);
		
		JButton btnRestart = new JButton("Restart");
		btnRestart.setBounds(287, 278, 89, 23);
		run.add(btnRestart);
		
		JPanel results = new JPanel();
		results.setEnabled(false);
		tabs.addTab("Results", null, results, null);
		results.setLayout(null);
		
		JButton btnNewButton_1 = new JButton("Restart");
		btnNewButton_1.setBounds(386, 278, 89, 23);
		results.add(btnNewButton_1);

	}
	
	private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {
		if (getGeneratedFile().equalsIgnoreCase(""))
		{
			JOptionPane.showMessageDialog((Component) PluginServices.getMDIManager().getActiveWindow(),
					PluginServices.getText(null, "Please_select_a_valid_file"));
			return;
		}
		try {
			double tol = getTolerance();
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog((Component) PluginServices.getMDIManager().getActiveWindow(),
					PluginServices.getText(null, "Please_enter_a_valid_number"));
			return;
		}
		
		btnNextPressed = true;
		closeWindow();
	}

	private void closeWindow() {
		if (PluginServices.getMainFrame() != null) {
			PluginServices.getMDIManager().closeWindow(this);
		}
	}
	
	public String getGeneratedFile() {
		return textField.getText();
	}

	public WindowInfo getWindowInfo() {
		if (wi == null) {
			wi = new WindowInfo(WindowInfo.MODALDIALOG);
			wi.setWidth((int) this.getPreferredSize().getWidth());
			wi.setHeight((int) this.getPreferredSize().getHeight());
			wi.setTitle("TSP/VRP solver");
		}
		return wi;
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

		pointLayerModel = new DefaultComboBoxModel(arrayLayers);
		//pointLayerModel.toString();
		pointLayer.setModel(pointLayerModel);
//		cboLayerOrigins.setModel(pointLayer);
//		cboLayerDestinations.setModel(cboLayerDestinationsModel);
		pointLayer.setRenderer(new LayerListCellRenderer());
//		cboLayerDestinations.setRenderer(new LayerListCellRenderer());
		
	}
	
	/**
	 * @return If the user presses the next button, this variable will be set to true.
	 */
	public boolean isBtnNextPressed() {
		return btnNextPressed;
	}
	
	public void setBtnNextPressed(boolean value){
		this.btnNextPressed=value;
	}
	
	public Object getWindowProfile(){
		return WindowInfo.DIALOG_PROFILE;
	}
	
	public double getTolerance() {
		return Double.parseDouble(tolerance.getText());
	}
	
	public FLyrVect getPointLayer() {
		return (FLyrVect) pointLayer.getSelectedItem();
	}
}
