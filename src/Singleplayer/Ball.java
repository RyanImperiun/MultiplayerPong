package Singleplayer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Ball extends Rectangle {
	private static final long serialVersionUID = 1L;

	SinglePlayer sp;
	int speed = 3;
	int x, y, width, height, vx = speed, vy = speed;

	public Ball(SinglePlayer sp) {
		this.sp = sp;

		width = 8;
		height = 8;
		x = sp.getWidth() / 2 - width;
		y = sp.getHeight() / 2 - height;

		setBounds(x, y, width, height);
	} // End constructor

	public void tick(SinglePlayer sp, double delta) {
		movement(delta);
		setBounds(x, y, width, height);
	} // End tick

	private void movement(double delta) {
		if (y <= 0)
			vy = speed;
		if (y >= sp.getHeight() - height)
			vy = -speed;
		if (x <= 0) { // If hits left side (Player)
			vx = speed;
			sp.aiScore++;
		}
		if (x >= sp.getWidth() - width) { // If hits right side (AI)
			vx = -speed;
			sp.p1Score++;
		}

		x += vx * delta;
		y += vy * delta;

		if (this.intersects(sp.player)) {
			vx = speed;
		}
		if (this.intersects(sp.ai)) {
			vx = -speed;
		}
	} // End movement

	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillOval(x, y, width, height);
	} // End render
}