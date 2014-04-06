package es.darkhogg.ld19;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public final class Font {
	
	final static String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,-_'\"!?()[]{}%$|/\\=+*#&@:;<>·€~^";
	final static BufferedImage SHEET;
	static {
		BufferedImage img = null;
		try {
			img = ImageIO.read( Font.class.getResource( "DhLDFont.png" ) );
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
		
		SHEET = img;
	}
	
	public static void drawStringAt ( String str, Graphics g, int x, int y, double pos ) {
		if ( str.contains( "\n" ) ) {
			int i = 0;
			for ( String piece : str.split( "\n" ) ) {
				drawStringAt( piece, g, x, y+i*8, pos );
				i++;
			}
		} else {
			
			str = str.toUpperCase();
			int ipos = x - (int)(str.length()*8*pos);
			
			for ( int i = 0; i < str.length(); i++ ) {
				char c = str.charAt( i );
				int cp = CHARS.indexOf( c );
				if ( cp >= 0 ) {
					int lx = (cp%8)*8;
					int ly = (cp/8)*8;
					g.drawImage(
						SHEET.getSubimage( lx, ly, 8, 8 ),
						ipos+i*8,
						y,
					null );
				}
			}
			
		}
	}
	
}
