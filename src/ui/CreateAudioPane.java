package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;

import audio.AudioConverter;
import ui.utils.DocumentLimit;
import ui.utils.VideoOptions;


public class CreateAudioPane extends JPanel{
	
	private AudioConverter ac;
	private JTextArea festivalTextBox;
	private DefaultStyledDocument customDoc;
	private JLabel wordCount;
	private String projectPath;
	private JComboBox<Object> voices;
	private JComboBox<Object> pitch;
	private JComboBox<Object> speed;
	
	CreateAudioPane(String projectPath, final AudioConverter ac, final VideoOptions vidOption, final MediaPlayer mainFrame) {
		this.projectPath = projectPath;
		this.ac = ac;
		
		setLayout(new BorderLayout());
		
		JPanel westButtons = new JPanel();
		westButtons.setLayout(new GridLayout(3, 1 , 0, 3));
		
		final JButton previewBtn = new JButton("Preview");
		previewBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (vidOption.getIsPlaying()) {
					vidOption.playBtnFuntionality();
					mainFrame.setPlayButton("Play");
				}
				String text = festivalTextBox.getText();
				String voiceChosen = voices.getSelectedItem().toString();
				String pitchChosen = pitch.getSelectedItem().toString();
				String speedChosen = speed.getSelectedItem().toString();

				if (!ac.getIsPreviewing()) {
					ac.convertToAudio(text, voiceChosen, pitchChosen, speedChosen);
				}
				else {
					ac.cancelPreview();
					ac.convertToAudio(text, voiceChosen, pitchChosen, speedChosen);
				}
			}
		});
		
		final JButton stopBtn = new JButton("Stop");
		stopBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ac.cancelPreview();
			}
		});
		
		final JButton clearBtn = new JButton("Clear");
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				festivalTextBox.setText("");
			}
		});
		
		westButtons.add(previewBtn);
		westButtons.add(stopBtn);
		westButtons.add(clearBtn);
		
		JPanel centreFunctions = new JPanel();
		centreFunctions.setLayout(new BorderLayout());
		JPanel txtAreaPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel heading = new JLabel("Enter text:");
		wordCount = new JLabel();
		//JText area for festival text
		festivalTextBox = new JTextArea();
		festivalTextBox.setPreferredSize(new Dimension(720, 150));
		
		if(festivalTextBox.getText().isEmpty()) {
			previewBtn.setEnabled(false);
			stopBtn.setEnabled(false);
			clearBtn.setEnabled(false);
		}
		
		//Document used to restrict characters entered into textbox
		customDoc = new DefaultStyledDocument();
		customDoc.setDocumentFilter(new DocumentLimit(250));
		customDoc.addDocumentListener(new DocumentListener(){
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (festivalTextBox.getText().length() > 0) {
					previewBtn.setEnabled(true);
					stopBtn.setEnabled(true);
					clearBtn.setEnabled(true);
				}
				updateCount();	
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (festivalTextBox.getText().length() == 0) {
					previewBtn.setEnabled(false);
					stopBtn.setEnabled(false);
					clearBtn.setEnabled(false);
				}
				updateCount();	
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateCount();
			}
		});
		festivalTextBox.setDocument(customDoc);
		updateCount();
		festivalTextBox.setLineWrap(true);
		
		txtAreaPane.add(festivalTextBox);
		
		JPanel eastButtons = new JPanel();
		eastButtons.setLayout(new GridLayout(4, 1, 2, 3));
		String[] voiceList = { "KAL", "RAB", "DON" };
		voices = new JComboBox<Object>(voiceList);
		String[] pitchList = { "90Hz", "Default Hz", "130Hz", "180Hz" }; 
		pitch = new JComboBox<Object>(pitchList);
		pitch.setSelectedIndex(1);
		String[] speedList = { "0.5x", "0.75x", "1.0x", "1.2x" };
		speed = new JComboBox<Object>(speedList);
		speed.setSelectedIndex(2);
		JButton defaultBtn = new JButton("Default");
		defaultBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				voices.setSelectedIndex(0);
				pitch.setSelectedIndex(1);
				speed.setSelectedIndex(2);
			}
		});
		
		eastButtons.add(voices);
		eastButtons.add(pitch);
		eastButtons.add(speed);
		eastButtons.add(defaultBtn);
		
		centreFunctions.add(heading, BorderLayout.NORTH);
		centreFunctions.add(txtAreaPane, BorderLayout.CENTER);
		centreFunctions.add(wordCount, BorderLayout.SOUTH);
		
		JPanel eastPanel = new JPanel(new BorderLayout(3, 0));
		JPanel crtBtnPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		//create button
		JButton createBtn = new JButton("Create");
		createBtn.setPreferredSize(new Dimension(100, 180));
		createBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = festivalTextBox.getText();
				String voiceChosen = voices.getSelectedItem().toString();
				String pitchChosen = pitch.getSelectedItem().toString();
				String speedChosen = speed.getSelectedItem().toString();
				Boolean successful = ac.convertToWav(text, voiceChosen, pitchChosen, speedChosen);
				if (successful) {
					ac.wavToMp3();
				}
			}
		});
		
		crtBtnPane.add(createBtn);
		eastPanel.add(eastButtons, BorderLayout.WEST);
		eastPanel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.CENTER);
		eastPanel.add(crtBtnPane, BorderLayout.EAST);
		
		add(westButtons, BorderLayout.WEST);
		add(centreFunctions, BorderLayout.CENTER);
		add(eastPanel, BorderLayout.EAST);
		
	}
	
	private void updateCount(){
		wordCount.setText((250-customDoc.getLength())+" characters remaining");
	}

}
