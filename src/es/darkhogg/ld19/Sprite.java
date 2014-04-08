package es.darkhogg.ld19;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public enum Sprite {
	DARKHOGG( "spritesheet.png", 0, 0, 16, 16 ),
	SOLID( "spritesheet.png", 16, 0, 16, 16 ),
	WALL( "spritesheet.png", 32, 0, 16, 16 ),
	GROUND( "spritesheet.png", 48, 0, 16, 16 ),
	PLAYER_DOWN( "spritesheet.png", 48, 16, 16, 16 ),
	PLAYER_UP( "spritesheet.png", 32, 16, 16, 16 ),
	PLAYER_RIGHT( "spritesheet.png", 0, 16, 16, 16 ),
	PLAYER_LEFT( "spritesheet.png", 16, 16, 16, 16 ),
	INIT( "spritesheet.png", 64, 0, 16, 16 ),
	DOWN( "spritesheet.png", 112, 0, 16, 16 ),
	CHEST_CLOSED( "spritesheet.png", 80, 0, 16, 16 ),
	CHEST_OPEN( "spritesheet.png", 96, 0, 16, 16 ),
	INGOT( "spritesheet.png", 16*15, 16*15, 16, 16 ),
	TRIFORCE( "spritesheet.png", 16*14, 16*15, 16, 16 ),
	DARK_GEM( "spritesheet.png", 16*13, 16*15, 16, 16 ),
	PEARL( "spritesheet.png", 16*12, 16*15, 16, 16 ),
	CHAOS_EMERALD( "spritesheet.png", 16*11, 16*15, 16, 16 ),
	RUPEE( "spritesheet.png", 16*10, 16*15, 16, 16 ),
	COIN_MARIO( "spritesheet.png", 16*9, 16*15, 16, 16 ),
	VASE( "spritesheet.png", 16*8, 16*15, 16, 16 ),
	CROWN( "spritesheet.png", 16*7, 16*15, 16, 16 ),
	REDSTONE( "spritesheet.png", 16*6, 16*15, 16, 16 ),
	DOLLAR( "spritesheet.png", 16*5, 16*15, 16, 16 ),
	BANDAGE( "spritesheet.png", 16*4, 16*15, 16, 16 ),
	BUG( "spritesheet.png", 16*3, 16*15, 16, 16 ),
	RING( "spritesheet.png", 16*15, 16*14, 16, 16 ),
	JELLY( "spritesheet.png", 0, 32, 16, 16 ),
	FOLLOWER( "spritesheet.png", 16, 32, 16, 16 ),
	HIDER_UP( "spritesheet.png", 32, 32, 16, 16 ),
	HIDER_DOWN( "spritesheet.png", 48, 32, 16, 16 ),
	HEART_FULL( "spritesheet.png", 64, 16, 8, 8 ),
	HEART_HALF( "spritesheet.png", 72, 16, 8, 8 ),
	HEART_EMPTY( "spritesheet.png", 64, 24, 8, 8 ),
	GAME_OVER( "gameover.png", 0, 0, 130, 26 ),
	ARROW_DOWN( "spritesheet.png", /*96*/144, 16, 16, 16 ),
	ARROW_UP( "spritesheet.png", /*80*/144, 16, 16, 16 ),
	ARROW_RIGHT( "spritesheet.png", /*112*/144, 16, 16, 16 ),
	ARROW_LEFT( "spritesheet.png", /*128*/144, 16, 16, 16 ),
	PUFF_BIG( "spritesheet.png", 160, 16, 8, 8 ),
	PUFF_MEDIUM( "spritesheet.png", 160, 24, 8, 8 ),
	PUFF_SMALL( "spritesheet.png", 168, 16, 8, 8 ),
	BLUE_BALL( "spritesheet.png", 168, 24, 8, 8 ),
	TITLE( "title.png", 0, 0, 222, 22 ),
	;
	
	private final BufferedImage image;
	private static Map<String,URL> loadedRes;
	

	private Sprite ( URL url, int x, int y, int w, int h ) {
        System.out.printf("Loading Sprite.%s from '%s'...%n", this, url);
	    
		BufferedImage image = null;
		
		try {
			BufferedImage img  = ImageIO.read( url );
			image = img.getSubimage( x, y, w, h );
		} catch ( IOException e ) {
			e.printStackTrace();
			System.exit( 0 );
		}
		
		this.image = image;
	}
	
	private Sprite ( String imgName, int x, int y, int w, int h ) {
		this( loadResource( imgName ), x, y, w, h );
	}
	
	private static URL loadResource ( String imgName ) {
		if ( loadedRes == null ) {
			loadedRes = new HashMap<String,URL>();
		}
		if ( loadedRes.containsKey( imgName ) ) {
			return loadedRes.get( imgName );
		} else {
			return Sprite.class.getResource( imgName );
		}
		
	}
	
	public BufferedImage getImage () {
		return image;
	}
}
