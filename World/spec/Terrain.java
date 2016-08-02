package World.spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

/**
 * Terrain class, contains trees, road, and others. Draws each. Also contains
 * camera
 *
 * @author John Gilhuly, Fufu Hu
 */
public class Terrain {

	private Camera myCamera;
	private Dimension mySize;
	private double[][] myAltitude;
	private List<Tree> myTrees;
	private List<Road> myRoads;
	private float[] mySunlight;
	private List<Other> myOthers;
	private Texture terrainTexture;
	private Map<String, List<double[]>> terrainMesh;

	/**
	 * Create a new terrain
	 *
	 * @param width
	 *            The number of vertices in the x-direction
	 * @param depth
	 *            The number of vertices in the z-direction
	 */
	public Terrain(int width, int depth) {
		myCamera = new Camera(this);
		mySize = new Dimension(width, depth);
		myAltitude = new double[width][depth];
		myTrees = new ArrayList<Tree>();
		myRoads = new ArrayList<Road>();
		mySunlight = new float[3];
		myOthers = new ArrayList<Other>();
		terrainMesh = new HashMap<String, List<double[]>>();
	}

	public Terrain(Dimension size) {
		this(size.width, size.height);
	}

	public Dimension size() {
		return mySize;
	}

	public List<Tree> trees() {
		return myTrees;
	}

	public List<Road> roads() {
		return myRoads;
	}

	public float[] getSunlight() {
		return mySunlight;
	}

	public List<Other> others() {
		return myOthers;
	}

	public Camera getCamera() {
		return myCamera;
	}

	/**
	 * Set the sunlight direction.
	 * 
	 * Note: the sun should be treated as a directional light, without a
	 * position
	 * 
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void setSunlightDir(float dx, float dy, float dz) {
		mySunlight[0] = dx;
		mySunlight[1] = dy;
		mySunlight[2] = dz;
	}

	/**
	 * Resize the terrain, copying any old altitudes.
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		mySize = new Dimension(width, height);
		double[][] oldAlt = myAltitude;
		myAltitude = new double[width][height];

		for (int i = 0; i < width && i < oldAlt.length; i++) {
			for (int j = 0; j < height && j < oldAlt[i].length; j++) {
				myAltitude[i][j] = oldAlt[i][j];
			}
		}
	}

	/**
	 * Get the altitude at a grid point
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public double getGridAltitude(int x, int z) {
		return myAltitude[x][z];
	}

	/**
	 * Set the altitude at a grid point
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public void setGridAltitude(int x, int z, double h) {
		myAltitude[x][z] = h;
	}

	/**
	 * Get the altitude at an arbitrary point. Non-integer points should be
	 * interpolated from neighbouring grid points
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public double altitude(double x, double z) {
		double altitude = 0;

		if (x % 1 == 0 && z % 1 == 0) {
			altitude = myAltitude[(int) x][(int) z];
		} else {
			int x0 = (int) Math.floor(x);
			double y0 = myAltitude[x0][(int) Math.floor(z)];
			int x1 = (int) Math.ceil(x);
			double y1 = myAltitude[x0][(int) Math.ceil(z)];
			altitude = y0 + (y1 - y0) * ((x - x0) / (x1 - x0));
		}
		return altitude;
	}

	/**
	 * Add a tree at the specified (x,z) point. The tree's y coordinate is
	 * calculated from the altitude of the terrain at that point.
	 * 
	 * @param x
	 * @param z
	 */
	public void addTree(double x, double z) {
		double y = altitude(x, z);
		Tree tree = new Tree(x, y, z);
		myTrees.add(tree);
	}

	/**
	 * Add a road.
	 * 
	 * @param x
	 * @param z
	 */
	public void addRoad(double width, double[] spine) {
		Road road = new Road(width, spine);
		myRoads.add(road);
	}

	/**
	 * Add an other
	 * 
	 * @param x
	 * @param z
	 */
	public void addOther(double x, double z) {
		Other other = new Other(x, altitude(x, z), z);
		myOthers.add(other);
	}

	/**
	 * Initialize Terrain and Others
	 * 
	 * @param gl
	 */
	public void init(GL2 gl) {
		String textureFileName = "grass.jpg";
		String extension = "jpg";
		terrainTexture = new Texture(gl, textureFileName, extension);
		Tree.init(gl);
		Road.init(gl);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE,
				GL2.GL_MODULATE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,
				GL2.GL_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
				GL2.GL_REPEAT);

		for (Other o : myOthers)
			o.init(gl);
	}

	/**
	 * Draw each piece of the world
	 * 
	 * @param gl
	 */
	public void drawWorld(GL2 gl) {
		myCamera.setView(gl);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		drawTerrain(gl);
		drawTrees(gl);
		drawRoads(gl);
		drawOthers(gl);
	}

	/**
	 * Draws the terrain
	 * 
	 * @param gl
	 */
	private void drawTerrain(GL2 gl) {

		// Material properties for terrain
		float[] diffuseCoeff = { 0.1f, 1.0f, 0.8f, 1.0f };
		float[] specCoeff = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] ambientCoeff = { 0.1f, 1.0f, 0.8f, 1.0f };
		float phong = 10f;

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuseCoeff, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specCoeff, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambientCoeff, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, phong);

		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, terrainTexture.getTextureId());

		// mesh arrays
		List<double[]> normals = new ArrayList<double[]>(); // one array for
		// each normal
		List<double[]> vertices = new ArrayList<double[]>(); // one array for
		// each vertex
		List<double[]> faces = new ArrayList<double[]>(); // one array for each
															// face, 4 numbers,
															// first number is
															// face normal
															// index, next 3 are
															// vertices
		int normalsCounter = 0;
		int vertexCounter = 0;

		// draw the terrain and fill mesh arrays
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		gl.glBegin(GL2.GL_TRIANGLES);
		{
			for (int row = 0; row < myAltitude.length - 1; row++) {
				for (int col = 0; col < myAltitude[row].length - 1; col++) {
					double[] normal = MathUtil
							.calculateFaceNormal(
									new double[] { row, altitude(row, col), col },
									new double[] { row, altitude(row, col + 1),
											col + 1 });
					gl.glNormal3d(normal[0], normal[1], normal[2]);
					normals.add(normal);

					gl.glTexCoord2d(0, 0);
					gl.glVertex3d(row, altitude(row, col), col);
					vertices.add(new double[] { row, altitude(row, col), col });
					gl.glTexCoord2d(0, 1);
					gl.glVertex3d(row, altitude(row, col + 1), col + 1);
					vertices.add(new double[] { row, altitude(row, col + 1),
							col + 1 });
					gl.glTexCoord2d(1, 1);
					gl.glVertex3d(row + 1, altitude(row + 1, col), col);
					vertices.add(new double[] { row + 1,
							altitude(row + 1, col), col });

					faces.add(new double[] { normalsCounter, vertexCounter,
							vertexCounter + 1, vertexCounter + 2 });
					normalsCounter++;
					vertexCounter += 3;

					double[] normal2 = MathUtil
							.calculateFaceNormal(
									new double[] { row, altitude(row, col), col },
									new double[] { row, altitude(row, col + 1),
											col + 1 });
					gl.glNormal3d(normal2[0], normal2[1], normal2[2]);
					normals.add(normal2);

					gl.glTexCoord2d(0, 0);
					gl.glVertex3d(row + 1, altitude(row + 1, col), col);
					vertices.add(new double[] { row + 1,
							altitude(row + 1, col), col });
					gl.glTexCoord2d(0, 1);
					gl.glVertex3d(row, altitude(row, col + 1), col + 1);
					vertices.add(new double[] { row, altitude(row, col + 1),
							col + 1 });
					gl.glTexCoord2d(1, 1);
					gl.glVertex3d(row + 1, altitude(row + 1, col + 1), col + 1);
					vertices.add(new double[] { row + 1,
							altitude(row + 1, col + 1), col + 1 });

					faces.add(new double[] { normalsCounter, vertexCounter,
							vertexCounter + 1, vertexCounter + 2 });
					normalsCounter++;
					vertexCounter += 3;
				}
			}

		}
		gl.glEnd();

		terrainMesh.put("Vertices", vertices);
		terrainMesh.put("Normals", normals);
		terrainMesh.put("Faces", faces);
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	/**
	 * Tells each road to draw itself
	 * 
	 * @param gl
	 */
	private void drawRoads(GL2 gl) {
		for (Road road : myRoads) {
			road.drawSelf(gl, altitude(road.point(0)[0], road.point(0)[1]));
		}
	}

	/**
	 * Tells each tree to draw itself
	 * 
	 * @param gl
	 */
	private void drawTrees(GL2 gl) {
		for (Tree tree : myTrees) {
			tree.drawSelf(gl);
		}
	}

	/**
	 * Tells each other to draw itself
	 * 
	 * @param gl
	 */
	private void drawOthers(GL2 gl) {
		for (Other o : myOthers) {
			o.drawSelf(gl);
		}
	}
}
