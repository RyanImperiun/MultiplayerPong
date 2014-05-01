package Singleplayer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Player extends Rectangle {
	private static final long serialVersionUID = 1L;

	SinglePlayer sp;

	// Player details
	int x;
	int y;
	int width = 15;
	int height = 45;
	int speed = 3;

	// Booleans for movement
	public static boolean moveUp = false, moveDown = false;

	public Player(SinglePlayer sp) {
		this.sp = sp;

		// Initialize variables
		x = 10;
		y = sp.getHeight() / 2 - height / 2;
		setBounds(x, y, width, height);
	} // End constructor

	public void tick(SinglePlayer sp, double delta) {
		// Movement
		if (sp.IH.up.down && y > 0)
			y -= speed * delta;
		if (sp.IH.down.down && y + height < sp.getHeight())
			y += speed * delta;

		// If off-screen (No idea why)
		if (y < 0) {
			y = 0;
		}
		if (y + height > sp.getHeight()) {
			y = sp.getHeight() - height;
		}

		// Replace the rectangle around new coords
		setBounds(x, y, width, height);
	} // End tick

	public void render(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(x, y, width, height);
	} // End render
}