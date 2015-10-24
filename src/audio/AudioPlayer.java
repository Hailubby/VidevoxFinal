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
		System.out.print(volume);
		play();
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

}
