package World.spec;

import javax.media.opengl.GL2;

/**
 * Sun class, stores info about the Sun and Moon, and updates their position
 * 
 * @author Fufu Hu
 *
 */
public class Sun {
	private float[] myDir = { 0, -1, 0, 0 };
	private float[] myColor = { 1.0f, 1.0f, 0.0f };
	private float myRadius = 0.25f;

	private boolean rising = true;

	public Sun(float[] sunlightDir) {
		myDir[0] = sunlightDir[0];
		myDir[1] = sunlightDir[1];
		myDir[2] = sunlightDir[2];
		myDir[3] = 0;
	}

	public float[] getMyDir() {
		return myDir;
	}

	public void setMyDir(float[] myDir) {
		this.myDir = myDir;
	}

	public float[] getMyColor() {
		return myColor;
	}

	public void setMyColor(float[] myColor) {
		this.myColor = myColor;
	}

	public float getMyRadius() {
		return myRadius;
	}

	public void setMyRadius(float myRadius) {
		this.myRadius = myRadius;
	}

	/**
	 * Update the position of the light
	 * 
	 * @param gl
	 * @param day
	 * @return
	 */
	public boolean update(GL2 gl, boolean day) {
		if (myDir[1] > 1) { // orb has reach below ground level, change to day
							// or night
			myDir[0] = -1.99f;
			myDir[1] = -1f;

			myColor[1] = 1.0f;

			rising = true;
			day = !day;

			if (day) {
				if (gl.glIsEnabled(GL2.GL_LIGHT2))
					gl.glDisable(GL2.GL_LIGHT2);

				if (!gl.glIsEnabled(GL2.GL_LIGHT1))
					gl.glEnable(GL2.GL_LIGHT1);
			} else {
				if (gl.glIsEnabled(GL2.GL_LIGHT1))
					gl.glDisable(GL2.GL_LIGHT1);

				if (!gl.glIsEnabled(GL2.GL_LIGHT2))
					gl.glEnable(GL2.GL_LIGHT2);
			}
		}

		if (rising) { // orb is rising
			myDir[0] += 0.01f;

			if (myDir[0] > 0) { // orb has reached peak
				rising = false;
			}
		} else { // orb is setting
			myDir[0] += 0.01f;
			myDir[1] += 0.007f;
		}

		if (day) { // Sun
			gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, myDir, 0);

			myColor[1] -= 0.001f; // update sun color
			gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, myColor, 0);
			gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, myColor, 0);
		} else { // Moon
			gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, myDir, 0);
		}

		return day;
	}
}
