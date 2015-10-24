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
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
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
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class MediaPlayer extends JFrame{
	
	static MediaPlayer mainFrame;
	private EmbeddedMediaPlayer video;
	private ProgressSlider progress;
	private Menu menuBar;
	private VideoOptions vidOption;
	private AudioConverter ac;
	
	private JPanel mainPanel;
	private JPanel controlPanel;
	private JPanel eastVidControlsPanel;
	private ProjectPane projectPane;
	private JTabbedPane audioTabPane;
	private JButton playButton;
	private JButton hideBtn;
	private JLabel timeLbl1;
	private JLabel timeLbl2;
	
	private String projectPath;
	private String videoPath;
	private String currentVideo;
	private boolean isHidden = true;
	private boolean vidSelected = false;
	
	public MediaPlayer(String projectPath) {
		ac = new AudioConverter(projectPath);
		
		setTitle("Vidivox");
		setSize(1024, 735);
		setLocation(200, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getRootPane().setBorder(BorderFactory.createMatteBorder(0, 3, 0, 3, Color.WHITE));
		
		mainFrame = this;
		mainFrame.setResizable(false);
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
		vidOption = new VideoOptions(video);
		
		// Timer to update progress bar
		int delay = 50;
		ActionListener updateSlider = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (vidSelected) {
					if (!isHidden) {
						audioTabPane.setVisible(true);
						projectPane.setVisible(true);
					}
					progress.setLength();
					progress.updateSlider(video.getTime());
					if (video.getTime() <= 250) {
						video.mute(false);
					}
					timeLbl1.setText(vidOption.timeOfVid(video.getTime()));
					timeLbl2.setText(vidOption.timeOfVid(video.getLength()));
				} else {
					audioTabPane.setVisible(false);
					projectPane.setVisible(false);
				}
			}
		};

		Timer timer = new Timer(delay, updateSlider);
		timer.start();
	}
	
	protected void mainFrameWindowStateChanged(WindowEvent e) {
		//minimized
		if ((e.getNewState() & mainFrame.MAXIMIZED_BOTH) == mainFrame.MAXIMIZED_BOTH) {
			hideBtn.setEnabled(false);
		}
		else {
			hideBtn.setEnabled(true);
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
				mainFrame.eastVideoControls();
				mainFrame.constructAudioPanels();
				mainFrame.attachSouthPanels();
				mainFrame.attachProjectPane();
				mainFrame.attachMediaPanel();
				mainFrame.setVisible(true);
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
				vidOption.playBtnFuntionality();
				if (vidOption.getIsPlaying()) {
					if (!vidOption.getPreviewIsFinished() && (projectPane.getPreviewPlayer() != null)) {
						projectPane.playPreview();
					}
					playButton.setText("Pause");
				} else {
					if (!vidOption.getPreviewIsFinished() && (projectPane.getPreviewPlayer() != null)) {
						projectPane.pausePreview();
					}
					playButton.setText("Play");
				}
			}
		});
		
		//Rewind button
		JButton rewindBtn = new JButton("<<");
		rewindBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vidOption.rewindBtnFunctionality();
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
				vidOption.forwardBtnFunctionality();
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
				vidOption.stopBtnFunctionality();
				if (projectPane.getPreviewPlayer() != null) {
					projectPane.stopPreview();
					vidOption.setPreviewIsFinished(false);
				}
				playButton.setText("Play");
			}
		});
		
		controlPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		controlPanel.add(rewindBtn);
		controlPanel.add(playButton);
		controlPanel.add(stopBtn);
		controlPanel.add(forwardBtn);
	}
	
	//Volume control
	public void eastVideoControls() {

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
				boolean mutted = vidOption.muteBtnFunctionality();
				if (!mutted) {
					soundCtrl.setEnabled(false);
					muteBtn.setText("Unmute");
				} else {
					soundCtrl.setEnabled(true);
					muteBtn.setText("Mute");
				}
			}
		});
		
		hideBtn = new JButton("Show");
		hideBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isHidden) {
					audioTabPane.setVisible(false);
					projectPane.setVisible(false);
					mainFrame.setResizable(false);
					isHidden = true;
					hideBtn.setText("Show");
				}
				else {
					audioTabPane.setVisible(true);
					projectPane.setVisible(true);
					mainFrame.setResizable(false);
					isHidden = false;
					hideBtn.setText("Hide");
				}
			}
		});
		
		JSeparator separator= new JSeparator(SwingConstants.VERTICAL);
		Dimension d = separator.getPreferredSize();
		d.height = hideBtn.getPreferredSize().height;
		separator.setPreferredSize(d);
		
		eastVidControlsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		eastVidControlsPanel.add(volumeLabel);
		eastVidControlsPanel.add(soundCtrl);
		eastVidControlsPanel.add(muteBtn);
		eastVidControlsPanel.add(separator);
		eastVidControlsPanel.add(hideBtn);
	}
	
	//mp3 creation + add audio tabbed pane panel
	//create south panel, this goes south of south panel
	public void constructAudioPanels() {
		audioTabPane = new JTabbedPane();
		audioTabPane.add("Create Audio", new CreateAudioPane(projectPath, ac));
		audioTabPane.add("Add Audio", new AddAudioPane(projectPath, ac, vidOption, mainFrame));
	}
	
	public void attachSouthPanels() {
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		
		southPanel.add(controlPanel, BorderLayout.WEST);
		southPanel.add(eastVidControlsPanel, BorderLayout.EAST);
		southPanel.add(audioTabPane, BorderLayout.SOUTH);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
	}
	
	//Project side panel
	public void attachProjectPane() {
		projectPane = new ProjectPane(projectPath,ac, vidOption, mainFrame);
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
				if (fc.getPath().isEmpty() || (fc.getPath() == null)) {
					vidSelected = false;
				}else {
					curVid.setText("" + fc.getFileName());
					videoPath = fc.getPath();
					ac.setVideoPath(videoPath);
					vidOption.playNewVideo(videoPath, video, currentVideo, progress);
					vidSelected = true;
					if (vidOption.getIsPlaying()) {
						playButton.setText("Pause");
					}
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
		video.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			 @Override
			 public void finished(uk.co.caprica.vlcj.player.MediaPlayer mediaPlayer) {
				 vidOption.setIsPlaying(false);
				 playButton.setText("Play");
			 }
		 });
		
		JPanel progressPane = new JPanel(new BorderLayout());
		timeLbl1 = new JLabel("00:00");
		timeLbl2 = new JLabel("00:00");
		progressPane.add(timeLbl1, BorderLayout.WEST);
		progressPane.add(progress, BorderLayout.CENTER);
		progressPane.add(timeLbl2, BorderLayout.EAST);
		
		mediaPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
		mediaPanel.add(progressPane, BorderLayout.SOUTH);
		mainPanel.add(mediaPanel, BorderLayout.CENTER);
	}
	
	public void setPlayButton() {
		playButton.setText("Pause");
	}
	
	public EmbeddedMediaPlayer getVideoPlayer() {
		return video;
	}

}
