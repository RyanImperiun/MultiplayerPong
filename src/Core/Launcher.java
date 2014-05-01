package Core;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Launcher {

	public static void main(String[] args) {
		
		// Set a UI look/feel to the JButton
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();

		} catch (InstantiationException e) {
			e.printStackTrace();

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		new MainMenu();
	}
}