package es.darkhogg.ld19;

public enum TerrainType {
	GROUND( '.', Sprite.GROUND, true ),
	WALL( '%', Sprite.WALL, false ),
	SOLID( '#', Sprite.SOLID, false ),
	INIT( 'I', Sprite.INIT, true ),
	EXIT( 'E', Sprite.DOWN, true ),
	CHEST_CLOSED( 'C', Sprite.CHEST_CLOSED, false ),
	CHEST_OPEN( 'c', Sprite.CHEST_OPEN, false ),
	;

	private final char symbol;
	private final Sprite sprite;
	private final boolean walkable;
	
	private TerrainType ( char symbol, Sprite sprite, boolean walkable ) {
		this.symbol = symbol;
		this.sprite = sprite;
		this.walkable = walkable;
	}
	
	public char getSymbol () {
		return symbol;
	}
	
	public Sprite getSprite () {
		return sprite;
	}

	public boolean isWalkable () {
		return walkable;
	}
}
