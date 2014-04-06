package es.darkhogg.ld19;

public class Particle extends Entity {
	
	Sprite sprite;
	int life;
	
	protected double posX, posY;
	protected double spdX, spdY;
	protected double accX, accY;
	
	public Particle (
		Game game, double x, double y, double xSpd, double ySpd,
		double xAcc, double yAcc, Sprite sprite, int life
	) {
		super( game );
		posX = x;
		posY = y;
		spdX = xSpd;
		spdY = ySpd;
		accX = xAcc;
		accY = yAcc;
		this.sprite = sprite;
		this.life = life;
	}
	
	@Override public void update () {
		// Accelerate
		spdX += accX;
		spdY += accY;
		
		// Move
		posX += spdX;
		posY += spdY;
		
		life--;
	}
}
