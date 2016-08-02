package World.spec;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

/**
 * Road class, holds the info for the road and draws itself
 *
 * @author Fufu Hu
 */
public class Road {

	private List<Double> myPoints;
	private double myWidth;
	private static Texture roadTexture;

	/**
	 * Create a new road starting at the specified point
	 */
	public Road(double width, double x0, double y0) {
		myWidth = width;
		myPoints = new ArrayList<Double>();
		myPoints.add(x0);
		myPoints.add(y0);
	}

	/**
	 * Create a new road with the specified spine
	 *
	 * @param width
	 * @param spine
	 */
	public Road(double width, double[] spine) {
		myWidth = width;
		myPoints = new ArrayList<Double>();
		for (int i = 0; i < spine.length; i++) {
			myPoints.add(spine[i]);
		}
	}

	/**
	 * The width of the road.
	 * 
	 * @return
	 */
	public double width() {
		return myWidth;
	}

	/**
	 * Add a new segment of road, beginning at the last point added and ending
	 * at (x3, y3). (x1, y1) and (x2, y2) are interpolated as bezier control
	 * points.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 */
	public void addSegment(double x1, double y1, double x2, double y2,
			double x3, double y3) {
		myPoints.add(x1);
		myPoints.add(y1);
		myPoints.add(x2);
		myPoints.add(y2);
		myPoints.add(x3);
		myPoints.add(y3);
	}

	/**
	 * Get the number of segments in the curve
	 * 
	 * @return
	 */
	public int size() {
		return myPoints.size() / 6;
	}

	public static void init(GL2 gl) {
		String roadTextureFileName = "road.jpg";
		String extension = "jpg";
		roadTexture = new Texture(gl, roadTextureFileName, extension);

	}

	public void drawSelf(GL2 gl, double altitude1) {

		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, roadTexture.getTextureId());

		double altitude = altitude1 + 0.02;
		gl.glColor4d(0.0, 0.0, 0.0, 1);
		int numberOfParts = 10;
		double tIncrement = 1.0 / (numberOfParts + 1);
		double t = 0;
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		for (int segment = 0; segment < size(); segment++) {
			for (int i = 0; i < numberOfParts; i++) {
				double[] p1 = point(t);
				double[] p2 = point(t + tIncrement);
				double dx = p2[0] - p1[0];
				double dy = p2[1] - p1[1];

				double[] normal = { dy, -dx };
				normal = MathUtil.normalizeVector(normal);
				normal = MathUtil.multiplyVector(normal, myWidth / 2);
				double x1 = p1[0] - normal[0];
				double y1 = p1[1] - normal[1];
				double x2 = p1[0] + normal[0];
				double y2 = p1[1] + normal[1];
				double x3 = p2[0] - normal[0];
				double y3 = p2[1] - normal[1];
				double x4 = p2[0] + normal[0];
				double y4 = p2[1] + normal[1];

				gl.glTexCoord2d(0, 0);
				gl.glVertex3d(x2, altitude, y2);
				gl.glTexCoord2d(0, 1);
				gl.glVertex3d(x1, altitude, y1);
				gl.glTexCoord2d(1, 0);
				gl.glVertex3d(x4, altitude, y4);
				gl.glTexCoord2d(1, 1);
				gl.glVertex3d(x3, altitude, y3);

				t = t + tIncrement;
			}
		}
		gl.glEnd();
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	// this vector just tangent of curve, still need to multiple by 1/2
	public double[] findVector(double t) {
		double[] vector = new double[2];

		int i = (int) Math.floor(t);
		t = t - i;

		i *= 6;

		double x0 = myPoints.get(i++);
		double y0 = myPoints.get(i++);
		double x1 = myPoints.get(i++);
		double y1 = myPoints.get(i++);
		double x2 = myPoints.get(i++);
		double y2 = myPoints.get(i++);
		double x3 = myPoints.get(i++);
		double y3 = myPoints.get(i++);
		vector[0] = 3 * (1 - t) * (1 - t) * (x1 - x0) + 6 * (1 - t) * t
				* (x2 - x1) + 3 * t * t * (x3 - x2);
		vector[1] = 3 * (1 - t) * (1 - t) * (y1 - y0) + 6 * (1 - t) * t
				* (y2 - y1) + 3 * t * t * (y3 - y2);
		return vector;
	}

	// return two points for both sides of road(x,z)
	public double[] thirdPoint(double[] point, double width) {
		double[] thirdPoint = new double[4];
		double length = width / 2;// draw half of width first
		length = Math.sqrt(length);
		thirdPoint[0] = point[0] + length;
		thirdPoint[1] = point[1] + length;
		thirdPoint[2] = point[0] - length;
		thirdPoint[3] = point[1] - length;
		return thirdPoint;
	}

	/**
	 * Get the specified control point.
	 * 
	 * @param i
	 * @return
	 */
	public double[] controlPoint(int i) {
		double[] p = new double[2];
		p[0] = myPoints.get(i * 2);
		p[1] = myPoints.get(i * 2 + 1);
		return p;
	}

	/**
	 * Get a point on the spine. The parameter t may vary from 0 to size().
	 * Points on the kth segment take have parameters in the range (k, k+1).
	 * 
	 * @param t
	 * @return
	 */
	public double[] point(double t) {
		int i = (int) Math.floor(t);
		t = t - i;

		i *= 6;

		double x0 = myPoints.get(i++);
		double y0 = myPoints.get(i++);
		double x1 = myPoints.get(i++);
		double y1 = myPoints.get(i++);
		double x2 = myPoints.get(i++);
		double y2 = myPoints.get(i++);
		double x3 = myPoints.get(i++);
		double y3 = myPoints.get(i++);

		double[] p = new double[2];

		p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
		p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;

		return p;
	}

	/**
	 * Calculate the Bezier coefficients
	 * 
	 * @param i
	 * @param t
	 * @return
	 */
	private double b(int i, double t) {

		switch (i) {

		case 0:
			return (1 - t) * (1 - t) * (1 - t);

		case 1:
			return 3 * (1 - t) * (1 - t) * t;

		case 2:
			return 3 * (1 - t) * t * t;

		case 3:
			return t * t * t;
		}

		// this should never happen
		throw new IllegalArgumentException("" + i);
	}

}
