package org.gvsig.graph.vrp.support;

import org.gvsig.graph.*;

import java.io.File;


public class Costs {
	
	float[][] costMatrix;	// Bi-dimentional array with the costs (distances?) between any two points
	
	// Create the Distance Matrix from a file with the specified format
	// TODO: The user can choose to minimize distance or time
	public void createCostMatrix(File file, IODMatrixFileWriter selectedWriter, boolean minimizeDistance){
		if (selectedWriter instanceof ODMatrixFileWriter4cols){
			
		}
		else if (selectedWriter instanceof ODMatrixFileWriter4cols_minutes_km){
			
		}
		else if (selectedWriter instanceof ODMatrixFileWriterRFormat){
			
		}
		else {
			// TODO: throw something
		}
	}
}
