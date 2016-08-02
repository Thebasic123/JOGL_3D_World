package World.spec;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * LevelIO class, turns the given file into a terrain object, and vice versa
 *
 * @author John Gilhuly
 */
public class LevelIO {

	/**
	 * Load a terrain object from a JSON file
	 * 
	 * @param mapFile
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Terrain load(File mapFile) throws FileNotFoundException {

		Reader in = new FileReader(mapFile);
		JSONTokener jtk = new JSONTokener(in);
		JSONObject jsonTerrain = new JSONObject(jtk);

		int width = jsonTerrain.getInt("width");
		int depth = jsonTerrain.getInt("depth");
		Terrain terrain = new Terrain(width, depth);

		JSONArray jsonSun = jsonTerrain.getJSONArray("sunlight");
		float dx = (float) jsonSun.getDouble(0);
		float dy = (float) jsonSun.getDouble(1);
		float dz = (float) jsonSun.getDouble(2);
		terrain.setSunlightDir(dx, dy, dz);

		JSONArray jsonAltitude = jsonTerrain.getJSONArray("altitude");
		for (int i = 0; i < jsonAltitude.length(); i++) {
			int x = i % width;
			int z = i / width;

			double h = jsonAltitude.getDouble(i);
			terrain.setGridAltitude(x, z, h);
		}

		if (jsonTerrain.has("trees")) {
			JSONArray jsonTrees = jsonTerrain.getJSONArray("trees");
			for (int i = 0; i < jsonTrees.length(); i++) {
				JSONObject jsonTree = jsonTrees.getJSONObject(i);
				double x = jsonTree.getDouble("x");
				double z = jsonTree.getDouble("z");
				terrain.addTree(x, z);
			}
		}

		if (jsonTerrain.has("roads")) {
			JSONArray jsonRoads = jsonTerrain.getJSONArray("roads");
			for (int i = 0; i < jsonRoads.length(); i++) {
				JSONObject jsonRoad = jsonRoads.getJSONObject(i);
				double w = jsonRoad.getDouble("width");

				JSONArray jsonSpine = jsonRoad.getJSONArray("spine");
				double[] spine = new double[jsonSpine.length()];

				for (int j = 0; j < jsonSpine.length(); j++) {
					spine[j] = jsonSpine.getDouble(j);
				}
				terrain.addRoad(w, spine);
			}
		}

		if (jsonTerrain.has("others")) {
			JSONArray jsonOthers = jsonTerrain.getJSONArray("others");
			for (int i = 0; i < jsonOthers.length(); i++) {
				JSONObject jsonOther = jsonOthers.getJSONObject(i);
				double x = jsonOther.getDouble("x");
				double z = jsonOther.getDouble("z");
				terrain.addOther(x, z);
			}
		}
		return terrain;
	}

	/**
	 * Write Terrain to a JSON file
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void save(Terrain terrain, File file) throws IOException {
		JSONObject json = new JSONObject();

		Dimension size = terrain.size();
		json.put("width", size.width);
		json.put("depth", size.height);

		JSONArray jsonSun = new JSONArray();
		float[] sunlight = terrain.getSunlight();
		jsonSun.put(sunlight[0]);
		jsonSun.put(sunlight[1]);
		jsonSun.put(sunlight[2]);
		json.put("sunlight", jsonSun);

		JSONArray altitude = new JSONArray();
		for (int i = 0; i < size.width; i++) {
			for (int j = 0; j < size.height; j++) {
				altitude.put(terrain.getGridAltitude(i, j));
			}
		}
		json.put("altitude", altitude);

		JSONArray trees = new JSONArray();
		for (Tree t : terrain.trees()) {
			JSONObject j = new JSONObject();
			double[] position = t.getPosition();
			j.put("x", position[0]);
			j.put("z", position[2]);
			trees.put(j);
		}
		json.put("trees", trees);

		JSONArray roads = new JSONArray();
		for (Road r : terrain.roads()) {
			JSONObject j = new JSONObject();
			j.put("width", r.width());

			JSONArray spine = new JSONArray();
			int n = r.size();

			for (int i = 0; i <= n * 3; i++) {
				double[] p = r.controlPoint(i);
				spine.put(p[0]);
				spine.put(p[1]);
			}
			j.put("spine", spine);
			roads.put(j);
		}
		json.put("roads", roads);

		JSONArray others = new JSONArray();
		for (Other o : terrain.others()) {
			JSONObject j = new JSONObject();
			double[] position = o.getPosition();
			j.put("x", position[0]);
			j.put("z", position[2]);
			trees.put(j);
		}
		json.put("others", others);

		FileWriter out = new FileWriter(file);
		json.write(out);
		out.close();

	}

	/**
	 * For testing.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Terrain terrain = LevelIO.load(new File(args[0]));
		LevelIO.save(terrain, new File(args[1]));
	}

}
