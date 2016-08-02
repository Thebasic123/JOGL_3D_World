package ass2.spec;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Avatar class, holds info about the avatar, and draws itself
 * 
 * @author John Gilhuly
 *
 */
public class Avatar {

	private Terrain myTerrain;

	private double myY;
	private double dy = -.1;
	private final double MAX_Y = 1.8;

	public Avatar(Terrain terrain, double startingY) {
		myTerrain = terrain;
		myY = startingY;
	}

	/**
	 * Draws itself as a bouncing ball
	 * 
	 * @param gl
	 * @param x
	 * @param z
	 * @param angle
	 */
	public void drawSelf(GL2 gl, double x, double z, double angle) {
		double altitude;
		try {
			altitude = myTerrain.altitude(x, z);
		} catch (Exception e) {
			altitude = MAX_Y - .6;
		}

		// change direction if needed
		if (myY + dy < altitude + .5) {
			dy *= -1;
			myY = altitude + .5;
		} else if (myY + dy > altitude + MAX_Y) {
			dy *= -1;
			myY = altitude + MAX_Y;
		}

		// change height (bouncing effect)
		if (dy < 0)
			myY += dy / (myY);
		else
			myY += dy / (myY);

		// Material properties
		float[] diffuseCoeff = { 0.6f, 0.2f, 0.2f, 1.0f };
		float[] specCoeff = { 0.95f, 0.8f, 0.6f, 0.2f };
		float[] ambientCoeff = { 0.7f, 0.3f, 0.3f, 1.0f };
		float phong = 100f;

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuseCoeff, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specCoeff, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambientCoeff, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, phong);

		// draw sphere
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glColor3f(1, 1, 1);
		gl.glTranslated(x, myY, z);
		gl.glRotated(angle, 0, 1, 0);
		GLUT glut = new GLUT();
		glut.glutSolidSphere(.25, 20, 20);
		gl.glPopMatrix();
	}

}
