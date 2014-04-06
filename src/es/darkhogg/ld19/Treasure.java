package es.darkhogg.ld19;

public final class Treasure {
	
	private final TreasureType type;
	private boolean open;
	
	public Treasure ( TreasureType type ) {
		this.type = type;
	}
	
	public TreasureType getType () {
		return type;
	}
	
	public boolean isOpen () {
		return open;
	}
	
	public TreasureType open () {
		open = true;
		return type;
	}
}
