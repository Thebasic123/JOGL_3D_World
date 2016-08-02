package World.spec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * Tree class, contains tree info and draws itself
 *
 * @author Fufu Hu
 */
public class Tree {

	private double[] myPos;
	private final double myHeight = 2;
	private final int slices = 32;
	private final double TRUNK_RADIUS = .1;
	private final double mySphereRadius = 0.4;
	private Map<String, List<double[]>> treeMesh;
	private static Texture leavesTexture;
	private static Texture trunkTexture;

	public Tree(double x, double y, double z) {
		myPos = new double[3];
		myPos[0] = x;
		myPos[1] = y;
		myPos[2] = z;
		treeMesh = new HashMap<String, List<double[]>>();
	}

	public double[] getPosition() {
		return myPos;
	}

	/**
	 * Initializes the tree's textures
	 * 
	 * @param gl
	 * @param altitude
	 */
	public static void init(GL2 gl) {
		String leavesTextureFileName = "leaves.jpg";
		String extension = "jpg";
		leavesTexture = new Texture(gl, leavesTextureFileName, extension);
		String trunkTextureFileName = "trunk.jpg";
		trunkTexture = new Texture(gl, trunkTextureFileName, extension);
	}

	/**
	 * Draws the tree
	 * 
	 * @param gl
	 */
	public void drawSelf(GL2 gl) {

		// Material properties
		float[] diffuseCoeff = { 0.5f, 0.5f, 0.5f, 1.0f };
		float[] specCoeff = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] ambientCoeff = { 0.0f, 0.0f, 0.0f, 1.0f };
		float phong = 10f;

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuseCoeff, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specCoeff, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambientCoeff, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, phong);

		// base and head heights
		double y1 = myPos[1] + myHeight;
		double y2 = myPos[1];

		// draw base and fill mesh arrays
		List<double[]> fanNormals = new ArrayList<double[]>(); // one array for
																// each normal
		List<double[]> fanVertices = new ArrayList<double[]>(); // one array for
																// each vertex
		List<double[]> fanFaces = new ArrayList<double[]>(); // one array for
																// each face, 4
																// numbers,
																// first number
																// is face
																// normal index,
																// next 3 are
																// vertices
		drawTriangleFan(gl, 1, y1, fanNormals, fanVertices, fanFaces);
		treeMesh.put("Top Vertices", fanVertices);
		treeMesh.put("Top Normals", fanNormals);
		treeMesh.put("Top Faces", fanFaces);

		// draw top and fill mesh arrays
		List<double[]> fanNormals2 = new ArrayList<double[]>(); // one array for
																// each normal
		List<double[]> fanVertices2 = new ArrayList<double[]>(); // one array
																	// for each
																	// vertex
		List<double[]> fanFaces2 = new ArrayList<double[]>(); // one array for
																// each face, 4
																// numbers,
																// first number
																// is face
																// normal index,
																// next 3 are
																// vertices
		drawTriangleFan(gl, -1, y2, fanNormals2, fanVertices2, fanFaces2);
		treeMesh.put("Bottom Vertices", fanVertices2);
		treeMesh.put("Bottom Normals", fanNormals2);
		treeMesh.put("Bottom Faces", fanFaces2);

		// draw trunk and leaves
		drawTrunk(gl, y1, y2);
		drawLeaves(gl, y1);

	}

	/**
	 * Draws the trunk using a gluCylinder
	 * 
	 * @param gl
	 * @param y1
	 * @param y2
	 */
	private void drawTrunk(GL2 gl, double y1, double y2) {
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Tree.trunkTexture.getTextureId());

		gl.glPushMatrix();
		gl.glTranslated(myPos[0], myPos[1], myPos[2]);
		gl.glRotated(90, 1, 0, 0);
		GLU glu = new GLU();
		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricTexture(quadric, true);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		glu.gluCylinder(quadric, .1, .1, y2 - y1, 32, 32);
		gl.glPopMatrix();

		gl.glDisable(GL2.GL_TEXTURE_2D);

	}

	/**
	 * Draw a triangle fan at the given y, and fill the give mesh arrays
	 * 
	 * @param gl
	 * @param normalY
	 * @param y
	 * @param normals
	 * @param vertices
	 * @param faces
	 */
	private void drawTriangleFan(GL2 gl, int normalY, double y,
			List<double[]> normals, List<double[]> vertices,
			List<double[]> faces) {
		double[] facesArray = new double[slices + 3];
		facesArray[0] = 0;
		gl.glBegin(GL2.GL_TRIANGLE_FAN);
		{
			gl.glNormal3d(0, normalY, 0);
			gl.glVertex3d(myPos[0], y, myPos[2]);
			vertices.add(new double[] { myPos[0], y, myPos[2] });
			facesArray[1] = 1;
			double angleStep = 2 * Math.PI / slices;
			for (int i = 0; i <= slices; i++) {
				double a0 = i * angleStep;

				// Calculate vertices for the quad
				double x0 = myPos[0] + (TRUNK_RADIUS * Math.cos(a0));
				double z0 = myPos[2] + (TRUNK_RADIUS * Math.sin(a0));

				gl.glVertex3d(x0, y, z0);
				vertices.add(new double[] { x0, y, z0 });
				facesArray[i + 2] = i + 2;
			}
		}
		gl.glEnd();

		normals.add(new double[] { 0, normalY, 0 });
		faces.add(facesArray);
	}

	/**
	 * Draw the leaves of the tree using a gluSphere
	 * 
	 * @param gl
	 * @param y1
	 */
	private void drawLeaves(GL2 gl, double y1) {
		gl.glEnable(GL2.GL_TEXTURE_2D);

		// material properties
		float[] diffuseCoeff = { 0.0f, 1.0f, 0.0f, 1.0f };
		float[] specCoeff = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] ambientCoeff = { 0.0f, 1.0f, 0.0f, 1.0f };
		float phong = 10f;

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuseCoeff, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specCoeff, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambientCoeff, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, phong);

		// draw the sphere
		GLU glu = new GLU();
		gl.glPushMatrix();
		gl.glTranslated(myPos[0], y1, myPos[2]);
		GLUquadric sphere = glu.gluNewQuadric();
		glu.gluQuadricTexture(sphere, true);
		glu.gluQuadricNormals(sphere, GLU.GLU_SMOOTH);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Tree.leavesTexture.getTextureId());
		glu.gluSphere(sphere, mySphereRadius, 50, 50);
		gl.glPopMatrix();

		gl.glDisable(GL2.GL_TEXTURE_2D);
	}
}
