package es.darkhogg.ld19;

public enum TreasureType {

	//COIN( 120, 10, null ),
	//CLOTHES( 60, 170, null ),
	INGOT( 24, 3000, Sprite.INGOT, "Gold Ingot", "a" ),
	PEARL( 24, 500, Sprite.PEARL, "Pearl", "a" ),
	VASE( 24, 250, Sprite.VASE, "Vase", "a" ),
	CROWN( 24, 1200, Sprite.CROWN, "Golden Crown", "a" ),
	DOLLAR( 24, 1, Sprite.DOLLAR, "Dollar", "a" ),
	RING( 24, 800, Sprite.RING, "Silver Ring", "a" ),
	TRIFORCE( 1, 100000, Sprite.TRIFORCE, "Triforce", "the" ),
	CHAOS_EMERALD( 1, 70000, Sprite.CHAOS_EMERALD, "Chaos Emerald", "a" ),
	REDSTONE( 1, 15, Sprite.REDSTONE, "Redstone", "some" ),
	RUPEE( 1, 20, Sprite.RUPEE, "Red Rupee", "a" ),
	COIN_MARIO( 1, 5, Sprite.COIN_MARIO, "Coin", "a" ),
	DARK_GEM( 1, 64, Sprite.DARK_GEM, "Dark Gem", "a" ),
	BUG( 1, -256, Sprite.BUG, "#!@?-%&_:·*", "a" ),
	BANDAGE( 1, 310, Sprite.BANDAGE, "Bandage", "a" ),
	;
	
	private static int totalValues = 0;
	static {
		for ( TreasureType tt : values() ) {
			totalValues += tt.value;
		}
	}
	
	private final int value;
	private final int score;
	private final Sprite sprite;
	private final String prep;
	private final String name;
	
	private TreasureType ( int value, int score, Sprite sprite, String name, String prep ) {
		this.value = value;
		this.score = score;
		this.sprite = sprite;
		this.name = name;
		this.prep = prep;
	}
	
	public int getScore () {
		return score;
	}
	
	public static TreasureType getRandomTreasure () {
		int rndValue = (int)( Math.random()*totalValues );
		int currentValue = 0;
		for ( TreasureType tt : values() ) {
			currentValue += tt.value;
			if ( currentValue > rndValue ) {
				return tt;
			}
		}
		
		return null;
	}
	
	public Sprite getSprite () {
		return sprite;
	}
	
	public String getName () {
		return name;
	}
	
	public String getPrep () {
		return prep;
	}
}
