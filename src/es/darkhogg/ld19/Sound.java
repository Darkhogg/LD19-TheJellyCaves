package es.darkhogg.ld19;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public enum Sound {
	DIG( "dig.wav", "dig1.wav", "dig2.wav", "dig3.wav", "dig4.wav" ),
	HURT( "hurt.wav" ),
	DEATH( "death.wav" ),
	THROW( "throw.wav" ),
	ENEMY_DEATH( "enemdeath.wav" ),
	PICKUP( "pickup.wav" ),
	START( "start.wav" ),
	HEART( "heart.wav" );
	
	private URL[] urls;
	
	private Sound ( URL... urls ) {
		this.urls = urls;
	}
	
	private Sound ( String... names ) {
		urls = new URL[ names.length ];
		for ( int i = 0; i < names.length; i++ ) {
			urls[ i ] = Sound.class.getResource( names[ i ] );
		}
	}
	
	private class SoundPlayer implements Runnable {
		@Override
		public void run () {
			try {
				
				URL url = urls[ (int)( Math.random()*urls.length ) ];
				
				AudioInputStream audioInput = 
					AudioSystem.getAudioInputStream( url );
				AudioFormat audioFormat = audioInput.getFormat();
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat );
				
				SourceDataLine audioLine;

				audioLine = (SourceDataLine) AudioSystem.getLine( info );
	
				audioLine.open( audioFormat );
	
				audioLine.start();		
				
				int bytesRead = 0;
				byte[] audioData = new byte[ 1024 * 128 ]; // 128 KiB
				
				try {
					while ( bytesRead != -1 ) {
						bytesRead = audioInput.read( audioData );
						
						if ( bytesRead >= 0 ) {
							audioLine.write( audioData, 0, bytesRead );
						}
					}
				} catch ( IOException e ) {
					e.printStackTrace();
				} finally {
					audioLine.drain();
					audioLine.close();
				}
			} catch ( LineUnavailableException e1 ) {
				e1.printStackTrace();
			} catch ( UnsupportedAudioFileException e ) {
				e.printStackTrace();
			} catch ( IOException e ) {
				e.printStackTrace();
			}	
		}
	}

	public void play () {
		Thread sndPlayer = new Thread( new SoundPlayer() );
		sndPlayer.setDaemon( true );
		sndPlayer.start();
	}
}
