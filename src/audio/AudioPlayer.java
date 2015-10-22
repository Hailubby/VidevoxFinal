package audio;

import uk.co.caprica.vlcj.component.AudioMediaPlayerComponent;

public class AudioPlayer {
	private AudioMediaPlayerComponent audioPlayerComponent = new AudioMediaPlayerComponent();
	
	public AudioPlayer() {
		
	}
	
	public AudioPlayer(String mrl, int volume) {
		audioPlayerComponent = new AudioMediaPlayerComponent();
		audioPlayerComponent.getMediaPlayer().playMedia(mrl);
		pause();
		setVolume(volume);
		play();
	}
	
//	public void start(String mrl, int volume) {
//		audioPlayerComponent.getMediaPlayer().playMedia(mrl);
//		audioPlayerComponent.getMediaPlayer().setVolume(volume * 2);
//	}
	
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
		audioPlayerComponent.getMediaPlayer().setVolume(volume * 2);
	}

}
