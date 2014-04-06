package es.darkhogg.ld19;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public final class Cave {

	protected final int width;
	protected final int height;
	protected final TerrainType[][] terrainData;
	protected final boolean[][] discovered;
	protected final double[][] light;
	protected final Treasure[][] treasures;
	
	protected int level = 1;
	protected Cave parentCave;
	protected int parentX;
	protected int parentY;
	
	final Map<Point,Cave> subCaves = new HashMap<Point,Cave>();
	
	protected int iniX;
	protected int iniY;
	
	private Cave ( int width, int height, TerrainType[][] terrainData ) {
		this.width = width;
		this.height = height;
		this.terrainData = terrainData;
		this.discovered = new boolean[ width ][ height ];
		this.light = new double[ width ][ height ];
		this.treasures = new Treasure[ width ][ height ];
	}
	
	public static Cave generateCave ( int width, int height ) {

		final double SRATIO = 0.35;
		final double WRATIO = 0.51;
		final int COUNT = 5;
		final int TIMES = 15;
		
		TerrainType[][] curr, temp, aux;
		curr = new TerrainType[ width ][ height ];
		aux = new TerrainType[ width ][ height ];
		
		// Initialize terrain
		for ( int i = 0; i < width-0; i++ ) {
			for ( int j = 0; j < height-0; j++ ) {
				TerrainType tt =
					( Math.random()<SRATIO || i==0 || j==0 || i==width-1 || j==height-1 )
					? TerrainType.SOLID
					: TerrainType.GROUND;;
				curr[ i ][ j ] = tt;
				aux[ i ][ j ] = tt;
					
			}
		}
		
		// Step terrain (SOLID)
		for ( int n = 0; n < TIMES; n++ ) {
			// Swap
			temp = curr;
			curr = aux;
			aux = temp;
			
			// Process SOLID walls ( aux -> curr )
			for ( int i = 1; i < width-1; i++ ) {
				for ( int j = 1; j < height-1; j++ ) {
					int count = 0;

					count += (aux[ i-1 ][ j-1 ] == TerrainType.SOLID) ? 1 : 0;
					count += (aux[ i-1 ][ j ] == TerrainType.SOLID) ? 1 : 0;
					count += (aux[ i-1 ][ j+1 ] == TerrainType.SOLID) ? 1 : 0;
					count += (aux[ i ][ j-1 ] == TerrainType.SOLID) ? 1 : 0;
					count += (aux[ i ][ j ] == TerrainType.SOLID) ? 1 : 0;
					count += (aux[ i ][ j+1 ] == TerrainType.SOLID) ? 1 : 0;
					count += (aux[ i+1 ][ j-1 ] == TerrainType.SOLID) ? 1 : 0;
					count += (aux[ i+1 ][ j ] == TerrainType.SOLID) ? 1 : 0;
					count += (aux[ i+1 ][ j+1 ] == TerrainType.SOLID) ? 1 : 0;
					
					curr[ i ][ j ] = (count >= COUNT)
						? TerrainType.SOLID
						: TerrainType.GROUND;
				}
			}
		}
		
		// Initialize walls
		for ( int i = 0; i < width-0; i++ ) {
			for ( int j = 0; j < height-0; j++ ) {
				if ( curr[ i ][ j ] != TerrainType.SOLID ) {
					TerrainType tt =
						( Math.random()<WRATIO || i==0 || j==0 || i==width-1 || j==height-1 )
						? TerrainType.WALL
						: TerrainType.GROUND;;
					curr[ i ][ j ] = tt;
				}
				
				aux[ i ][ j ] = curr[ i ][ j ];
			}
		}
		
		// Step terrain (WALLS)
		for ( int n = 0; n < TIMES; n++ ) {
			// Swap
			temp = curr;
			curr = aux;
			aux = temp;
			
			// Process walls ( aux -> curr )
			for ( int i = 1; i < width-1; i++ ) {
				for ( int j = 1; j < height-1; j++ ) {
					if ( aux[ i ][ j ] != TerrainType.SOLID ) {
						int count = 0;
	
						count += (aux[ i-1 ][ j-1 ] != TerrainType.GROUND) ? 1 : 0;
						count += (aux[ i-1 ][ j ] != TerrainType.GROUND) ? 1 : 0;
						count += (aux[ i-1 ][ j+1 ] != TerrainType.GROUND) ? 1 : 0;
						count += (aux[ i ][ j-1 ] != TerrainType.GROUND) ? 1 : 0;
						count += (aux[ i ][ j ] != TerrainType.GROUND) ? 1 : 0;
						count += (aux[ i ][ j+1 ] != TerrainType.GROUND) ? 1 : 0;
						count += (aux[ i+1 ][ j-1 ] != TerrainType.GROUND) ? 1 : 0;
						count += (aux[ i+1 ][ j ] != TerrainType.GROUND) ? 1 : 0;
						count += (aux[ i+1 ][ j+1 ] != TerrainType.GROUND) ? 1 : 0;
						
						curr[ i ][ j ] = (count >= COUNT)
							? TerrainType.WALL
							: TerrainType.GROUND;
					}
				}
			}
		}

		// Create cave
		Cave cave = new Cave( width, height, curr );
		
		// Create some treasures at random positions
		// There is a minimum and a maximum, chests in walls are discarded
		{
			int num = 0, i = 0;
			double min = Math.sqrt( width*height )/32;
			double max = Math.sqrt( width*height );
			while ( num < min || i < max ) {
				i++;
				int x = (int)( Math.random()*width );
				int y = (int)( Math.random()*height );
				if ( cave.terrainData[ x ][ y ].isWalkable()
				  && cave.treasures[ x ][ y ] == null
				) {
					num++;
					cave.treasures[ x ][ y ] = new Treasure( TreasureType.getRandomTreasure() );
					cave.terrainData[ x ][ y ] = TerrainType.CHEST_CLOSED;
				}
			}
		}
		
		// Generate exits
		{
			int num = 0, i = 0;
			while ( num < 1 || i < 5 ) {
				i++;
				int x = (int)( Math.random()*width );
				int y = (int)( Math.random()*height );
				if ( cave.getTerrainAt( x, y ) == TerrainType.GROUND
				  && cave.getTerrainAt( x-1, y ) == TerrainType.GROUND
				  && cave.getTerrainAt( x+1, y ) == TerrainType.GROUND
				  && cave.getTerrainAt( x, y-1 ) == TerrainType.GROUND
				  && cave.getTerrainAt( x, y+1 ) == TerrainType.GROUND
				  //&& cave.terrainData[ x ][ y ] != TerrainType.EXIT
				) {
					num++;
					cave.terrainData[ x ][ y ] = TerrainType.EXIT;
				}
			}
		}
		
		// Create spawn point
		List<Point> points = new ArrayList<Point>();;
		final int sizeX = 80, sizeY = 80;
		for ( int i = 0; i < sizeX; i++ ) {
			for ( int j = 0; j < sizeY; j++ ) {
				if ( cave.getTerrainAt( i, j ) == TerrainType.GROUND
				  && cave.getTerrainAt( i-1, j ) == TerrainType.GROUND
				  && cave.getTerrainAt( i+1, j ) == TerrainType.GROUND
				  && cave.getTerrainAt( i, j-1 ) == TerrainType.GROUND
				  && cave.getTerrainAt( i, j+1 ) == TerrainType.GROUND
				) {
					points.add( new Point( i, j ) );
				}
			}
		}
		
		if ( points.isEmpty() ) {
			return Cave.generateCave( width, height );
		} else {
			Point spawn = points.get( (int)( Math.random()*points.size() ) );
			cave.iniX = spawn.x;
			cave.iniY = spawn.y;
			cave.discover( spawn.x, spawn.y );
			cave.calcLight( spawn.x, spawn.y );
			
			cave.terrainData[ cave.iniX ][ cave.iniY ] = TerrainType.INIT;
			
			// Return
			return cave;
		}
	}
	
	public TerrainType getTerrainAt ( int x, int y ) {
		if ( x < 0 || x >= width || y < 0 || y >= height ) {
			return TerrainType.SOLID;
		}
		return terrainData[ x ][ y ];
	}
	
	public boolean isDiscovered ( int x, int y ) {
		if ( x < 0 || x >= width || y < 0 || y >= height ) {
			return false;
		}
		return discovered[ x ][ y ];
	}
	
	public double getLightAt ( int x, int y ) {
		if ( x < 0 || x >= width || y < 0 || y >= height ) {
			return 0.0;
		}
		return light[ x ][ y ];
	}
	
	public void discover ( int x, int y ) {
		Queue<Point> queue = new LinkedList<Point>();
		queue.add( new Point( x, y ) );
		discovered[ x ][ y ] = true;
		
		while ( !queue.isEmpty() ) {
			Point p = queue.remove();

			if ( terrainData[ p.x-1 ][ p.y-1 ].isWalkable()
			  && !discovered[ p.x-1 ][ p.y-1 ]
			) {
				queue.add( new Point( p.x-1, p.y-1 ) );
			}
			discovered[ p.x-1 ][ p.y-1 ] = true;

			if ( terrainData[ p.x-1 ][ p.y ].isWalkable()
			  && !discovered[ p.x-1 ][ p.y ]
			) {
				queue.add( new Point( p.x-1, p.y ) );
			}
			discovered[ p.x-1 ][ p.y ] = true;

			if ( terrainData[ p.x-1 ][ p.y+1 ].isWalkable()
			  && !discovered[ p.x-1 ][ p.y+1 ]
			) {
				queue.add( new Point( p.x-1, p.y+1 ) );
			}
			discovered[ p.x-1 ][ p.y+1 ] = true;

			if ( terrainData[ p.x ][ p.y-1 ].isWalkable()
			  && !discovered[ p.x ][ p.y-1 ]
			) {
				queue.add( new Point( p.x, p.y-1 ) );
			}
			discovered[ p.x ][ p.y-1 ] = true;

			if ( terrainData[ p.x ][ p.y+1 ].isWalkable()
			  && !discovered[ p.x ][ p.y+1 ]
			) {
				queue.add( new Point( p.x, p.y+1 ) );
			}
			discovered[ p.x ][ p.y+1 ] = true;

			if ( terrainData[ p.x+1 ][ p.y-1 ].isWalkable()
			  && !discovered[ p.x+1 ][ p.y-1 ]
			) {
				queue.add( new Point( p.x+1, p.y-1 ) );
			}
			discovered[ p.x+1 ][ p.y-1 ] = true;

			if ( terrainData[ p.x+1 ][ p.y ].isWalkable()
			  && !discovered[ p.x+1 ][ p.y ]
			) {
				queue.add( new Point( p.x+1, p.y ) );
			}
			discovered[ p.x+1 ][ p.y ] = true;

			if ( terrainData[ p.x+1 ][ p.y+1 ].isWalkable()
			  && !discovered[ p.x+1 ][ p.y+1 ]
			) {
				queue.add( new Point( p.x+1, p.y+1 ) );
			}
			discovered[ p.x+1 ][ p.y+1 ] = true;
		}
	}
	
	public void calcLight ( int x, int y ) {
		for ( int i = 0; i < width; i++ ) {
			for ( int j = 0; j < height; j++ ) {
				light[ i ][ j ] = 0.0;
			}
		}
		
		Queue<Point> queue = new LinkedList<Point>();
		queue.add( new Point( x, y ) );
		light[ x ][ y ] = 0.8;

		final double SUB = 0.14;
		final double SUBD = SUB*1.414;
		while ( !queue.isEmpty() ) {
			Point p = queue.remove();
			double cl = light[ p.x ][ p.y ];
			
			// Adjacents
			if ( light[ p.x-1 ][ p.y ] < cl-SUB ) {
				light[ p.x-1 ][ p.y ] = cl-SUB;
				if ( getTerrainAt( p.x-1, p.y ).isWalkable() ) {
					queue.add( new Point( p.x-1, p.y ) );
				}
			}
			if ( light[ p.x+1 ][ p.y ] < cl-SUB ) {
				light[ p.x+1 ][ p.y ] = cl-SUB;
				if ( getTerrainAt( p.x+1, p.y ).isWalkable() ) {
					queue.add( new Point( p.x+1, p.y ) );
				}
			}
			if ( light[ p.x ][ p.y-1 ] < cl-SUB ) {
				light[ p.x ][ p.y-1 ] = cl-SUB;
				if ( getTerrainAt( p.x, p.y-1 ).isWalkable() ) {
					queue.add( new Point( p.x, p.y-1 ) );
				}
			}
			if ( light[ p.x ][ p.y+1 ] < cl-SUB ) {
				light[ p.x ][ p.y+1 ] = cl-SUB;
				if ( getTerrainAt( p.x, p.y+1 ).isWalkable() ) {
					queue.add( new Point( p.x, p.y+1 ) );
				}
			}
			
			// Diagonals
			if ( light[ p.x-1 ][ p.y-1 ] < cl-SUBD ) {
				light[ p.x-1 ][ p.y-1 ] = cl-SUBD;
				if ( getTerrainAt( p.x-1, p.y-1 ).isWalkable() ) {
					queue.add( new Point( p.x-1, p.y-1 ) );
				}
			}
			if ( light[ p.x-1 ][ p.y+1 ] < cl-SUBD ) {
				light[ p.x-1 ][ p.y+1 ] = cl-SUBD;
				if ( getTerrainAt( p.x-1, p.y+1 ).isWalkable() ) {
					queue.add( new Point( p.x-1, p.y+1 ) );
				}
			}
			if ( light[ p.x+1 ][ p.y-1 ] < cl-SUBD ) {
				light[ p.x+1 ][ p.y-1 ] = cl-SUBD;
				if ( getTerrainAt( p.x+1, p.y-1 ).isWalkable() ) {
					queue.add( new Point( p.x+1, p.y-1 ) );
				}
			}
			if ( light[ p.x+1 ][ p.y+1 ] < cl-SUBD ) {
				light[ p.x+1 ][ p.y+1 ] = cl-SUBD;
				if ( getTerrainAt( p.x+1, p.y+1 ).isWalkable() ) {
					queue.add( new Point( p.x+1, p.y+1 ) );
				}
			}
		}
	}
	
	public List<Point> getShortestPath ( Point start, Point end ) {
		
		class Aux{double g,h;Point p;
			public Aux(double g,double h,Point p){this.g=g;this.h=h;this.p=p;}};
		
		Map<Point,Aux> closedSet = new HashMap<Point,Aux>();
		Map<Point,Aux> openSet = new HashMap<Point,Aux>();
		openSet.put( start, new Aux( 0, Math.abs(start.x-end.x) + Math.abs(start.y-end.y), null ) );
		
		boolean cont = true;
		while ( !openSet.isEmpty() && cont ) {
			
			// Search the minimum F point
			Point p = null;
			double f = 0;
			for ( Map.Entry<Point,Aux> e : openSet.entrySet() ) {
				double nF = e.getValue().g + e.getValue().h;
				if ( p == null || nF < f ) {
					f = nF;
					p = e.getKey();
				}
			}

			// Close the current point
			Aux a = openSet.remove( p );
			closedSet.put( p, a );
			
			// Add adjacent points to the open set
			Point[] ps = {
					new Point( p.x-1, p.y ),
					new Point( p.x+1, p.y ),
					new Point( p.x, p.y-1 ),
					new Point( p.x, p.y+1 ),
			};
			
			for ( Point np : ps ) {
				if ( getTerrainAt( np.x, np.y ).isWalkable() ) {
					Aux na = new Aux(
						a.g+1,
						Math.abs( np.x - end.x ) + Math.abs( np.y - end.y ),
						p
					);
					if ( closedSet.containsKey( np ) ) {
						Aux oa = closedSet.get( np );
						if ( oa.g+oa.h > na.g+na.h ) {
							closedSet.put( np, na );
						}
					} else {
						openSet.put( np, na);
					}
				}
			}
			
			// End
			if ( p.equals( end ) ) {
				cont = false;
				
				LinkedList<Point> path = new LinkedList<Point>();
				do {
					path.addFirst( p );
					p = closedSet.get( p ).p;
				} while ( p != null && !p.equals( start ) );
				
				return path;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder();
		/*sb.append( width );
		sb.append( ' ' );
		sb.append( height );
		sb.append( '\n' );*/
		
		for ( int j = 0; j < height; j++ ) {
			for ( int i = 0; i < width; i++ ) {
				sb.append( terrainData[ i ][ j ].getSymbol() );
			}
			sb.append( '\n' );
		}
		
		return sb.toString();
	}
}
