package es.darkhogg.ld19;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;

public final class FollowerEnemy extends Enemy {

	long lastMove;
	long lastPath;
	List<Point> path;
	Iterator<Point> pathIt;
	
	protected FollowerEnemy ( Game game ) {
		super( game );
		life = 2;
	}
	
	@Override
	public void update () {
		super.update();
		
		if ( game.now - lastMove > 300000000L ) {
			lastMove = game.now;

			int difX = posX - game.player.posX;
			int difY = posY - game.player.posY;
			double dist = Math.sqrt( difX*difX + difY*difY );
			if ( path == null || dist > 512 || path.isEmpty() || !pathIt.hasNext() ) {
				Direction dir = Direction.values()[ (int)( Math.random()*Direction.values().length ) ];
				if ( game.currentCave.getTerrainAt( posX/16 + dir.getX(), posY/16 + dir.getY() ).isWalkable() ) {
					posX += dir.getX()*16;
					posY += dir.getY()*16;
				}
			} else {
				Point p;
				do {
					p = pathIt.next();
				} while ( posX == p.x && posY == p.y );
				posX = p.x*16;
				posY = p.y*16;
			}
			
			if ( (dist < 256 && game.now-lastPath > 3*1000*1000*1000) || dist < 64 ) {
				lastPath = game.now;
				path = game.currentCave.getShortestPath(
					new Point( posX/16, posY/16 ),
					new Point( game.player.posX/16, game.player.posY/16 )
				);
				if ( path != null ) {
					pathIt = path.iterator();
				}
			}
		}
	}
	
	@Override
	public boolean canHurt () {
		return true;
	}

	@Override
	public int getDamage () {
		return 3;
	}

	@Override
	public Sprite getSprite () {
		return Sprite.FOLLOWER;
	}

	@Override
	public double getHeartProbability () {
		return 0.1;
	}

	@Override
	public boolean canBeHurt () {
		return true;
	}

}
