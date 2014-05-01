package Multiplayer;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CreateServer extends Canvas implements Runnable, KeyListener { // Create the server
	private static final long	serialVersionUID	= 1L;

	// Ball
	Ball						b;

	// Input and Output
	DataOutputStream			out;
	DataInputStream				in;

	// Socket information
	ServerSocket				serverSocket;
	Socket						socket;
	int							serverPort;

	// Frame
	JFrame						frame;
	int							width				= 600;
	int							height				= 400;
	public final Dimension		gameDim				= new Dimension(width, height);

	// Screen
	BufferedImage				image				= new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	// Game Info
	int							pWidth				= 15;
	int							pHeight				= 45;
	int							xPos;
	int							yPos;
	int							cXPos;
	int							cYPos;

	// Booleans for movement
	boolean						moveUp				= false;
	boolean						moveDown			= false;

	// Rectangles for ball collision
	Rectangle					serverRect;
	Rectangle					clientRect;

	// Scores
	int							serverScore			= 0;
	int							clientScore			= 0;

	// For run
	private int					ticks				= 0;
	private int					frames				= 0;
	public double				delta;

	// Used in the "run" method to limit the frame rate to the UPS
	boolean						limitFrameRate		= false;
	boolean						shouldRender;

	private void requestInformation() {
		try {
			serverPort = Integer.parseInt(JOptionPane.showInputDialog("What is the port that you wish to utilize? (Make sure to PortForward, if needed)"));
			JOptionPane.showMessageDialog(null, "You can get your IP at whatismyip.com (For incoming connections)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / 60D;

		long lastTimer = System.currentTimeMillis();
		delta = 0D;

		while (true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;

			// If you want to limit frame rate, shouldRender = false
			shouldRender = false;

			// If the time between ticks = 1, then various things (shouldRender = true, keeps FPS locked at UPS)
			while (delta >= 1) {
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}
			if (!limitFrameRate && ticks > 0)
				shouldRender = true;

			// If you should render, render!
			if (shouldRender) {
				frames++;
				render();
			}

			// Reset stuff every second for the new "FPS" and "UPS"
			if (System.currentTimeMillis() - lastTimer >= 1000) {
				lastTimer += 1000;
				frames = 0;
				ticks = 0;
			}
		}
	} // End run

	private void createFrame() {
		// Frame stuff
		setMinimumSize(gameDim);
		setMaximumSize(gameDim);
		setPreferredSize(gameDim);
		frame = new JFrame("Pong Multiplayer -Server-");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		frame.add(this, BorderLayout.CENTER);
		frame.pack();

		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// Players initializing
		xPos = 15;
		yPos = frame.getHeight() / 2 - pHeight;
		cXPos = frame.getWidth() - pWidth - 15;
		cYPos = frame.getHeight() / 2 - pHeight;

		serverRect = new Rectangle(xPos, yPos, pWidth, pHeight);
		clientRect = new Rectangle(cXPos, cYPos, pWidth, pHeight);

		b = new Ball(this);

		addKeyListener(this);

		requestFocus();

		Thread thread = new Thread(this);
		thread.start();
	}

	private void handShake() {
		try {
			serverSocket = new ServerSocket(serverPort);
			socket = serverSocket.accept();
			if (out == null || in == null) {
				out = new DataOutputStream(socket.getOutputStream());
				in = new DataInputStream(socket.getInputStream());
			}
			ConnectedPlayer cp = new ConnectedPlayer(out, in, this);
			Thread userThread = new Thread(cp);
			System.out.println("User has connected");
			userThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public CreateServer() {
		requestInformation();
		handShake();
		createFrame();
	}

	private void movement() {
		if (moveUp && yPos > 0) {
			yPos -= 3;
		}

		if (moveDown && yPos + pHeight < getHeight()) {
			yPos += 3;
		}
	}

	private void tick() {
		movement(); // Check for any movement

		// Move the ball
		b.tick();

		// Re-bind the collision rectangles to new positions after movement
		serverRect.setBounds(xPos, yPos, pWidth, pHeight);
		clientRect.setBounds(cXPos, cYPos, pWidth, pHeight);

		sendInformation();
	}

	private void sendInformation() {
		if (frames % 3 == 0) {
			try {
				out.writeByte(yPos);
				out.writeByte(serverScore);
				out.writeByte(clientScore);
				out.writeByte(b.x);
				out.writeByte(b.y);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void render() {
		BufferStrategy bs = getBufferStrategy();

		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

		g.setColor(Color.WHITE);
		g.fillRect(xPos, yPos, pWidth, pHeight);
		g.fillRect(cXPos, cYPos, pWidth, pHeight);
		b.render(g);

		// Draw scores
		g.drawString("P1 Score: " + serverScore, 40, 10);
		g.drawString("P2 Score: " + clientScore, getWidth() - 105, 10);

		g.dispose();
		bs.show();
	}

	// Key Input

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			moveUp = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			moveDown = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			moveUp = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			moveDown = false;
		}
	}

	public void keyTyped(KeyEvent e) {

	}
}