package ass2.spec;

/**
 * MathUtil class, contains math utility methods
 * 
 * @author John Gilhuly, Fufu Hu
 *
 */
public class MathUtil {

	/**
	 * Calculates the normal of a face given two of its vertices
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double[] calculateFaceNormal(double[] v1, double[] v2) {
		double[] faceNormal = new double[3];

		faceNormal[0] = v1[1] * v2[2] - v1[2] * v2[1];
		faceNormal[1] = v1[2] * v2[0] - v1[0] * v2[2];
		faceNormal[2] = v1[0] * v2[1] - v1[1] * v2[0];

		return faceNormal;
	}

	public static double[] normalizeVector(double[] vector) {
		double[] newVector = new double[2];
		double length = Math
				.sqrt(vector[0] * vector[0] + vector[1] * vector[1]);
		newVector[0] = vector[0] / length;
		newVector[1] = vector[1] / length;
		return newVector;
	}

	public static double[] multiplyVector(double[] vector, double length) {
		double[] newVector = new double[2];
		newVector[0] = vector[0] * length;
		newVector[1] = vector[1] * length;
		return newVector;
	}

}
