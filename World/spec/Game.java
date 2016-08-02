package World.spec;

import java.io.File;
import java.io.FileNotFoundException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;

/**
 * Game class, initializes lighting and runs display loop
 *
 * @author  Fufu Hu
 */
@SuppressWarnings("serial")
public class Game extends JFrame implements GLEventListener {

	private Terrain myTerrain;
	private boolean day = true;
	private Sun mySun;

	public Game(Terrain terrain) {
		super("Assignment 2");
		myTerrain = terrain;
	}

	/**
	 * Run the game.
	 *
	 */
	public void run() {
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		GLJPanel panel = new GLJPanel();
		panel.addGLEventListener(this);
		panel.addKeyListener(myTerrain.getCamera());

		// Add an animator to call 'display' at 60fps
		FPSAnimator animator = new FPSAnimator(60);
		animator.add(panel);
		animator.start();

		getContentPane().add(panel);
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Load a level file and display it.
	 * 
	 * @param args
	 *            - The first argument is a level file in JSON format
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Terrain terrain = LevelIO.load(new File(args[0]));
		Game game = new Game(terrain);
		game.run();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		mySun = new Sun(myTerrain.getSunlight()); // create sun object

		// enable lighting
		initLighting(gl);
		gl.glEnable(GL2.GL_LIGHT1); // start in day time, with the torch off
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_DEPTH_TEST);

		gl.glEnable(GL2.GL_NORMALIZE);

		myTerrain.init(gl); // initializes the terrain textures
	}

	private void initLighting(GL2 gl) {
		// Uncomment for ambient lighting
		// float[] amb = { 0.1f, 0.2f, 0.3f, 1.0f };
		// gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, amb, 0);

		// Sun
		float[] amb1 = { 0.0f, 0.0f, 0.0f, 1.0f };
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, amb1, 0);

		float[] dif1 = { 1.0f, 1.0f, 0.0f, 1.0f };
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, dif1, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, dif1, 0);

		// Moon
		float[] amb2 = { 0.0f, 0.0f, 0.0f, 1.0f };
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT, amb2, 0);

		float[] dif2 = { 0.0f, 0.0f, 1.0f, 1.0f };
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, dif2, 0);
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPECULAR, dif2, 0);

		// Torch
		float[] amb3 = { 0.6f, 0.6f, 0.6f, 1.0f };
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_AMBIENT, amb3, 1);

		float[] dif3 = { 0.6f, 0.6f, 0.6f, 1.0f };
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_DIFFUSE, dif3, 1);
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_SPECULAR, dif3, 1);

		gl.glLightf(GL2.GL_LIGHT3, GL2.GL_SPOT_CUTOFF, 10);
		gl.glLightf(GL2.GL_LIGHT3, GL2.GL_SPOT_EXPONENT, 4);

		// move the sun to a starting position
		day = mySun.update(gl, day);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		myTerrain.drawWorld(gl); // draws the terrain, trees, road, and others
		day = mySun.update(gl, day); // update the sun's position
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		gl.glOrtho(-2, 2, -2, 2, 1, 20);
	}
}
