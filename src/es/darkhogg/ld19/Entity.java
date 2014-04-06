package es.darkhogg.ld19;

public abstract class Entity {

	protected final Game game;
	
	protected int posX, posY;
	protected int spdX, spdY;
	protected int accX, accY;
	
	protected Entity ( Game game ) {
		this.game = game;
	}
	
	public void update () {
		// Accelerate
		spdX += accX;
		spdY += accY;
		
		// Move
		posX += spdX;
		posY += spdY;
	}
}
