package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import audio.AudioConverter;
import audio.AudioConverterListener;
import audio.AudioPlayer;
import ui.utils.VideoOptions;

public class AddAudioPane extends JPanel{
	
	private int volume;
	long totalMilliSeconds = 0;
	private JTextField audTxt;
	private JButton previewBtn;
	private JButton stpBtn;
	private String filePath;
	private AudioPlayer audioPlayer = new AudioPlayer();
	private int min;
	private int sec;
	private VideoOptions vidOption;
	private MediaPlayer mainFrame;
	private AudioConverter ac;
	
	
	AddAudioPane(String projectPath, final AudioConverter ac, final VideoOptions vidOption, final MediaPlayer mainFrame) {
		this.ac = ac;
		this.vidOption = vidOption;
		this.mainFrame = mainFrame;
		
		setLayout(new BorderLayout());
		
		JPanel projectPanel = new JPanel();
		projectPanel.setLayout(new BorderLayout());
		
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new BorderLayout());
		
		JPanel tblPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		JLabel heading1 = new JLabel("Project audio files:");
		//set up audio table
		final JTable audioTable = new JTable();
		final AudioTableModel tableModel = new AudioTableModel(projectPath);
		audioTable.setModel(tableModel);
		ac.addListener(new AudioConverterListener() {

			@Override
			public void addedAudioListChanged() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void audioFileCreated(String fileName, String filePath) {
				tableModel.addAudioFile(fileName, filePath);
			}
			
		});
		audioTable.setCellSelectionEnabled(true);
		final ListSelectionModel cellSelectionModel = audioTable.getSelectionModel();
		cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (audioTable.getSelectedRow() > -1) {
					Object key = audioTable.getValueAt(audioTable.getSelectedRow(), 0);
					filePath = ((AudioTableModel)audioTable.getModel()).getFilePath(key.toString());	
					audTxt.setText(filePath);
					previewBtn.setEnabled(true);
					stpBtn.setEnabled(true);
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(400, 150));
		audioTable.setFillsViewportHeight(true);
		scrollPane.setViewportView(audioTable);
		
		tblPanel.add(scrollPane);
		
		projectPanel.add(heading1, BorderLayout.NORTH);
		projectPanel.add(tblPanel, BorderLayout.CENTER);
		
		JPanel browsePanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 55));
		JLabel orLbl = new JLabel("Or");
		//browse button
		JButton browseBtn = new JButton("Browse");
		browseBtn.setPreferredSize(new Dimension(100,25));
		browseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser fc = new FileChooser(null, "AUDIO");
				filePath = fc.getPath();
				if (!filePath.isEmpty()) {
					audTxt.setText(filePath);
					previewBtn.setEnabled(true);
					stpBtn.setEnabled(true);
					audioTable.clearSelection();
				}
			}
		});

		browsePanel.add(orLbl);
		browsePanel.add(browseBtn);
		
		westPanel.add(projectPanel, BorderLayout.WEST);
		westPanel.add(browsePanel, BorderLayout.EAST);
		
		JPanel eastPanel = new JPanel(new BorderLayout());
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setPreferredSize(new Dimension(455, 100));
		
		JPanel selAud = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
		JLabel selAudLbl = new JLabel("Selected Audio");
		audTxt = new JTextField();
		audTxt.setEditable(false);
		audTxt.setPreferredSize(new Dimension(330, 25));
		
		selAud.add(selAudLbl);
		selAud.add(audTxt);
		
		JLabel volumeLbl = new JLabel("Audio Volume");
		final JSlider soundCtrl = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		soundCtrl.setPreferredSize(new Dimension(330, 50));
		volume = soundCtrl.getValue();

		ChangeListener l = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				//Change volume on media player and audio preview if present
				volume = soundCtrl.getValue();
				if (audioPlayer != null) {
					audioPlayer.setVolume(volume);
				}
			}
		};
		soundCtrl.addChangeListener(l);
		
		JPanel audSndPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 7, 0));
		audSndPanel.add(volumeLbl);
		audSndPanel.add(soundCtrl);
		
		JPanel timePanel = new JPanel(); 
		timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));
		JLabel strtLbl = new JLabel ("Start Time");
		final JTextField minutes = new JTextField("00", JTextField.CENTER);
		minutes.setPreferredSize(new Dimension(7,25));
		minutes.setToolTipText("minutes");
		final JTextField seconds = new JTextField("00", JTextField.CENTER);
		seconds.setPreferredSize(new Dimension(7,25));
		seconds.setToolTipText("seconds");
		JLabel separator1 = new JLabel(":");
		JButton curTimeBtn = new JButton("Current Time");
		curTimeBtn.setToolTipText("Inserts the current time into the delay boxes");
		curTimeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] currentTime = getCurrentTime().split(":");
				minutes.setText(currentTime[0]);
				seconds.setText(currentTime[1]);
			}
		});
		
		timePanel.add(Box.createRigidArea(new Dimension(5, 0)));
		timePanel.add(strtLbl);
		timePanel.add(Box.createRigidArea(new Dimension(5,0)));
		timePanel.add(minutes);
		timePanel.add(separator1);
		timePanel.add(seconds);
		timePanel.add(Box.createRigidArea(new Dimension(200, 0)));
		timePanel.add(curTimeBtn);
		timePanel.add(Box.createRigidArea(new Dimension(4, 0)));
		
		northPanel.add(selAud, BorderLayout.NORTH);
		northPanel.add(audSndPanel, BorderLayout.CENTER);
		northPanel.add(timePanel, BorderLayout.SOUTH);
		
		JPanel southPanel = new JPanel(new BorderLayout());
		
		JPanel selAud2 = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
		previewBtn = new JButton("Preview");
		previewBtn.setPreferredSize(new Dimension(100, 25));
		previewBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (vidOption.getIsPlaying()) {
					vidOption.playBtnFuntionality();
					mainFrame.setPlayButton("Play");
				}
				volume = soundCtrl.getValue();
				audioPlayer.stop();
				audioPlayer = new AudioPlayer(filePath);
				try {
					Thread.sleep(100);
					audioPlayer.setVolume(soundCtrl.getValue());
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		previewBtn.setEnabled(false);
		
		stpBtn = new JButton("Stop");
		stpBtn.setPreferredSize(new Dimension(100, 25));
		stpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (audioPlayer != null) {
					audioPlayer.pause();
				}
			}
		});
		stpBtn.setEnabled(false);
		
		selAud2.add(previewBtn);
		selAud2.add(stpBtn);
		
		JPanel addAudPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton addBtn = new JButton("Add Audio");
		addBtn.setPreferredSize(new Dimension(350, 50));
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((audTxt.getText() != null) && !audTxt.getText().isEmpty()){
					try {
						min = Integer.parseInt(minutes.getText());
						sec = Integer.parseInt(seconds.getText());
						
						totalMilliSeconds = 0;
						volume = soundCtrl.getValue();
						
						double v = ((double)volume)/50;
						
						if((min == 0) && (sec == 0)) {
							//create new audio file with selected volumed
							ac.progAudioNoDelay(filePath, v);
						}
						else {
							totalMilliSeconds += TimeUnit.MINUTES.toMillis((long)min);
							totalMilliSeconds += TimeUnit.SECONDS.toMillis((long)sec);
							
							ac.progAudioWithDelay(totalMilliSeconds, filePath, v);
						}
						ac.setIsExported(false);
					} catch(NumberFormatException e1) {
						JOptionPane.showMessageDialog(mainFrame, "Invalid number format. It should be xx:xx where x is a number 0-9", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}

			}
		});
		
		addAudPanel.add(addBtn);
		
		southPanel.add(selAud2, BorderLayout.CENTER);
		southPanel.add(addAudPanel, BorderLayout.SOUTH);
		
		eastPanel.add(northPanel, BorderLayout.NORTH);
		eastPanel.add(southPanel, BorderLayout.SOUTH);

		add(westPanel, BorderLayout.WEST);
		add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.CENTER);
		add(eastPanel, BorderLayout.EAST);
		
	}
	
	private String getCurrentTime() {
		return vidOption.timeOfVid(mainFrame.getVideoPlayer().getTime());
	}


}
