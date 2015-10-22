package audio;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;

public class AudioConverter {
	
	private Process p;
	private boolean isPreviewing = false;
	private String mp3Path;
	private String mp3Name;
	private String projectPath;
	private String videoPath;
	String outputMp3Name;
	private AddedAudio audioObject;
	private HashMap<String, AddedAudio> addedAudioMap = new HashMap<String, AddedAudio>();
	private List<AudioConverterListener> listeners;
	
	public AudioConverter(String projectPath) {
		this.projectPath = projectPath;
		listeners = new ArrayList<AudioConverterListener>();
	}
	
	public void convertToAudio(String text) {
		// Used to read out input text using festival
		String cmd = "echo " + text + " |festival --tts";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			p = builder.start();
			isPreviewing = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void cancelPreview() {
		try {
			//gets festival process ID
			if(p.getClass().getName().equals("java.lang.UNIXProcess")){
				
				Field f = p.getClass().getDeclaredField("pid");
				f.setAccessible(true);
			
				int pid = f.getInt(p);
				//executes bash command pstree
				String cmd = "pstree -p "+pid;
				ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
				Process process = builder.start();
				
				//input stream to java is the output stream of the bash command
				InputStream stdout = process.getInputStream ();

				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				
				String line = stdoutBuffered.readLine();
				if (line != null) {
					//gets substring of numbers between play( and )
					int startIndex = line.indexOf("play") + "play(".length();
					line = line.substring(startIndex);
					line = line.substring(0, line.indexOf(")"));
					
					//kill play id to kill the festival voice
					String killCmd = "kill "+line;
					ProcessBuilder killBuilder = new ProcessBuilder("/bin/bash", "-c", killCmd);
					killBuilder.start();
				}
				isPreviewing = false;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	public Boolean getIsPreviewing() {
		return isPreviewing;
	}

	public void convertToWav(String festivalText) {
		File f, g;
		
		while (true) {
			mp3Name = JOptionPane.showInputDialog("Please name the synthesized speech file");
			mp3Path = projectPath+"/Audio/" + mp3Name;

			f = new File(mp3Path + ".mp3");
			g = new File(mp3Path + ".wav");
			if (f.exists() || g.exists()) {
				JOptionPane.showMessageDialog(null,"This file already exists.");
			} else if (mp3Path.trim().length() == 0 || mp3Path == null) {
				JOptionPane.showMessageDialog(null,"The name field was left blank.");

			} else {
				break;
			}
		}
		mp3Path = mp3Path.replace(" ", "\\ ");
		
		// Generates a wav file of the specified name, synthesizing the input
		// string into speech
		String cmd = "echo " + festivalText + "| text2wave -o " + mp3Path + ".wav";
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
		String cmd = "ffmpeg -i " + mp3Path + ".wav -f mp3 " +mp3Path + ".mp3";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();

			cmd = "rm " + mp3Path + ".wav";
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			builder.start();
			
			for (AudioConverterListener listener : listeners) {
				listener.audioFileCreated(mp3Name + ".mp3", mp3Path + ".mp3");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	public void removeAudioFile(String filePath) {
		System.out.println(""+filePath);
		String cmd = "rm " + filePath;
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			builder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void progAudioWithDelay(long delay, String audioPath, double volume) {
		outputMp3Name = "delayed" + (new File(audioPath)).getName();
		String cmd = "ffmpeg -y -i " + audioPath + " -filter_complex \"[0:a]volume=" + volume + "[aud2];[aud2]adelay=" + delay + "\" "+projectPath+"/ProjectAudio/" + outputMp3Name;
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		Process process;
		try {
			process = builder.start();
			process.waitFor();
			audioObject = new AddedAudio(projectPath+"/ProjectAudio/" + outputMp3Name, delay);
			addedAudioMap.put(outputMp3Name, audioObject);
			for (AudioConverterListener listener : listeners) {
				listener.addedAudioListChanged();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void progAudioNoDelay(String audioPath, double volume) {
		outputMp3Name = "" + (new File(audioPath)).getName();
		String cmd = "ffmpeg -y -i " + audioPath + " -filter_complex \"[0:a]volume=" + volume +"\" "+projectPath+"/ProjectAudio/" + outputMp3Name;
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		Process process;
		try {
			process = builder.start();
			process.waitFor();
			audioObject = new AddedAudio(projectPath+"/ProjectAudio/" + outputMp3Name, 0);
			addedAudioMap.put(outputMp3Name, audioObject);
			for (AudioConverterListener listener : listeners) {
				listener.addedAudioListChanged();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void mergeAudioToExport(int rows, JTable audioListTable) {
		String inputListToMix = "";
		String key;
		String tempPath;
		
		for(int i = 0; i < rows; i++) {
			key = audioListTable.getValueAt(i, 0).toString();
			tempPath = addedAudioMap.get(key).getAudioPath();
			inputListToMix = inputListToMix + " -i " + tempPath;
		}
		
		String cmd = "ffmpeg" + inputListToMix + " -filter_complex amix=inputs=" + rows + " " + projectPath+"/Videos/temp.mp3";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void mergeVideo() {
		String cmd = "ffmpeg -i " + videoPath + " -i " + projectPath+"/Videos/temp.mp3"
				+" -filter_complex \"[1:a]apad[audio] ; [audio][0:a]amerge[aout]\" -map 0:v -map \"[aout]\" -c:v copy "
				+ projectPath+"/Videos/output.avi";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HashMap<String, AddedAudio> getAddedAudioMap() {
		return addedAudioMap;
	}
	
	public void addListener(AudioConverterListener acl) {
		listeners.add(acl);
	}
	
	public void removeListener(AudioConverterListener acl) {
		listeners.remove(acl);
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

}
