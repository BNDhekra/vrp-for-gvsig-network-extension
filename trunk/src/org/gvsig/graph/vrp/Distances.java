package org.gvsig.graph.vrp;

import org.gvsig.graph.*;

import java.io.File;


public class Distances {
	
	float[][] distanceMatrix;	// Bi-dimentional array with the distances between any two points
	
	// Create the Distance Matrix from a file with the specified format
	public void createDistanceMatrix(File file, IODMatrixFileWriter selectedWriter, boolean minimizeDistance){
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
