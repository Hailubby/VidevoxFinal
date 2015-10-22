package audio;

public interface AudioConverterListener {
	
	void addedAudioListChanged();
	void audioFileCreated(String fileName, String filePath);

}
