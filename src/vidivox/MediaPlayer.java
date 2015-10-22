package vidivox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

@SuppressWarnings("serial")
public class MediaPlayer extends JFrame {

	private EmbeddedMediaPlayer video;
	private ProgressSlider progress;
	private RewindSession rewinding;
	private String currentVideo;
	private String currentFestAudio;
	private String currentOwnAudio;
	private String videoPath;

	private JPanel mainPanel;
	private JPanel controlPanel;
	private MediaPlayer frame;
	private AudioConverter ac;
	private JButton playButton;
		
	private boolean isMerged = false;
	private boolean isPlaying = false;
	private boolean isVideo = false;
	private boolean audioIsFinished = true;
	private boolean audioIsFinished2 = true;
	
	private PlayAudio audioPlayerComponent;
	private PlayAudio audioPlayerComponent2;


	
	

	MediaPlayer() {
		setTitle("Vidivox Prototype");
		setSize(1024, 525);
		setLocation(200, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getRootPane().setBorder(BorderFactory.createMatteBorder(0, 3, 0, 3, Color.WHITE));

		frame = this;
		ac = new AudioConverter(this);
		progress = new ProgressSlider(this);
		playButton = new JButton("Play");

		// Timer to update progress bar
		int delay = 50;
		ActionListener updateSlider = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (frame.checkIfVideo(false)) {
					progress.setLength();
					progress.updateSlider(video.getTime());
				}
			}
		};

		Timer timer = new Timer(delay, updateSlider);
		timer.start();

	}

	public static void main(final String args[]) {

		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),
				"/Applications/vlc-2.0.0/VLC.app/Contents/MacOS/lib");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MediaPlayer mainFrame = new MediaPlayer();
				mainFrame.setVisible(true);
				mainFrame.attachMainPanel();
				mainFrame.constructControlPanel();
				mainFrame.attachControls();
				mainFrame.attachSidePanel();
				mainFrame.attachMediaPanel();

			}
		});

	}

	public void attachMainPanel() {

		// MAIN PANEL
		mainPanel = new JPanel();
		BorderLayout mainlayout = new BorderLayout(3, 0);
		mainPanel.setLayout(mainlayout);
		frame.setContentPane(mainPanel);
	}

	public void constructControlPanel() {
		playButton.setPreferredSize(new Dimension(100, 25));
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Check if video chosen
				if (!checkIfVideo(true)) {
					return;
				}
				
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
					playButton.setText("Pause");
					
					//Resume any audio being previewed currently that hasnt already completed
					if ((!audioIsFinished) && (audioPlayerComponent != null) && (audioPlayerComponent2 == null)) {
						audioPlayerComponent.play();
					}
					else if (audioPlayerComponent2 != null){
						if ((!audioIsFinished) && (audioIsFinished2)) {
							audioPlayerComponent.play();
						}
						else if ((audioIsFinished) && (!audioIsFinished2)){
							audioPlayerComponent2.play();
						}
						else if ((!audioIsFinished) && (!audioIsFinished2)){
							audioPlayerComponent.play();
							audioPlayerComponent2.play();
						}
					}
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
					playButton.setText("Play");
					
					//Pause any audio being previewed when the video is paused
					if ((!audioIsFinished) && (audioPlayerComponent != null) && (audioPlayerComponent2 == null)) {
						audioPlayerComponent.pause();
					}
					else if (audioPlayerComponent2 != null){
						if ((!audioIsFinished) && (audioIsFinished2)) {
							audioPlayerComponent.pause();
						}
						else if ((audioIsFinished) && (!audioIsFinished2)){
							audioPlayerComponent2.pause();
						}
						else if ((!audioIsFinished) && (!audioIsFinished2)){
							audioPlayerComponent.pause();
							audioPlayerComponent2.pause();
						}
					}
				}
			}

		});

		JButton rewindbutton = new JButton("<<");
		rewindbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Check if video chosen
				if (!checkIfVideo(true)) {
					return;
				}
				// Increase rewind if pressed again
				if (rewinding != null) {
					rewinding.increaseRate();
					return;
				}

				if(video.isPlaying()){
					video.pause();
				}
				isPlaying = false;
				playButton.setText("Play");

				// New BackgroundTask of Rewinding
				rewinding = new RewindSession(video);
				rewinding.execute();
			}

		});

		JButton forwardbutton = new JButton(">>");
		forwardbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Check if video chosen
				if (!checkIfVideo(true)) {
					return;
				}

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
				
				isPlaying = false;
				playButton.setText("Play");

			}

		});

		JButton stopbutton = new JButton("Stop");
		stopbutton.setPreferredSize(new Dimension(100, 25));
		stopbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Check if video chosen
				if (!checkIfVideo(true)) {
					return;
				}

				video.stop();
				playButton.setText("Play");
				isPlaying = false;
			}

		});

		controlPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		controlPanel.add(rewindbutton);
		controlPanel.add(playButton);
		controlPanel.add(stopbutton);
		controlPanel.add(forwardbutton);
	}

	public void attachControls() {

		JLabel volumeLabel = new JLabel("Volume:");
		final JSlider soundCtrl = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		soundCtrl.setMajorTickSpacing(25);
		soundCtrl.setPaintTicks(true);

		ChangeListener l = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				//Change volume on media player and audio preview if present
				video.setVolume(soundCtrl.getValue() * 2);
				if (!audioIsFinished && audioIsFinished2) {
					audioPlayerComponent.setVolume(soundCtrl.getValue() * 2);
				}
				else if (audioIsFinished && !audioIsFinished2) {
					audioPlayerComponent2.setVolume(soundCtrl.getValue() * 2);
				}
				else if (!audioIsFinished && !audioIsFinished2) {
					audioPlayerComponent.setVolume(soundCtrl.getValue() * 2);
					audioPlayerComponent2.setVolume(soundCtrl.getValue() * 2);
				}
			}
		};
		soundCtrl.addChangeListener(l);
		

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());

		JPanel soundpanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		soundpanel.add(soundCtrl);
		soundpanel.add(volumeLabel, FlowLayout.LEFT);

		southPanel.add(controlPanel, BorderLayout.WEST);
		southPanel.add(soundpanel, BorderLayout.EAST);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
	}

	public void attachSidePanel() {

		JButton button3 = new JButton("Select Video");
		button3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser fc = new FileChooser(frame);
				videoPath = fc.getPath();

				// Check if video is actually chosen
				if (videoPath == null || videoPath.length() == 0) {
					return;
				}
				playNewVideo(videoPath);
				
			}
		});

		//Selects audio which is previewed by default and is prepped to be merged/embedded within the video
		JButton button4 = new JButton("Pick Audio");
		button4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!checkIfVideo(true)) {
					return;
				}
				if (isPlaying) {
					video.pause();
					isPlaying = false;
					playButton.setText("Play");
				}

				new AddAudioFrame(frame);
				
			}
		});

		//Allows all audio to be stripped from the video, or removes the currently selected preview audio
		JButton button5 = new JButton("Strip Audio");
		button5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!checkIfVideo(true)) {
					return;
				}
				if (isPlaying) {
					video.pause();
					isPlaying = false;
					playButton.setText("Play");
				}
				new StripAudioFrame(frame);
			}

		});
		
		//Embeds selected audio tracks into the video file
		JButton button6 = new JButton("Merge Video and Audio");
		button6.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!checkIfVideo(true)) {
					return;
				} else if ((currentFestAudio == null) && (currentOwnAudio == null)) {
					//pause the video and preview audio if there is any
					if (isPlaying) {
						video.pause();
						isPlaying = false;
						playButton.setText("Play");
						if (!audioIsFinished && audioIsFinished2) {
							audioPlayerComponent.pause();
						}
						else if (audioIsFinished && !audioIsFinished2) {
							audioPlayerComponent2.pause();
						}
						else if (!audioIsFinished && !audioIsFinished2) {
							audioPlayerComponent.pause();
							audioPlayerComponent2.pause();
						}
					}
					//error pop up because no audio has been selected
					JOptionPane.showMessageDialog(frame, "Please select an audio file first");
					
				} else {
					//pauses the video and any preview audio if any
					if (isPlaying) {
						video.pause();
						isPlaying = false;
						playButton.setText("Play");
						if (!audioIsFinished && audioIsFinished2) {
							audioPlayerComponent.pause();
						}
						else if (audioIsFinished && !audioIsFinished2) {
							audioPlayerComponent2.pause();
						}
						else if (!audioIsFinished && !audioIsFinished2) {
							audioPlayerComponent.pause();
							audioPlayerComponent2.pause();
						}
					}
					
					//merges audio and plays the new video
					String mergedVideo = ac.embedAudio(currentVideo, currentFestAudio, currentOwnAudio);
					if (mergedVideo != null) {
						isMerged = true;
						playNewVideo(mergedVideo);
					}
				}
			}

		});

		JPanel rightpanel = new JPanel(new GridLayout(4, 1, 0, 3));
		rightpanel.add(button3);
		rightpanel.add(button4);
		rightpanel.add(button5);
		rightpanel.add(button6);

		mainPanel.add(rightpanel, BorderLayout.EAST);
	}

	public void attachMediaPanel() {
		// MEDIA COMPONENT
		JPanel mediaPanel = new JPanel(new BorderLayout());

		EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		video = mediaPlayerComponent.getMediaPlayer();

		mediaPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
		mediaPanel.add(progress, BorderLayout.SOUTH);
		mainPanel.add(mediaPanel, BorderLayout.CENTER);
	}

	// Checks if a video has been chosen to play by the user
	public boolean checkIfVideo(boolean display) {
		if (!isVideo) {
			if (display) {
				JOptionPane.showMessageDialog(frame, "Please select a video first.");
			}
			return false;
		} else {
			return true;
		}

	}

	// Called when a new video is selected to play and be the "Current Video"
	public void playNewVideo(String newvideo) {
		currentVideo = newvideo;

		// Set Progress bar
		progress.setVideo(video);
		progress.setValue(0);

		// Autoplay video
		video.playMedia(currentVideo);
		isPlaying = true;
		isVideo = true;
		playButton.setText("Pause");
		progress.setLength();
	}

	public void setFestAudio(String currentFestAudioIn) {
		currentFestAudio = currentFestAudioIn;
	}

	public void setOwnAudio(String currentOwnAudioIn) {
		currentOwnAudio = currentOwnAudioIn;
	}

	public void setIsMerged(Boolean b) {
		isMerged = b;
	}

	public boolean getIsMerged() {
		return isMerged;
	}

	public String getCurrentVideo() {
		return currentVideo;
	}

	public String getOriginalVideoPath() {
		return videoPath;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public JPanel getControlPanel() {
		return controlPanel;
	}
	
	public void setAudioPlayer1(PlayAudio audio) {
		audioPlayerComponent = audio;
	}
	
	public void setAudioPlayer2(PlayAudio audio) {
		audioPlayerComponent2 = audio;
	}

	public void setAudioIsFinished(boolean b) {
		audioIsFinished = b;
	}

	public void setAudioIsFinished2(boolean b) {
		audioIsFinished2 = b;
		
	}


}