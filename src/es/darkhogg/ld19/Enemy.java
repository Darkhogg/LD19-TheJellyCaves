package es.darkhogg.ld19;

public abstract class Enemy extends Entity {

	int life;
	
	protected Enemy ( Game game ) {
		super( game );
	}

	public abstract Sprite getSprite ();
	public abstract boolean canHurt ();
	public abstract boolean canBeHurt ();
	public abstract int getDamage ();
	public abstract double getHeartProbability ();

	public static Enemy getForLevel ( Game game, int level ) {
		
		switch ( level ) {
			case 1:
				if ( Math.random() < 0.05 ) {
					return new HidingEnemy( game );
				}
				return new JellyEnemy( game );

			case 2:
				if ( Math.random() < 0.2 ) {
					return new FollowerEnemy( game );
				}
				if ( Math.random() < 0.1 ) {
					return new HidingEnemy( game );
				}
				return new JellyEnemy( game );
				
			case 3:
				if ( Math.random() < 0.4 ) {
					return new FollowerEnemy( game );
				}
				if ( Math.random() < 0.2 ) {
					return new HidingEnemy( game );
				}
				return new JellyEnemy( game );
				
			case 4:
				if ( Math.random() < 0.8 ) {
					return new FollowerEnemy( game );
				}
				if ( Math.random() < 0.3 ) {
					return new HidingEnemy( game );
				}
				return new JellyEnemy( game );
			
			default:
				if ( Math.random() < 0.8 ) {
					return new FollowerEnemy( game );
				}
				if ( Math.random() < 0.4 ) {
					return new HidingEnemy( game );
				}
				return new JellyEnemy( game );
		}
	}
}
