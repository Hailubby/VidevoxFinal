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

import audio.AddedAudio;
import audio.AudioConverter;
import audio.AudioConverterListener;
import audio.AudioPlayer;

public class AddAudioPane extends JPanel{
	
	private int volume;
	long totalMilliSeconds = 0;
	private JTextField audTxt;
	private String filePath;
	private AudioPlayer audioPlayer = new AudioPlayer();
	private int hour;
	private int min;
	private int sec;
	private AudioConverter ac;
	
	
	AddAudioPane(String projectPath, final AudioConverter ac) {
		this.ac = ac;
		
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
		final JTextField hours = new JTextField("00", JTextField.CENTER);
		hours.setPreferredSize(new Dimension(10, 25));
		final JTextField minutes = new JTextField("00", JTextField.CENTER);
		minutes.setPreferredSize(new Dimension(10,25));
		final JTextField seconds = new JTextField("00", JTextField.CENTER);
		seconds.setPreferredSize(new Dimension(10,25));
		JLabel separator1 = new JLabel(":");
		JLabel separator2 = new JLabel(":");
		JButton curTimeBtn = new JButton("Current Time");
		
		timePanel.add(Box.createRigidArea(new Dimension(5, 0)));
		timePanel.add(strtLbl);
		timePanel.add(Box.createRigidArea(new Dimension(5,0)));
		timePanel.add(hours);
		timePanel.add(separator1);
		timePanel.add(minutes);
		timePanel.add(separator2);
		timePanel.add(seconds);
		timePanel.add(Box.createRigidArea(new Dimension(175, 0)));
		timePanel.add(curTimeBtn);
		timePanel.add(Box.createRigidArea(new Dimension(4, 0)));
		
		northPanel.add(selAud, BorderLayout.NORTH);
		northPanel.add(audSndPanel, BorderLayout.CENTER);
		northPanel.add(timePanel, BorderLayout.SOUTH);
		
		JPanel southPanel = new JPanel(new BorderLayout());
		
		JPanel selAud2 = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
		JButton previewBtn = new JButton("Preview");
		previewBtn.setPreferredSize(new Dimension(100, 25));
		previewBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				audioPlayer.pause();
				audioPlayer = new AudioPlayer(filePath, volume);
			}
		});
		
		JButton stpBtn = new JButton("Stop");
		stpBtn.setPreferredSize(new Dimension(100, 25));
		stpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (audioPlayer != null) {
					audioPlayer.pause();
				}
			}
		});
		
		selAud2.add(previewBtn);
		selAud2.add(stpBtn);
		
		JPanel addAudPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton addBtn = new JButton("Add Audio");
		addBtn.setPreferredSize(new Dimension(350, 50));
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hour = Integer.parseInt(hours.getText());
				min = Integer.parseInt(minutes.getText());
				sec = Integer.parseInt(seconds.getText());
				
				totalMilliSeconds = 0;
				
				double v = ((double)volume)/50;
				
				if((hour == 0) && (min == 0) && (sec == 0)) {
					//create new audio file with selected volumed
					ac.progAudioNoDelay(filePath, v);
				}
				else {
					totalMilliSeconds += TimeUnit.HOURS.toMillis((long)hour);
					totalMilliSeconds += TimeUnit.MINUTES.toMillis((long)min);
					totalMilliSeconds += TimeUnit.SECONDS.toMillis((long)sec);
					
					ac.progAudioWithDelay(totalMilliSeconds, filePath, v);
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


}
