/*********************************************************************
* Copyright (c) 2019 The University of York.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* Contributors:
*     Horacio Hoyos - initial API and implementation
**********************************************************************/
package org.eclipse.scava.crossflow.examples.simple.nbody;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Create bodies by loading them from a JSON file. The file must be an array of objects, where
 * each object has the following structure:
 * <p>
 * <code>
 *	{
		"x": -0.773767,
		"y": 0.843888,
		"z": 0.739989,
		"vx": -0.725803,
		"vy": 0.41869,
		"vz": 0.944066,
		"m": -0.544131,
		"cr": 0.243596,
		"cb": -0.905942,
		"cg": -0.442844
	}
 * 
 * </code>
 * 
 * @author Horacio Hoyos Rodriguez
 *
 */
public class JsonBodiesOpenGL implements Bodies {

	private final Path data;
	
	public JsonBodiesOpenGL(Path data) {
		super();
		this.data = data;
	};

	@Override
	public List<NBody3DBody> createBodies() throws CreatingBodiesException {
		List<NBody3DBody> result = new ArrayList<>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(data.toFile());
			System.out.println(jsonNode.isArray());
			for (final JsonNode body : jsonNode) {
	    		result.add(new SimpleOpenGLBody(
					new StockVector3D(
							body.get("x").asDouble(),
							body.get("y").asDouble(),
							body.get("z").asDouble()),
					new StockVector3D(
							body.get("vx").asDouble(),
							body.get("vy").asDouble(),
							body.get("vz").asDouble()),
					body.get("m").asDouble(),
					(float) body.get("az").asDouble(),
					(float) body.get("az").asDouble(),
					(float) body.get("az").asDouble())
					);
			}
		} catch (IOException e) {
			throw new CreatingBodiesException("Error reading body information from JSON");
		}	
		return result;
	}

}
