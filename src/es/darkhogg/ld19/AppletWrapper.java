package es.darkhogg.ld19;

import java.applet.Applet;
import java.awt.BorderLayout;

@SuppressWarnings( "serial" )
public final class AppletWrapper extends Applet {
	
	private Game game;
	
	@Override
	public void init () {
		game = new Game();
		
		setLayout( new BorderLayout() );
		add( game );
		setSize( game.getSize() );
		game.start();
	}
	
}
