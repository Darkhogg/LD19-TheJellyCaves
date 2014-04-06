package es.darkhogg.ld19;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

@SuppressWarnings( "serial" )
public final class GuiWrapper extends JFrame implements WindowListener {
	
	private Game game;
	
	public GuiWrapper () {
		super( "The Jelly Caves" );
		
		game = new Game();
		add( game, BorderLayout.CENTER );
		
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		addWindowListener( this );
		pack();
	}
	
	public static void main ( String[] args ) {
		GuiWrapper frame = new GuiWrapper();
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
		frame.game.start();
		frame.setIconImage(
			Toolkit.getDefaultToolkit().getImage(
				GuiWrapper.class.getResource( "DhIcon.png" )
			)
		);
	}
	
	@Override
	public void windowActivated ( WindowEvent e ) {
	}

	@Override
	public void windowClosed ( WindowEvent e ) {
	}

	@Override
	public void windowClosing ( WindowEvent we ) {
		game.stop();
		try {
			game.gameThread.join();
		} catch ( InterruptedException e ) {
			e.printStackTrace();
			System.exit( 0 );
		}
		dispose();
	
	}

	@Override
	public void windowDeactivated ( WindowEvent e ) {
	}

	@Override
	public void windowDeiconified ( WindowEvent e ) {
	}

	@Override
	public void windowIconified ( WindowEvent e ) {
	}

	@Override
	public void windowOpened ( WindowEvent e ) {
	}
	
}
