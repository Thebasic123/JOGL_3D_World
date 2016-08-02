package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * Camera Class, controls the camera position and movement, and holds the Avatar
 * class. Also controls the torch
 * 
 * @author John Gilhuly
 *
 */
public class Camera implements KeyListener {

	private Terrain myTerrain;
	private float fieldOfView = 60;
	private float aspectRatio = 1;
	private float near = 2;
	private float far = 8;
	private double[] myPosition;
	private double yAxisAngle = -0.8;
	private double lx = -0.7833269179639226, lz = -0.6216099577654319;
	private double teapotAngle = 0.0;
	private boolean showAvatar = true;
	private boolean torch = false;
	private Avatar myAvatar;

	public Camera(Terrain terrain) {
		myTerrain = terrain;
		myPosition = new double[] { 10, 3, 10 };
		lx = Math.sin(yAxisAngle);
		lz = -Math.cos(yAxisAngle);
		myAvatar = new Avatar(terrain, myPosition[1] / 1.6);
	}

	public double[] getPosition() {
		return myPosition;
	}

	public void setXPosition(double x) {
		myPosition[0] = x;
	}

	public void setYPosition(double y) { // altitude, set relative to terrain
		myPosition[1] = y;
	}

	public void setZPosition(double z) {
		myPosition[2] = z;
	}

	/**
	 * Set the camera view
	 * 
	 * @param gl
	 */
	public void setView(GL2 gl) {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU glu = new GLU();
		glu.gluPerspective(fieldOfView, aspectRatio, near, far);
		glu.gluLookAt(myPosition[0], myPosition[1], myPosition[2],
				myPosition[0] + lx, myPosition[1] * .9, myPosition[2] + lz, 0,
				1, 0);

		if (showAvatar)
			myAvatar.drawSelf(gl, myPosition[0] + (2 * lx), myPosition[2]
					+ (2 * lz), teapotAngle);

		updateTorch(gl);
	}

	/**
	 * Updates the torches position and on/off
	 * 
	 * @param gl
	 */
	private void updateTorch(GL2 gl) {
		float[] position = { (float) myPosition[0], (float) myPosition[1] + 2,
				(float) myPosition[2], 1 };
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_POSITION, position, 0);

		float[] dir = { (float) lx, -0.6f, (float) lz };
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_SPOT_DIRECTION, dir, 0);

		if (torch && !gl.glIsEnabled(GL2.GL_LIGHT3)) { // Turn on torch
			gl.glEnable(GL2.GL_LIGHT3);
		} else if (!torch && gl.glIsEnabled(GL2.GL_LIGHT3)) { // Turn off torch
			gl.glDisable(GL2.GL_LIGHT3);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int k = e.getKeyCode();

		if (k == KeyEvent.VK_UP || k == KeyEvent.VK_W) { // move forward
			myPosition[0] += lx;
			myPosition[2] += lz;
			calculateAltitude();
		} else if (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) { // move
			// backward
			myPosition[0] -= lx;
			myPosition[2] -= lz;
			calculateAltitude();
		} else if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) { // turn right
			yAxisAngle += 0.1f;
			lx = Math.sin(yAxisAngle);
			lz = -Math.cos(yAxisAngle);
			teapotAngle -= 5.7;
		} else if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) { // turn left
			yAxisAngle -= 0.1f;
			lx = Math.sin(yAxisAngle);
			lz = -Math.cos(yAxisAngle);
			teapotAngle += 5.7;
		} else if (k == KeyEvent.VK_SPACE) { // toggle show avatar
			showAvatar = !showAvatar;
		} else if (k == KeyEvent.VK_T) { // toggle torch
			torch = !torch;
		}
	}

	/**
	 * Calculates the camera y position, with exception handling
	 */
	public void calculateAltitude() {
		try {
			myPosition[1] = myTerrain.altitude(myPosition[0] + (2 * lx),
					myPosition[2] + (2 * lz)) + 3;
		} catch (Exception e) {
			myPosition[1] = 3;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}
}
