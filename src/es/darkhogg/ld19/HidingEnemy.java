package es.darkhogg.ld19;

public class HidingEnemy extends Enemy {

	boolean canHurt;
	long lastShoot;
	
	protected HidingEnemy ( Game game ) {
		super( game );
		life = 3;
	}
	
	@Override 
	public void update () {
		int difX = posX - game.player.posX;
		int difY = posY - game.player.posY;
		double dist = Math.sqrt( difX*difX + difY*difY );
		
		canHurt = dist > 64;
		
		if ( canHurt && game.now-lastShoot > 1500000000
		  && game.currentCave.isDiscovered( posX/16, posY/16 )
		) {
			lastShoot = game.now;
			
			Ball ball = new Ball( game, posX, posY );
			game.balls.add( ball );
		}
	}
	
	@Override
	public boolean canBeHurt () {
		return canHurt;
	}

	@Override
	public boolean canHurt () {
		return canHurt;
	}

	@Override
	public int getDamage () {
		return 5;
	}

	@Override
	public double getHeartProbability () {
		return 0.2;
	}

	@Override
	public Sprite getSprite () {
		return canHurt ? Sprite.HIDER_UP : Sprite.HIDER_DOWN;
	}

}
