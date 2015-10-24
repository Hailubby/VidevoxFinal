package audio;

import ui.utils.VideoOptions;
import uk.co.caprica.vlcj.component.AudioMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class AudioPlayer {
	private AudioMediaPlayerComponent audioPlayerComponent = new AudioMediaPlayerComponent();
	
	public AudioPlayer() {
		
	}
	
	public AudioPlayer(String mrl) {
		audioPlayerComponent = new AudioMediaPlayerComponent();
		audioPlayerComponent.getMediaPlayer().playMedia(mrl);
	}
	
	public AudioPlayer(String mrl, final VideoOptions vidOption) {
		audioPlayerComponent = new AudioMediaPlayerComponent();
		audioPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			 @Override
			 public void finished(MediaPlayer mediaPlayer) {
				 vidOption.setPreviewIsFinished(true);
			 }
		 });
		audioPlayerComponent.getMediaPlayer().playMedia(mrl);
	}
	
	public void pause() {
		//pauses audio if it is not paused
		if (audioPlayerComponent.getMediaPlayer().isPlaying()) {
			audioPlayerComponent.getMediaPlayer().pause();
		}
	}
	
	
	public void play() {
		audioPlayerComponent.getMediaPlayer().play();
	}
	
	public void setVolume(int volume) {
		System.out.println(volume);
		audioPlayerComponent.getMediaPlayer().setVolume(volume * 2);
	}

	public void stop() {
		audioPlayerComponent.getMediaPlayer().stop();
	}

}
