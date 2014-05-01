package Singleplayer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class InputHandler implements KeyListener {

	public float mouseX, mouseY;
	SinglePlayer sp;

	public class Key {

		public boolean down, clicked;
		private int absorbs, presses;

		public void tick() {
			if (absorbs < presses) {
				absorbs++;
				clicked = true;
			} else {
				clicked = false;
			}
		}
		
		public void toggle(boolean pressed) {
			if (pressed != down) {
				down = pressed;
			}

			if (pressed) {
				presses++;
			}
		}

		public Key() {
			keys.add(this);
		}

	}

	public List<Key> keys = new ArrayList<Key>();

	// Create a new Key() object for every key
	public Key up = new Key();
	public Key down = new Key();

	public Key exit = new Key();

	public InputHandler(SinglePlayer sp) {
		this.sp = sp;
		sp.addKeyListener(this);
	}

	// Just a tick
	public void tick() {
		for (int i = 0; i < keys.size(); i++) {
			keys.get(i).tick();
		}
	}

	// Releases all keys
	public void releaseAll() {
		for (int i = 0; i < keys.size(); i++) {
			keys.get(i).down = false;
		}
	}

	public void toggle(KeyEvent e, boolean pressed) {
		// Player 1 controls
		if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP)
			up.toggle(pressed);
		if (e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN)
			down.toggle(pressed);

		// Escape shortcut
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			System.exit(0);

	}

	public void keyPressed(KeyEvent e) {
		toggle(e, true);
	}

	public void keyReleased(KeyEvent e) {
		toggle(e, false);
	}

	public void keyTyped(KeyEvent e) {

	}
}