package Multiplayer;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ClientPlayer extends Canvas implements Runnable, KeyListener { // Client
	private static final long	serialVersionUID	= 1L;

	// DataStreams
	DataOutputStream			out;
	DataInputStream				in;

	// Connection info
	String						serverIP;
	private int					serverPort;
	Socket						socket;

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
	private int					xPos;
	private int					yPos;
	private int					sXPos;
	private int					sYPos;

	// Booleans for movement
	boolean						moveUp				= false;
	boolean						moveDown			= false;

	// Scores
	private int					serverScore			= 0;
	private int					clientScore			= 0;

	// Ball info
	private int					bX;
	private int					bY;
	int							bSize				= 8;

	// For run
	private int					ticks				= 0;
	private int					frames				= 0;
	private int					FPS					= 0;
	private int					UPS					= 0;
	public double				delta;

	// Used in the "run" method to limit the frame rate to the UPS
	boolean						limitFrameRate		= false;
	boolean						shouldRender;

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
				setFPS(frames);
				setUPS(ticks);
				frames = 0;
				ticks = 0;
			}
		}
	} // End run

	private void requestInformation() {
		serverIP = JOptionPane.showInputDialog("What is the IP of the server you are connecting to? (Will also accept 'localhost')");
		serverPort = Integer.parseInt(JOptionPane.showInputDialog("What is the port you are connecting through?"));
		if (serverIP == "localhost")
			serverIP = "192.168.0.1";
	}

	private void createFrame() {
		// Frame stuff
		setMinimumSize(gameDim);
		setMaximumSize(gameDim);
		setPreferredSize(gameDim);
		frame = new JFrame("Pong Multiplayer -Client-");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		frame.add(this, BorderLayout.CENTER);
		frame.pack();

		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// Players initializing
		xPos = frame.getWidth() - pWidth - 15;
		yPos = frame.getHeight() / 2 - pHeight;
		sXPos = 15;
		setsYPos(frame.getHeight() / 2 - pHeight);

		addKeyListener(this);

		requestFocus();

		Thread thread = new Thread(this);
		thread.start();
	}

	private void handShake() {
		try {

			socket = new Socket(serverIP, serverPort);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());

			try {
				out.writeUTF("This goes to 'ConnectedPlayer'");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			Input serverIn = new Input(in, this);
			Thread inputThread = new Thread(serverIn);
			inputThread.start();
		} catch (IOException e) {
			System.out.println("Could not connect to the Server!"); // If it couldn't utilize the incoming data stream
		}
	}

	public ClientPlayer() {
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
		movement();
		if (out != null && frames % 3 == 0) {
			try {
				out.writeByte(yPos); // Send new coordinates
			} catch (IOException e) {
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

		g.setColor(Color.WHITE); // Set the color of players to white
		g.fillRect(xPos, yPos, pWidth, pHeight);
		g.fillRect(sXPos, getsYPos(), pWidth, pHeight);

		// Render ball
		g.fillOval(getbX(), getbY(), bSize, bSize);

		// Draw scores
		g.drawString("P1 Score: " + getServerScore(), 40, 10);
		g.drawString("P2 Score: " + getClientScore(), getWidth() - 105, 10);

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

	// Getters and Setters

	public int getUPS() {
		return UPS;
	}

	public void setUPS(int uPS) {
		UPS = uPS;
	}

	public int getFPS() {
		return FPS;
	}

	public void setFPS(int fPS) {
		FPS = fPS;
	}

	public int getbX() {
		return bX;
	}

	public void setbX(int bX) {
		this.bX = bX;
	}

	public int getbY() {
		return bY;
	}

	public void setbY(int bY) {
		this.bY = bY;
	}

	public int getServerScore() {
		return serverScore;
	}

	public void setServerScore(int serverScore) {
		this.serverScore = serverScore;
	}

	public int getClientScore() {
		return clientScore;
	}

	public void setClientScore(int clientScore) {
		this.clientScore = clientScore;
	}

	public int getsYPos() {
		return sYPos;
	}

	public void setsYPos(int i) {
		this.sYPos = i;
	}
}

class Input implements Runnable {
	DataInputStream	in;
	ClientPlayer	cp;

	public Input(DataInputStream in, ClientPlayer cp) {
		this.in = in;
		this.cp = cp;
	}

	public void run() {
		while (true) {
			try {
				cp.setsYPos(in.readByte());
				cp.setServerScore(in.readByte());
				cp.setClientScore(in.readByte());
				cp.setbX(in.readByte());
				cp.setbY(in.readByte());

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Disconnected from Server!"); // If it is not receiving any info from server
				System.exit(0);
			}
		}
	}
}