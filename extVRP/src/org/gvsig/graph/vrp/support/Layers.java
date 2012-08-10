package org.gvsig.graph.vrp.support;

import java.util.Vector;

import org.gvsig.exceptions.BaseException;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;

/**
 * This class provides supporting methods to be used by others.
 * Supports some Layer based methods.
 * @author David Pinheiro
 */
public class Layers {
	
	/**
	 * Returns all the layers of type POINT and MULTIPOINT.
	 * @param mapContext
	 * @return Vector of vectorial Layers
	 * @throws BaseException
	 */
	public static Vector<FLyrVect> getPointLayers(MapContext mapContext) throws BaseException {
		/** Layers currently on the workspace */
		FLayers layers = mapContext.getLayers();
		
		/** Layer iterator */
		LayersIterator it = new LayersIterator(layers);
		
		/** A vector of layers (of the type FLyrVect) */
		Vector<FLyrVect> arrayLayers = new Vector<FLyrVect>();
		
		// Iterates through every layer on the workspace and puts the ones of type POINT or MULTIPOINT on an array
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
		
		return arrayLayers;
	}
}
