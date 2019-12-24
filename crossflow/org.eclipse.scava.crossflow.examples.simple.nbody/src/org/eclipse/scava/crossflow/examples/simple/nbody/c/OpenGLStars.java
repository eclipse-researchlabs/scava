package org.eclipse.scava.crossflow.examples.simple.nbody.c;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glFrustum;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

public class OpenGLStars implements SimpleNBodyCS257 {

	private GLFWErrorCallback errorCallback;
    private long windowID;
	private final int N;
	private final double[] x;
	private final double[] y;
	private final double[] z;
	private final double[] m;
	private final double[] c;
	private final SimpleNBodyCS257 mathSimulation;

	public OpenGLStars(int N,
			double[] x,
			double[] y,
			double[] z,
			double[] ax,
			double[] ay,
			double[] az,
			double[] vx,
			double[] vy,
			double[] vz,
			double[] m,
			double[] c) {
		super();
		this.N = N;
		this.x = x;
		this.y = y;
		this.z = z;
		this.m = m;
		this.c = c;
		this.mathSimulation = new Stars(N, x, y, z, ax, ay, az, vx, vy, vz, m);
	}

	@Override
	public void init() {
		// Set the error handling code: all GLFW errors will be printed to the system error stream (just like println)
		GLFWErrorCallback.createPrint(System.err).set();
        glfwSetErrorCallback(errorCallback);

        // Initialize GLFW:
        if (!glfwInit())
            throw new IllegalStateException("GLFW initialization failed");

        // Configure the GLFW window
        windowID = glfwCreateWindow(
                640, 480,   // Width and height of the drawing canvas in pixels
                "Test",     // Title of the window
                MemoryUtil.NULL, // Monitor ID to use for fullscreen mode, or NULL to use windowed mode (LWJGL JavaDoc)
                MemoryUtil.NULL); // Window to share resources with, or NULL to not share resources (LWJGL JavaDoc)

        if (windowID == MemoryUtil.NULL)
            throw new IllegalStateException("GLFW window creation failed");

        glfwMakeContextCurrent(windowID); // Links the OpenGL context of the window to the current thread (GLFW_NO_CURRENT_CONTEXT error)
        glfwSwapInterval(1); // Enable VSync, which effective caps the frame-rate of the application to 60 frames-per-second
        glfwShowWindow(windowID);
		// If you don't add this line, you'll get the following exception:
        //  java.lang.IllegalStateException: There is no OpenGL context current in the current thread.
     	glfwMakeContextCurrent(windowID);
	    // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
     	GL.createCapabilities();
     	
	    // Set the clear color
     	glClearColor(0.5f, 0.5f, 0.5f, 1f);
     	setUpMatrices();
        
	}
	
	@Override	
	public void updateStars() {
		mathSimulation.updateStars();
		if (!glfwWindowShouldClose(windowID)) {
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			
			glMatrixMode(GL_MODELVIEW);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		    glLoadIdentity();
		    for (int i = 0; i < N; i++) {
		        glPushMatrix();
		        glTranslatef((float) x[i], (float) y[i], (float) z[i]);
		        glColor3f((float) c[3*i+0], (float) c[3*i+1], (float) c[3*i+2]);
		        
		        glBegin(GL_QUADS);
	            glVertex3f((float) -m[i]*0.01f, (float) -m[i]*0.01f, 0);
	            glVertex3f((float) -m[i]*0.01f, (float) m[i]*0.01f, 0);
	            glVertex3f((float) m[i]*0.01f, (float) m[i]*0.01f, 0);
	            glVertex3f((float) m[i]*0.01f, (float) -m[i]*0.01f, 0);
	            glEnd();
	                
		        glPopMatrix();
		    }
	        
		    // Swaps the front and back framebuffers, this is a very technical process which you don't necessarily
	        // need to understand. You can simply see this method as updating the window contents.
	        glfwSwapBuffers(windowID);
	        // Polls the user input. This is very important, because it prevents your application from becoming unresponsive
	        glfwPollEvents();
		}
	}


	@Override
	public void close() throws Exception {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(windowID);
		glfwDestroyWindow(windowID);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null); //.free();
	}
	
	private static void setUpMatrices() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(60, 640f / 480f, 0.3f, 100);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

	public double getLoopZeroTime() {
		return mathSimulation.getLoopZeroTime();
	}

	public double getLoopOneTime() {
		return mathSimulation.getLoopOneTime();
	}

	public double getLoopTwoTime() {
		return mathSimulation.getLoopTwoTime();
	}

	public double getLoopThreeTime() {
		return mathSimulation.getLoopThreeTime();
	}

	public double getTotalTime() {
		return mathSimulation.getTotalTime();
	}
	
	public static void gluPerspective(float fovy, float aspect, float near, float far) {
	    float bottom = -near * (float) Math.tan(fovy / 2);
	    float top = -bottom;
	    float left = aspect * bottom;
	    float right = -left;
	    glFrustum(left, right, bottom, top, near, far);
	}
	
}
