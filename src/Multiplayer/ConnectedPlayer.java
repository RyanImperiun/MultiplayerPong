package Multiplayer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

public class ConnectedPlayer implements Runnable { // User
	DataOutputStream out;
	DataInputStream in;
	CreateServer cs;

	public ConnectedPlayer(DataOutputStream out, DataInputStream in, CreateServer cs) {
		this.out = out;
		this.in = in;
		this.cs = cs;
	} // End constructor

	public void run() {
		while (true) {
			try {
				cs.cYPos = in.readByte(); // Set the client yPos to whatever the client has sent (update)
			} catch (IOException e) {
				this.out = null;
				this.in = null;
				JOptionPane.showMessageDialog(null, "Disconnected from Client");
				System.exit(0);
			}
		} // End while loop
	} // End run
} // End ConnectedUser