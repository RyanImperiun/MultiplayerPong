package Singleplayer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class AI extends Rectangle {
	private static final long serialVersionUID = 1L;

	SinglePlayer sp;

	int x;
	int y;
	int width = 15;
	int height = 45;
	int speed = 2;

	public AI(SinglePlayer sp) {
		this.sp = sp;

		x = sp.getWidth() - width - 10;
		y = sp.getHeight() / 2 - height / 2;

		setBounds(x, y, width, height);
	} // End constructor

	public void tick(SinglePlayer sp, double delta) {
		movement(delta);
		setBounds(x, y, width, height);
	} // End tick

	private void movement(double delta) {
		if (sp.b.y + sp.b.height / 2 > y - height / 2 && y < sp.getHeight() - height) {
			y += speed * delta;
		} else if (sp.b.y + sp.b.height / 2 < y - height / 2 && y > 0) {
			y -= speed * delta;
		}
	} // End movement

	public void render(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(x, y, width, height);
	} // End render
}