package es.darkhogg.ld19;

public enum Direction {
	LEFT( -1, 0 ),
	RIGHT( +1, 0 ),
	UP( 0, -1 ),
	DOWN( 0, +1 );
	
	private int x;
	private int y;
	
	private Direction ( int x, int y ) {
		this.x = x;
		this.y = y;
	}
	
	public int getX () {
		return x;
	}
	
	public int getY () {
		return y;
	}

	public Direction rotateLeft () {
		switch ( this ) {
			case UP: return RIGHT;
			case RIGHT: return DOWN;
			case DOWN: return LEFT;
			case LEFT: return UP;
		}
		
		throw new AssertionError();
	}
	
	public Direction rotateRight () {
		switch ( this ) {
			case UP: return LEFT;
			case RIGHT: return UP;
			case DOWN: return RIGHT;
			case LEFT: return DOWN;
		}
		
		throw new AssertionError();
	}
	
	public Direction invert () {
		return rotateLeft().rotateLeft();
	}
}
