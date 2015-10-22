package ui.utils;

import javax.swing.JSlider;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class VideoOptions {
	
	private RewindSession rewinding;
	private Boolean isPlaying = false;
	private Boolean isVideo = false;
	private EmbeddedMediaPlayer video;
	
	public void playNewVideo(String newvideo, EmbeddedMediaPlayer video, String currentVideo, ProgressSlider progress) {
		
		this.video = video;
		
		currentVideo = newvideo;
		
//		 Set Progress bar
		progress.setVideo(video);
		progress.setValue(0);

		// Autoplay video
		video.playMedia(currentVideo);

		isPlaying = true;
		isVideo = true;
		progress.setLength();
	}
	
	public void playBtnFuntionality(EmbeddedMediaPlayer video) {
		//Disable rewinds in progress
		if (rewinding != null) {
			rewinding.cancel(true);
			rewinding = null;
		}

		// Resume from other state
		if (isPlaying == false) {
			
			video.setRate(1);
			video.play();
			isPlaying = true;
			//set pause
			
			//Ensure the video is unmuted
			if (video.isMute()) {
				video.mute();
			}

			// Pause if playing normally
		} else {
			if(video.isPlaying()){
				video.pause();
			}
			isPlaying = false;
			//set play

		}
	}
	
	public void rewindBtnFunctionality(EmbeddedMediaPlayer video) {
		// Increase rewind if pressed again
		if (rewinding != null) {
			rewinding.increaseRate();
			return;
		}

		if(video.isPlaying()){
			video.pause();
		}
		isPlaying = false;

		// New BackgroundTask of Rewinding
		rewinding = new RewindSession(video);
		rewinding.execute();
	}
	
	public void forwardBtnFunctionality(EmbeddedMediaPlayer video) {
		// Mute the video
		if (!video.isMute()) {
			video.mute();
		}

		float currentRate = video.getRate();
		float newRate = currentRate + 1;

		// Limit rate to 4x speed
		if (newRate > 4) {
			newRate = 4;
		}
		
		video.setRate(newRate);
		video.play();
		
		//set isPlaying to false
		isPlaying = false;
	}
	
	public void stopBtnFunctionality(EmbeddedMediaPlayer video) {
		video.stop();
		isPlaying = false;
	}
	
	public boolean muteBtnFunctionality(EmbeddedMediaPlayer video) {
		video.mute();
		return video.isMute();
	}
	
	public Boolean getIsPlaying() {
		return isPlaying;
	}
	
	public Boolean getIsVideo() {
		return isVideo;
	}

	public void volCtrlFuntionality(EmbeddedMediaPlayer video, JSlider soundCtrl) {
		video.setVolume(soundCtrl.getValue() * 2);
	}


}
