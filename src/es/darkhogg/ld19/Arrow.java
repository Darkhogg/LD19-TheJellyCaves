package es.darkhogg.ld19;

public class Arrow extends Entity {

	Direction facing;
	
	public Arrow ( Game game, Direction dir ) {
		super( game );
		facing = dir;

		spdX = facing.getX()*6;
		spdY = facing.getY()*6;
	}
	
	public Sprite getSprite () {
		switch ( facing ) {
			case UP:    return Sprite.ARROW_UP;
			case DOWN:  return Sprite.ARROW_DOWN;
			case LEFT:  return Sprite.ARROW_LEFT;
			case RIGHT: return Sprite.ARROW_RIGHT;
		}
		
		throw new AssertionError();
	}
	
	@Override
	public void update () {
		super.update();
	}
}
