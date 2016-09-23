package game;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.media.AudioClip;

/**
 * Class to regulate all audio output.
 * 
 * @author Esmee
 *
 */
public class Audio {
	/**
	 * Path for the location of the audiofiles.
	 */
	private static final String PATH = "src/main/resources/audiofiles/";
	/**
	 * Track number for shooting.
	 */
	public static final int SHOOTING = 0;
	/**
	 * Track number for a small asteroid exploding.
	 */
	public static final int SMALLEXPLOSION = 1;
	/**
	 * Track number for a medium asteroid exploding.
	 */
	public static final int MEDIUMEXPLOSION = 2;
	/**
	 * Track number for a large asteroid exploding.
	 */
	public static final int LARGEEXPLOSION = 3;
	/**
	 * Track number for gaining a life.
	 */
	public static final int LIFEUP = 4;
	/**
	 * Map with key and value to easily find tracks.
	 */
	private final List<AudioClip> tracks;

	/**
	 * Constructor for audio class.
	 */
	public Audio() {
		tracks = new ArrayList<AudioClip>();
		try {
			final AudioClip shooting = new AudioClip(new File(
					PATH + "fire.mp3").toURI().toURL().toString());
			final AudioClip smallexplosion = new AudioClip(new File(
					PATH + "bangSmall.mp3").toURI().toURL().toString());
			final AudioClip mediumexplosion = new AudioClip(new File(
					PATH + "bangMedium.mp3").toURI().toURL().toString());
			final AudioClip largeexplosion = new AudioClip(new File(
					PATH + "bangLarge.mp3").toURI().toURL().toString());
			final AudioClip lifeup = new AudioClip(new File(
					PATH + "extraShip.mp3").toURI().toURL().toString());
			
			tracks.add(shooting);
			tracks.add(smallexplosion);
			tracks.add(mediumexplosion);
			tracks.add(largeexplosion);
			tracks.add(lifeup);
		} catch (MalformedURLException e) {
		}
	}

	/**
	 * Get a track by title.
	 * 
	 * @param tracknumber
	 *            number of track to be played
	 * @return AudioClip with that title
	 */
	private AudioClip get(final int tracknumber) {
		return tracks.get(tracknumber);
	}

	/**
	 * Get a track by title and play it.
	 * 
	 * @param tracknumber
	 *            number of track to be played
	 */
	public final void play(final int tracknumber) {
		get(tracknumber).play();
	}
}
