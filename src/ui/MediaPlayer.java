package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import audio.AudioConverter;
import ui.utils.ProgressSlider;
import ui.utils.VideoOptions;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class MediaPlayer extends JFrame{
	
	static MediaPlayer mainFrame;
	private EmbeddedMediaPlayer video;
	private ProgressSlider progress;
	private Menu menuBar;
	private VideoOptions vidOption = new VideoOptions();
	private AudioConverter ac;
	
	private JPanel mainPanel;
	private JPanel controlPanel;
	private JPanel soundPanel;
	private ProjectPane projectPane;
	private JTabbedPane audioTabPane;
	private JButton playButton;
	
	private String projectPath;
	private String videoPath;
	private String currentVideo;

	
	
	public MediaPlayer(String projectPath) {
		ac = new AudioConverter(projectPath);
		
		setTitle("Vidivox");
		setSize(1024, 735);
		setLocation(200, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getRootPane().setBorder(BorderFactory.createMatteBorder(0, 3, 0, 3, Color.WHITE));
		
		mainFrame = this;
		progress = new ProgressSlider(this);
		menuBar = new Menu(this);
		this.projectPath = projectPath;
		playButton = new JButton("Play");
		
		mainFrame.addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				mainFrameWindowStateChanged(e);
			}
			
		});
		
		mainSetUp();
		
		// Timer to update progress bar
		int delay = 50;
		ActionListener updateSlider = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (vidOption.getIsVideo()) {
					progress.setLength();
					progress.updateSlider(video.getTime());
					if (video.getTime() <= 250) {
						video.mute(false);
					}
				}
			}
		};

		Timer timer = new Timer(delay, updateSlider);
		timer.start();
	}
	
	protected void mainFrameWindowStateChanged(WindowEvent e) {
		//minimized
		if ((e.getNewState() & mainFrame.MAXIMIZED_BOTH) == mainFrame.MAXIMIZED_BOTH) {
			audioTabPane.setVisible(false);
			projectPane.setVisible(false);
		}
		else {
			audioTabPane.setVisible(true);
			projectPane.setVisible(true);
		}
	}

	public static void mainSetUp() {

		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),
				"/Applications/vlc-2.0.0/VLC.app/Contents/MacOS/lib");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainFrame.attachMainPanel();
				mainFrame.attachSelectVidPane();
				mainFrame.constructControlPanel();
				mainFrame.constructVolumeControls();
				mainFrame.constructAudioPanels();
				mainFrame.attachSouthPanels();
				mainFrame.attachProjectPane();
				mainFrame.attachMediaPanel();
				mainFrame.setVisible(true);
				mainFrame.pack();
			}
		});

	}
	
	public void attachMainPanel() {

		// MAIN PANEL
		mainPanel = new JPanel(new BorderLayout(3,0));
		mainFrame.setContentPane(mainPanel);
	}
	
	//Play pause buttons etc
	public void constructControlPanel() {
		playButton.setPreferredSize(new Dimension(100, 25));
		
		//Play button action listener
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vidOption.playBtnFuntionality(video);
				if (vidOption.getIsPlaying()) {
					playButton.setText("Pause");
				} else {
					playButton.setText("Play");
				}
			}
		});
		
		//Rewind button
		JButton rewindBtn = new JButton("<<");
		rewindBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vidOption.rewindBtnFunctionality(video);
				if (vidOption.getIsPlaying()) {
					playButton.setText("Pause");
				} else {
					playButton.setText("Play");
				}
			}
		});
		
		//fast forward button
		JButton forwardBtn = new JButton(">>");
		forwardBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vidOption.forwardBtnFunctionality(video);
				if (vidOption.getIsPlaying()) {
					playButton.setText("Pause");
				} else {
					playButton.setText("Play");
				}
			}
		});
		
		//stop button
		JButton stopBtn = new JButton("Stop");
		stopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vidOption.stopBtnFunctionality(video);
				if (vidOption.getIsPlaying()) {
					playButton.setText("Pause");
				} else {
					playButton.setText("Play");
				}
			}
		});
		
		controlPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		controlPanel.add(rewindBtn);
		controlPanel.add(playButton);
		controlPanel.add(stopBtn);
		controlPanel.add(forwardBtn);
	}
	
	//Volume control
	public void constructVolumeControls() {

		JLabel volumeLabel = new JLabel("Volume:");
		final JSlider soundCtrl = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		soundCtrl.setMajorTickSpacing(25);
		soundCtrl.setPaintTicks(true);

		ChangeListener l = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				vidOption.volCtrlFuntionality(video, soundCtrl);
			}
		};
		soundCtrl.addChangeListener(l);
		
		final JButton muteBtn = new JButton("Mute");
		muteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean mutted = vidOption.muteBtnFunctionality(video);
				if (!mutted) {
					soundCtrl.setEnabled(false);
					muteBtn.setText("Unmute");
				} else {
					soundCtrl.setEnabled(true);
					muteBtn.setText("Mute");
				}
			}
		});
		
		soundPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		soundPanel.add(volumeLabel);
		soundPanel.add(soundCtrl);
		soundPanel.add(muteBtn);
	}
	
	//mp3 creation + add audio tabbed pane panel
	//create south panel, this goes south of south panel
	public void constructAudioPanels() {
		audioTabPane = new JTabbedPane();
		
		audioTabPane.add("Create Audio", new CreateAudioPane(projectPath, ac));
		audioTabPane.add("Add Audio", new AddAudioPane(projectPath, ac));
	}
	
	public void attachSouthPanels() {
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		
		southPanel.add(controlPanel, BorderLayout.WEST);
		southPanel.add(soundPanel, BorderLayout.EAST);
		southPanel.add(audioTabPane, BorderLayout.SOUTH);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
	}
	
	//Project side panel
	public void attachProjectPane() {
		projectPane = new ProjectPane(projectPath,ac);
		mainPanel.add(projectPane, BorderLayout.EAST);
	}
	
	public void attachSelectVidPane() {
		JPanel slctVidPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		final JLabel curVid = new JLabel("No video is selected");
		//Autoplays selected video
		JButton slctVidBtn = new JButton("Select Video");
		slctVidBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser fc = new FileChooser(mainFrame, "VIDEO");
				curVid.setText("" + fc.getFileName());
				videoPath = fc.getPath();
				ac.setVideoPath(videoPath);
				vidOption.playNewVideo(videoPath, video, currentVideo, progress);
				if (vidOption.getIsPlaying()) {
					playButton.setText("Pause");
				}
			}
		});
		
		slctVidPane.add(slctVidBtn);
		slctVidPane.add(curVid);
		
		mainPanel.add(slctVidPane, BorderLayout.NORTH);
	}
	
	
	//attach vidoe player + progress bar
	public void attachMediaPanel() {
		// MEDIA COMPONENT
		JPanel mediaPanel = new JPanel(new BorderLayout());

		EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		video = mediaPlayerComponent.getMediaPlayer();
		
		JPanel progressPane = new JPanel(new BorderLayout());
		JLabel timeLbl1 = new JLabel("0:00");
		JLabel timeLbl2 = new JLabel("0:00");
		progressPane.add(timeLbl1, BorderLayout.WEST);
		progressPane.add(progress, BorderLayout.CENTER);
		progressPane.add(timeLbl2, BorderLayout.EAST);
		
		mediaPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
		mediaPanel.add(progressPane, BorderLayout.SOUTH);
		mainPanel.add(mediaPanel, BorderLayout.CENTER);
	}

}
