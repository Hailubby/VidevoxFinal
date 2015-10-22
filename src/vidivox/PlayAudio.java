package vidivox;

import uk.co.caprica.vlcj.component.AudioMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayer;

public class PlayAudio {
	
	private final AudioMediaPlayerComponent mediaPlayerComponent;
	
	PlayAudio(final vidivox.MediaPlayer frame, final boolean multiAudio) {
		 mediaPlayerComponent = new AudioMediaPlayerComponent();

		 mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			 @Override
			 public void finished(MediaPlayer mediaPlayer) {

				 if (!multiAudio) {
					 //sets boolean to true when the audio has finished playing
					 frame.setAudioIsFinished(true);
					 }
				 else {
					 //sets boolean to true when teh audio has finished playing
					 frame.setAudioIsFinished2(true);
					 }
				 }
			 });
		 }

	
	public void start(String mrl) {
		mediaPlayerComponent.getMediaPlayer().playMedia(mrl);
		}
	
	public void pause() {
		//pauses audio if it is not paused
		if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
			mediaPlayerComponent.getMediaPlayer().pause();
		}
	}
	
	public void play() {
		mediaPlayerComponent.getMediaPlayer().play();
	}
	
	public void setVolume(int volume) {
		mediaPlayerComponent.getMediaPlayer().setVolume(volume);
	}

}
