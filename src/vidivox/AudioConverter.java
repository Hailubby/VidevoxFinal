package vidivox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class AudioConverter {

	private String fileName;
	private JFrame relativeFrame;
	
	public AudioConverter(JFrame frame) {
		relativeFrame = frame;
		initialiseFolder();
	}

	//Sets up folder for MP3 and Video outputs
	public void initialiseFolder() {
		String cmd = "mkdir -p CustomVidivoxFolder";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void convertToAudio(String text) {
		// Used to read out input text using festival
		String cmd = "echo " + text + " |festival --tts";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void convertToWav(String text) {
		// Retrieves filename from user to save wav file, stored in instance
		File f, g;

		while (true) {
			fileName = JOptionPane.showInputDialog(relativeFrame, "Please name the synthesized speech file");
			fileName = "CustomVidivoxFolder/" + fileName;

			f = new File(fileName + ".mp3");
			g = new File(fileName + ".wav");
			if (f.exists() || g.exists()) {
				JOptionPane.showMessageDialog(relativeFrame, "This file already exists.");
			} else if (fileName.trim().length() == 0 || fileName == null) {
				JOptionPane.showMessageDialog(relativeFrame, "The name field was left blank.");

			} else {
				break;
			}

		}
		fileName = fileName.replace(" ", "\\ ");
		
		// Generates a wav file of the specified name, synthesizing the input
		// string into speech
		String cmd = "echo " + text + "| text2wave -o " + fileName + ".wav";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void wavToMp3() {
		// Converts from wav file to mp3 file using FFMPEG
		String cmd = "ffmpeg -i " + fileName + ".wav -f mp3 " +fileName + ".mp3";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();

			cmd = "rm " + fileName + ".wav";
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			builder.start();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	//Strips audio from the current video, output to custom file name
	public void removeAudio(String currentVideo, String outputVideo) {

		try {
			currentVideo = currentVideo.replace(" ", "\\ ");
			outputVideo = outputVideo.trim().replace(" ", "\\ ");

			outputVideo = "CustomVidivoxFolder/" + outputVideo;

			String cmd = "ffmpeg -i " + currentVideo + " -c copy -an " + outputVideo;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//Merges up to two audio tracks into the video
	public String embedAudio(String currentVideo, String currentFestAudio, String currentOwnAudio) {
		try {
			String currentAudio = null;

			currentVideo = currentVideo.replace(" ", "\\ ");

			File f;
			String outputFile;

			//Loops a request for a filename until a valid one is input
			while (true) {
				outputFile = JOptionPane.showInputDialog(relativeFrame,
						"Select a name for the outputted video file, please retain the current video extension");
				f = new File("CustomVidivoxFolder/" + outputFile);
				if (f.exists()) {
					JOptionPane.showMessageDialog(relativeFrame, "This file already exists.");
				} else if (outputFile.trim().length() == 0) {
					JOptionPane.showMessageDialog(relativeFrame, "The name field was left blank.");
				} else {
					break;
				}

			}

			outputFile = "CustomVidivoxFolder/" + outputFile;
			outputFile.replace(" ", "\\ ");
			
			// Converts to bash-friendly path names, changes spaces to "\ "
			if (currentFestAudio == null) {
				currentOwnAudio = currentOwnAudio.replace(" ", "\\ ");
				currentAudio = currentOwnAudio;
			} else if (currentOwnAudio == null) {
				currentFestAudio = currentFestAudio.replace(" ", "\\ ");
				currentAudio = currentFestAudio;
			} else {
				currentFestAudio=currentFestAudio.replace(" ", "\\ ");
				currentOwnAudio=currentOwnAudio.replace(" ", "\\ ");
			
			}
			String cmd;
			
			if (isAudioless(currentVideo) == null || isAudioless(currentVideo) == "") {
				// Merges a single audio stream into audioless video
				if (currentAudio != null) {
					cmd = "ffmpeg -i " + currentVideo + " -i "+currentAudio + " -map 0:v -map 1:a -c copy " + outputFile;
				}
				// Merges synthetic voice and custom mp3 selection into audioless video
				else {
					cmd = "ffmpeg -i " + currentVideo + " -i " + currentFestAudio + " -i " + currentOwnAudio
							+ " -filter_complex \"[1:a]apad[audio1] ; [audio1][2:a]amerge[aout] ; [aout]apad[finalaudio]\" -map 0:v -map \"[finalaudio]\" -c:v copy -shortest " + outputFile;
				}
			}
			else {
				
				// Merges a single audio stream into the video
				if (currentAudio != null) {
					cmd = "ffmpeg -i " + currentVideo + " -i " + currentAudio
							+" -filter_complex \"[1:a]apad[audio] ; [audio][0:a]amerge[aout]\" -map 0:v -map \"[aout]\" -c:v copy "
							+ outputFile;
	
				}
				// Merges synthetic voice and custom mp3 selection into the video
				else {
					cmd = "ffmpeg -i " + currentVideo + " -i " + currentFestAudio + " -i " + currentOwnAudio
							+ " -filter_complex \"[1:a]apad[audio1] ; [2:a]apad[audio2] ; [0:a][audio1][audio2]amerge=inputs=3[aout]\" -map 0:v -map \"[aout]\" -c:v copy "
							+ outputFile;
	
				}
			}
			System.out.println(cmd);
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();
			
			//Checks exit status to ensure merging is successful
			if (process.exitValue() != 0) {
				JOptionPane.showMessageDialog(relativeFrame,
						"Merge failed, remember to add the correct video extension to the output name");
				return null;
			}
			return outputFile;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	//Probes selected video file to see if an audio track exists
	public String isAudioless(String currentVideo) {
		try {
			currentVideo = currentVideo.replace(" ", "\\ ");
			String cmd = "ffprobe -i " + currentVideo + " -show_streams -select_streams a -loglevel error>info.txt";
			
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("info.txt")));
			String output = br.readLine();
			File f = new File("info.txt");
			f.delete();
			br.close();
			return output;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;

	}

	public String getFileName() {
		return fileName;
	}

}
