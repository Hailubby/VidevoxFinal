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
	private String outputMp3Name;
	private String outputVidPath;
	private String outputVidName;
	private String setVoice;
	private String setPitch;
	private String setPitchMethod = "(Parameter.set 'Int_Target_Method Int_Targets_Default)";
	private String setSpeed;
	private AddedAudio audioObject;
	private HashMap<String, AddedAudio> addedAudioMap = new HashMap<String, AddedAudio>();
	private List<AudioConverterListener> listeners;
	
	public AudioConverter(String projectPath) {
		this.projectPath = projectPath;
		listeners = new ArrayList<AudioConverterListener>();
	}
	
	public void convertToAudio(String text, String voice, String pitch, String speed) {
		String textToSay = "(SayText \\\""+ text +"\\\")\"";
		setFestivalOptions(voice, pitch, speed);
		// Used to read out input text using festival
		String cmd = "echo \"" + setVoice + " " + setPitch +" " + setPitchMethod + " " + setSpeed + " " + textToSay + " | festival";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			p = builder.start();
			isPreviewing = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setFestivalOptions(String voice, String pitch, String speed) {
		switch (voice) {
		case "KAL":
			setVoice = "(voice_kal_diphone)";
			break;
		case "RAB":
			setVoice = "(voice_rab_diphone)";
			break;
		case "DON":
			setVoice = "(voice_don_diphone)";
			break;
		}
		
		switch (pitch) {
		case "90Hz":
			setPitch = "(set! duffint_params '((start 90) (end 75))) (Parameter.set 'Int_Method 'DuffInt)";
			break;
		case "Default Hz":
			//default pitch, do not set anything
			setPitch = "(set! duffint_params '((start 105) (end 90))) (Parameter.set 'Int_Method 'DuffInt)";
			break;
		case "130Hz":
			setPitch = "(set! duffint_params '((start 130) (end 115))) (Parameter.set 'Int_Method 'DuffInt)";
			break;
		case "180Hz":
			setPitch = "(set! duffint_params '((start 180) (end 150))) (Parameter.set 'Int_Method 'DuffInt)";
			break;
		}
		
		switch(speed) {
		case "0.5x":
			setSpeed = "(Parameter.set 'Duration_Stretch 2.0)";
			break;
		case "0.75x":
			setSpeed = "(Parameter.set 'Duration_Stretch 1.5)";
			break;
		case "1.0x":
			//default speed, no need to set
			setSpeed = "(Parameter.set 'Duration_Stretch 1.0)";
			break;
		case "1.2x":
			setSpeed= "(Parameter.set 'Duration_Stretch 0.8)";
			break;
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

	public Boolean convertToWav(String festivalText, String voice, String pitch, String speed) {
		File f, g;
		
		while (true) {
			mp3Name = JOptionPane.showInputDialog("Please name the synthesized speech file");
			if (mp3Name == null) {
				return false;
			} else {
				if(mp3Name.endsWith(".mp3")) {
					mp3Name = mp3Name.substring(0, mp3Name.indexOf("."));
				}
				mp3Path = projectPath+"/.Audio/" + mp3Name;
	
				f = new File(mp3Path + ".mp3");
				g = new File(mp3Path + ".wav");
				if (f.exists() || g.exists()) {
					JOptionPane.showMessageDialog(null,"This file already exists.");
				} else if (mp3Name.trim().length() == 0 || mp3Name == null) {
					JOptionPane.showMessageDialog(null,"The name field was left blank.");
	
				} else {
					break;
				}
				mp3Path = mp3Path.replace(" ", "\\ ");
			}
		}
		
		if((mp3Name != null) && !mp3Name.isEmpty()) {
			setFestivalOptions(voice, pitch, speed);
			
			// Generates a wav file of the specified name, synthesizing the input
			// string into speech
			String cmd = "echo \"" + festivalText + "\" | text2wave -o " + mp3Path + ".wav" + " -eval \"" + setVoice + "\" -eval \"" + setPitch + "\" -eval \"" + setPitchMethod + "\" -eval \"" + setSpeed + "\"";
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			try {
				Process process = builder.start();
				process.waitFor();
				if (process.exitValue() == 0) {
					return true;
				} else {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
		
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
	
	public boolean mergeAudioToExport(int rows, JTable audioListTable) {
		String inputListToMix = "";
		String key;
		String tempPath;
		
		File f;
		
		while (true) {
			outputVidName = JOptionPane.showInputDialog("Please name the output video file");
			if (outputVidName == null) {
				return false;
			} 
			else {
				if(outputVidName.endsWith("avi")) {
					outputVidName = outputVidName.substring(0, outputVidName.indexOf("."));
				}
				outputVidPath = projectPath+"/Videos/" + outputVidName;
	
				f = new File(outputVidPath + ".avi");
				if (f.exists()) {
					JOptionPane.showMessageDialog(null,"This file already exists.");
				} else if (outputVidName.trim().length() == 0 || outputVidName == null) {
					JOptionPane.showMessageDialog(null,"The name field was left blank.");
				} else {
					break;
				}
				outputVidPath = outputVidPath.replace(" ", "\\ ");
			}
		}
		
		if((outputVidName != null) && !outputVidName.isEmpty()) {
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
				if (process.exitValue() == 0) {
					return true;
				} else {
					return false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void mergeVideo() {
		String cmd = "ffmpeg -i " + videoPath + " -i " + projectPath+"/Videos/temp.mp3"
				+" -filter_complex \"[1:a]apad[audio] ; [audio][0:a]amerge[aout]\" -map 0:v -map \"[aout]\" -c:v copy "
				+ outputVidPath+".avi";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();
			
			cmd = "rm " + projectPath+"/Videos/temp.mp3";
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			builder.start();
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
