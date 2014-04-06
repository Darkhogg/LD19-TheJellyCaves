package es.darkhogg.ld19;

public class Ball extends Entity {

	protected double posX, posY;
	protected double spdX, spdY;
	protected double accX, accY;
	
	boolean destroy;
	
	protected Ball ( Game game, int x, int y ) {
		super( game );
		
		posX = x;
		posY = y;
		
		double ang = Math.atan2( posY-game.player.posY, posX-game.player.posX );
		spdX = -4*Math.cos( ang );
		spdY = -4*Math.sin( ang );
	}
	
	@Override public void update () {
		// Accelerate
		spdX += accX;
		spdY += accY;
		
		// Move
		posX += spdX;
		posY += spdY;
		
		// Check collisions
		if ( !game.currentCave.getTerrainAt( (int)((posX+8)/16), (int)((posY+8)/16) ).isWalkable() ) {
			destroy = true;
		}
	}
}
