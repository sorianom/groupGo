package com.ideanest.vegos;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Scanner;

import com.ideanest.vegos.game.Color;
import com.ideanest.vegos.gtp.*;
import java.awt.Component;
import javax.swing.JOptionPane;



/**
 * A random player that can connect to a game using the GTP (v2) protocol.  It doesn't
 * keep track of board state, and just randomly generates move based on the board size.
 * It's likely many of the generated moves will be illegal, but hopefully the server will
 * deal with that somehow.  Only the basic GTP commands are implemented.
 * 
 * Note that the public method names must match the GTP protocol commands <em>precisely</em>
 * in spelling, case and argument types.  You can add more methods here, to implement the optional
 * parts of the GTP protocol, and also to add your own commands.  Many GTP GUIs allow you to enter
 * arbitrary commands to converse with your engine.
 * 
 * Note that the GTPClient itself implements the following basic commands:
 *   protocol_version known_command list_commands help quit
 * 
 * @author Piotr Kaminski
 */
public class RandomGTPEngine {
	
	private int boardSize;
	private final Random rnd = new Random();
		
	public String name() {return "Random GTP Engine";}
	public String version() {return "1.0";}	
	
	public void boardsize(int size) {
		this.boardSize = size;
	}
	
	public void clear_board() {
		// do nothing, we don't track the board
	}
	
	public void komi(float komi) {
		// ignore komi setting, like we're gonna win anyway!
	}
	
	public void play(Move move) {
		// ignore any moves played, we don't keep track of the board
	}
	
	public void undo()
	{
		// nothing to undo...
	}
	
	public Vertex genmove(Color color) {
		//Scanner reader = new Scanner(System.in);
		//System.out.println("X coordinate: ");
		//get user input for a
		//int x=reader.nextInt();				
		//int y = reader.nextInt();
		int x = 1;
		int y = 1;
		String inputValue = JOptionPane.showInputDialog("X coordinate: "); 
		x = Integer.parseInt(inputValue);
		inputValue = JOptionPane.showInputDialog("Y coordinate: "); 
		y = Integer.parseInt(inputValue);		
		return new Vertex(x, y);
	}
	
	/**
	 * Start the engine as a GTP client on standard in/out streams.  You could attach it to sockets
	 * instead, if playing over a network, etc.
	 */
	public static void main(String[] args) {
		GTPClient client = new GTPClient(new InputStreamReader(System.in), new OutputStreamWriter(System.out));
		client.registerController(new RandomGTPEngine());
		client.run();	// could do client.start() instead to start it on another thread
	}
}
