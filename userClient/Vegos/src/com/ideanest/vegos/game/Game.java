package com.ideanest.vegos.game;

import com.ideanest.util.UnexpectedException;
import com.ideanest.vegos.gtp.*;

/**
 * A game of Go.
 * @author Piotr Kaminski
 */
public abstract class Game implements Cloneable {
	
	public static final int MOVE_PASS = -1;
	public static final int MOVE_RESIGN = -2;
	
	protected float komi;
	protected int handicap;
	
	protected Color nextToPlay;
	protected Board board;
	protected int numMoves;
	
	private int lastMove;
	
	public boolean equals(Object o) {
		Game that = (Game) o;
		return
			this.komi == that.komi &&
			this.handicap == that.handicap &&
			this.nextToPlay == that.nextToPlay &&
			this.board.equals(that.board) &&
			this.numMoves == that.numMoves &&
			this.lastMove == that.lastMove;
	}
	
	public int hashCode() {
		return board.hashCode() ^ numMoves ^ handicap ^ getClass().hashCode();
	}
	
	/**
	 * Used by many methods to efficiently store lists of board positions.  Allocated only once,
	 * to the number of points on the board.
	 */
	protected Board.PositionList temp1, temp2;
		
	public Game(int sideSize) {
		board = new Board(sideSize);
		temp1 = board.createPositionList();
		temp2 = board.createPositionList();
		nextToPlay = Color.BLACK;
	}
	
	public abstract String getName();
	
	public String toString() {
		return getName() + " " + getSideSize() + "x" + getSideSize() + " handicap=" + handicap + " komi=" + komi;
	}
		
	public Object clone() throws CloneNotSupportedException {
		Game that = (Game) super.clone();
		that.board = this.board.duplicate();
		that.temp1 = that.board.createPositionList();
		that.temp2 = that.board.createPositionList();
		return that;
	}
	
	public Game duplicate() {
		try {
			return (Game) clone();
		} catch (CloneNotSupportedException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public Board.Grid getGrid() {return board.getGrid();}
	
	public void setKomi(float komi) {
		if (numMoves > 0) throw new IllegalStateException("game already started");
		this.komi = komi;
	}

	public void setFixedHandicap(int handicap) {
		if (numMoves > 0) throw new IllegalStateException("game already started");
		if (this.handicap > 0) board.clear();
		this.handicap = handicap;
		placeFixedHandicapStones();
	}
	
	/**
	 * Place fixed handicap stones on the board, and change to White's turn.
	 * @throws IllegalArgumentException if the combination of handicap and board size is illegal
	 */
	protected void placeFixedHandicapStones() {
		if (handicap == 0) return;
		final int ss = board.getSideSize();
		if (
			handicap < 0
			|| handicap == 1
			|| handicap > (ss % 2 == 0 ? 4 : 9)
			|| ss < 7
			|| ss == 7 && handicap > 4
		) throw new IllegalArgumentException("illegal handicap " + handicap);
		
		final int ho = ss < 13 ? 2 : 3;  // handicap offset
		final int s = 1 + ho, b = ss - ho, m = (ss+1)/2;
		
		for (int i=1; i <= handicap; i++) {
			switch(i) {
				case 1:  board.setPoint(getGrid().at(s, s), Color.BLACK); break;
				case 2:  board.setPoint(getGrid().at(b, b), Color.BLACK); break;
				case 3:  board.setPoint(getGrid().at(s, b), Color.BLACK); break;
				case 4:  board.setPoint(getGrid().at(b, s), Color.BLACK); break;
				case 5:
					if (handicap == 5) board.setPoint(getGrid().at(m, m), Color.BLACK);
					else board.setPoint(getGrid().at(s, m), Color.BLACK);
					break;
				case 6:  board.setPoint(getGrid().at(b, m), Color.BLACK); break;
				case 7:
					if (handicap == 7) board.setPoint(getGrid().at(m, m), Color.BLACK);
					else board.setPoint(getGrid().at(m, s), Color.BLACK);
					break;
				case 8:  board.setPoint(getGrid().at(m, b), Color.BLACK); break;
				case 9:  board.setPoint(getGrid().at(m, m), Color.BLACK); break;
				default:  assert false;
			}
		}
		nextToPlay = Color.WHITE;
	}
	
	/**
	 * Check if a point reaches another color.  The method uses both <code>temp1</code> and
	 * <code>temp2</code> to do its work.  If the point does not reach the color, then <code>temp2</code>
	 * will hold all the points traversed in an attempt to reach the other color.  All those points
	 * will be of the same color as the original point.
	 * @return whether the given point reaches another color
	 * @throws IllegalArgumentException if <code>p</code> is the same as the color, or <code>z</code> out of bounds
	 */
	protected boolean reaches(int z, Point p) {
		Point color = getPoint(z);
		if (color == Point.OUT_OF_BOUNDS || color == p) throw new IllegalArgumentException();
		temp1.clear();  temp2.clear();
		temp1.add(z);
		for (int i = 0; i < temp1.size(); i++) {
			z = temp1.get(i);
			if (getPoint(z) == p) return true;
			if (getPoint(z) == color) {
				// string continues
				temp2.add(z);  // save string member
				temp1.addNeighbors(z);  // expand search
			}
		}
		return false;
	}
	
	/**
	 * Verify that the string connected to the stone at the given position is still alive,
	 * and remove it if it's not.  If the position given is empty or out of bounds, do nothing.
	 * @param z the position of a stone that's a member of the string to verify
	 * @return true if the string was there, alive and remains on the board, false otherwise
	 */
	protected boolean verifyString(int z) {
		Point color = getPoint(z);
		if (color == Point.EMPTY || color == Point.OUT_OF_BOUNDS) return false;
		if (reaches(z, Point.EMPTY)) return true;
		// string is dead!
		for (int i=0; i < temp2.size(); i++) {
			board.setPoint(temp2.get(i), Point.EMPTY);
		}
		return false;
	}
	
	/**
	 * Play at the given position.  The move is automatically assigned to the player
	 * whose turn it is.  If the move is not legal, the board position is not changed
	 * and it's still the same player's move.  If the move is legal, the board reflects
	 * the effects of the move, the number of moves has increased by 1, the move
	 * is recorded, and it's the other player's move.
	 * @param z the position to play at, or MOVE_PASS to pass
	 * @return true if the play was successful, false if it was illegal and rejected
	 */
	public abstract boolean play(int z);
	
	/**
	 * Return whether this game is over.  When the game is over, no further moves
	 * can be made.  Furthermore, for some games, the score can only be counted
	 * once the game is over.
	 * @return true if the game is over, false otherwise
	 */
	public abstract boolean isOver();
	
	/**
	 * Return whether passing is allowed.
	 * @return true if passing can be a valid move, false otherwise; if passing is not allowed,
	 * there must exist another mechanism for ending the game
	 */
	public boolean isPassingAllowed() {return true;}
	
	/**
	 * Calculate the final score of the game.  Return the score in favor of black,
	 * adjusted by the komi.
	 * @return the final score; positive if black won, negative if white won
	 * @throws IllegalStateException if unable to calculate the score for some reason
	 */
	public abstract float score();
	
	/**
	 * Calculate the final score of the game, from the point of view of the given side.
	 * Useful to always get a positive value for a "good" score.
	 * @param side the player from whose perspective the score is returned
	 * @return the final score; positive if the given side won, negative otherwise
	 */
	public float score(Color side) {
		return side == Color.BLACK ? score() : -score();
	}
	
	/**
	 * Return a move played previously.  If the offset is positive, it starts counting from the beginning,
	 * the first move being 1.  If the offset is negative, it starts counting from the current position in
	 * the game, with the last move played being -1.  Moves never include fixed handicap placement.
	 * @param offset the offset of the move to retrieve
	 * @return the move played at the given offset
	 * @throws IndexOutOfBoundsException if the offset specified is outside the list of moves played, or the history horizon of this game
	 */
	public int getMove(int offset) {
		if (offset == 0) throw new IllegalArgumentException("offset cannot be 0");
		if (offset < 0) offset = numMoves + offset + 1;
		if (offset == numMoves) return lastMove;
		else throw new IndexOutOfBoundsException("requested move is beyond history horizon");
	}
	
	protected void recordMove(int move) {
		lastMove = move;
		numMoves++;
	}

	public double getKomi() {return komi;}

	public int getNumPoints() {
		return board.getNumPoints();
	}
	
	public Board.PositionList getPotentiallyPlayablePoints() {
		return board.getEmptyPoints();
	}
	
	public Board.PositionList getNonEmptyPoints() {
		return board.getNonEmptyPoints();
	}

	public Point getPoint(int z) {
		return board.getPoint(z);
	}

	public int getSideSize() {
		return board.getSideSize();
	}
	
	/**
	 * Returns the handicap.
	 */
	public int getHandicap() {
		return handicap;
	}

	/**
	 * Returns the next color to play.
	 */
	public Color getNextToPlay() {
		return nextToPlay;
	}

	/**
	 * Returns the number of moves played so far.  Placing fixed handicap stones
	 * does not count as moves, but placing free ones does.
	 * @return int the number of moves played in the game so far
	 */
	public int getNumMoves() {
		return numMoves;
	}

}
