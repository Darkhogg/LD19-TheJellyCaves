package es.darkhogg.ld19;

public class JellyEnemy extends Enemy {

	long lastMove;
	
	protected JellyEnemy ( Game game ) {
		super( game );
		life = 1;
	}
	
	@Override
	public void update () {
		super.update();
		
		if ( game.now - lastMove > 1000000000L ) {
			lastMove = game.now;
			Direction dir = Direction.values()[ (int)( Math.random()*Direction.values().length ) ];
			if ( game.currentCave.getTerrainAt( posX/16 + dir.getX(), posY/16 + dir.getY() ).isWalkable() ) {
				posX += dir.getX()*16;
				posY += dir.getY()*16;
			}
		}
	}

	@Override
	public Sprite getSprite () {
		return Sprite.JELLY;
	}

	@Override
	public boolean canHurt () {
		return true;
	}

	@Override
	public int getDamage () {
		return 1;
	}

	
	@Override
	public double getHeartProbability () {
		return 0.05;
	}

	@Override
	public boolean canBeHurt () {
		return true;
	}
}
