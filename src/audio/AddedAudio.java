package audio;

import java.util.concurrent.TimeUnit;

public class AddedAudio {
	private String audioPath;
	private String startTime;
	private long longStartTime;
	
	public AddedAudio(String audioPath, long startTime) {
		this.audioPath = audioPath;
		this.startTime = formatStartTime(startTime);
		this.longStartTime = startTime;
	}

	private String formatStartTime(long startTime2) {
		long hours = TimeUnit.MILLISECONDS.toHours(startTime2);
		long mins = TimeUnit.MILLISECONDS.toMinutes(startTime2) - TimeUnit.HOURS.toMinutes(hours);
		long secs = TimeUnit.MILLISECONDS.toSeconds(startTime2) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(mins);
		
		return String.format("%02d:%02d:%02d", hours, mins, secs);
	}

//	private void durationToSeconds() {
//		long mins = TimeUnit.MILLISECONDS.toMinutes(duration);
//		long secs = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(mins);
//
//		convertedDuration = String.format("%02d:%02d", mins, secs);
//	}
	
	public String getAudioPath() {
		return audioPath;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public long getLongStartTime() {
		return longStartTime;
	}

}
