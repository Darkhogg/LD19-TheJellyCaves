package es.darkhogg.ld19;


public class Player extends Entity {

	Direction facing = Direction.UP;
	int life = 20;
	
	protected Player ( Game game ) {
		super( game );
	}
	
	protected Player ( Game game, int x, int y ) {
		this( game );
		posX = x;
		posY = y;
	}
	
	public Sprite getSprite () {
		switch ( facing ) {
			case UP:    return Sprite.PLAYER_UP;
			case DOWN:  return Sprite.PLAYER_DOWN;
			case LEFT:  return Sprite.PLAYER_LEFT;
			case RIGHT: return Sprite.PLAYER_RIGHT;
		}
		
		throw new AssertionError();
	}
	
}
