package World.spec;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

/**
 * Other class, contains info about an other and draws itself using VBOs and
 * shaders
 * 
 * Adapted from class example: Triangle VBO Shader
 *
 * @author Fufu HU
 */

public class Other {

	private float positions[];
	private float colors[];
	private short indexes[];

	private FloatBuffer posData;
	private FloatBuffer colorData;
	private ShortBuffer indexData;

	private int bufferIds[] = new int[2];

	private static final String VERTEX_SHADER = "Shaders/AttributeVertex.glsl";
	private static final String FRAGMENT_SHADER = "Shaders/AttributeFragment.glsl";

	private static final int NUM_VERTICES = 4;

	private int shaderprogram;

	public Other(double x, double y, double z) {
		fillArrays((float) x, (float) y, (float) z);
	}

	public double[] getPosition() {
		return new double[] { positions[0], positions[2] };
	}

	/**
	 * Fill position, index, and color arrays
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	private void fillArrays(float x, float y, float z) {
		positions = new float[] { x, y, z, x - 1, y, z - 1, x - 1, y + 2,
				z - 1, x, y + 2, z };

		indexes = new short[] { 0, 1, 2, 3 };

		colors = new float[] { 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0 };

		// See RobotInfo Class
		// positions = RobotInfo.fillPositions(x, y, z);
		// indexes = RobotInfo.fillIndexes();
		// colors = RobotInfo.fillColors();

	}

	/**
	 * Initialize the other, VBO setup, and shader
	 * 
	 * @param gl
	 */
	public void init(GL2 gl) {

		posData = Buffers.newDirectFloatBuffer(positions);
		colorData = Buffers.newDirectFloatBuffer(colors);
		indexData = Buffers.newDirectShortBuffer(indexes);

		gl.glGenBuffers(2, bufferIds, 0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);

		gl.glBufferData(GL2.GL_ARRAY_BUFFER, positions.length * Float.BYTES
				+ colors.length * Float.BYTES, null, GL2.GL_STATIC_DRAW);

		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, positions.length
				* Float.BYTES, posData);

		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, positions.length * Float.BYTES,
				colors.length * Float.BYTES, colorData);

		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);

		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indexes.length
				* Short.BYTES, indexData, GL2.GL_STATIC_DRAW);

		try {
			shaderprogram = Shader.initShaders(gl, VERTEX_SHADER,
					FRAGMENT_SHADER);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * Draws self using the VBO data and shader
	 * 
	 * @param gl
	 */
	public void drawSelf(GL2 gl) {

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glUseProgram(shaderprogram);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);

		int vertexColLoc = gl.glGetAttribLocation(shaderprogram, "vertexCol");
		int vertexPosLoc = gl.glGetAttribLocation(shaderprogram, "vertexPos");

		gl.glEnableVertexAttribArray(vertexPosLoc);
		gl.glEnableVertexAttribArray(vertexColLoc);
		gl.glVertexAttribPointer(vertexPosLoc, 3, GL.GL_FLOAT, false, 0, 0);
		gl.glVertexAttribPointer(vertexColLoc, 3, GL.GL_FLOAT, false, 0,
				positions.length * Float.BYTES);

		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);

		gl.glDrawElements(GL2.GL_QUADS, NUM_VERTICES, GL2.GL_UNSIGNED_SHORT, 0);

		gl.glUseProgram(0);

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
}
