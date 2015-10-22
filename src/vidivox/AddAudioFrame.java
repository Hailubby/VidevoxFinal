package vidivox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;

@SuppressWarnings("serial")
public class AddAudioFrame extends JFrame {

	private String fAudio;
	private String ownAudio;

	private DefaultStyledDocument customDoc;
	
	//Components
	private PlayAudio audioPlayerComponent;
	private PlayAudio audioPlayerComponent2;
	private JLabel remainingCharacters;

	
	AddAudioFrame(final MediaPlayer frame) {
		final Font defaultFONT = new JLabel().getFont();
		String defaultFont = defaultFONT.getName();
		
		//audio player components to preview the audio with video
		audioPlayerComponent = new PlayAudio(frame, false);
		audioPlayerComponent2 = new PlayAudio(frame, true);

		setTitle("Add Audio");
		setSize(540, 320);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getRootPane().setBorder(BorderFactory.createMatteBorder(0, 3, 0, 3, Color.WHITE));
		setLocationRelativeTo(frame);

		final JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
		final AudioConverter ac = new AudioConverter(this);
		remainingCharacters = new JLabel();

		JPanel topPanel = new JPanel(new BorderLayout(0, 5));
		topPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

		JPanel centreTopPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel centrePanel = new JPanel(new BorderLayout(0, 5));

		JPanel botCentPanel = new JPanel(new BorderLayout(10, 10));
		botCentPanel.setBorder(new EmptyBorder(3, 10, 3, 10));

		JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		botPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

		JLabel titleLabel = new JLabel("Add existing MP3");
		titleLabel.setFont(new Font(defaultFont, Font.BOLD, 20));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel label = new JLabel("Select audio file:");
		final JTextField textBox = new JTextField();
		textBox.setPreferredSize(new Dimension(300, 25));
		;
		final JButton searchBtn = new JButton("Browse");
		searchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser fc = new FileChooser(searchBtn.getParent());
				textBox.setText(fc.getPath());
			}
		});

		centreTopPanel.add(label);
		centreTopPanel.add(textBox);
		centreTopPanel.add(searchBtn);

		topPanel.add(titleLabel, BorderLayout.CENTER);
		topPanel.add(centreTopPanel, BorderLayout.SOUTH);

		JLabel titleLabel2 = new JLabel("Create custom audio");
		titleLabel2.setFont(new Font(defaultFont, Font.BOLD, 20));
		titleLabel2.setHorizontalAlignment(SwingConstants.CENTER);

		final JTextArea textBox2 = new JTextArea();
		
		//Document used to restrict characters entered into textbox
		customDoc = new DefaultStyledDocument();
		customDoc.setDocumentFilter(new DocumentLimit(250));
		customDoc.addDocumentListener(new DocumentListener(){

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateCount();
				
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateCount();
				
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateCount();
				
			}
			
		});
		textBox2.setDocument(customDoc);
		updateCount();

		textBox2.setLineWrap(true);
		textBox2.setPreferredSize(new Dimension(350, 75));

		//Festival reads out provided text as a preview
		JButton previewBtn = new JButton("Preview");

		previewBtn.setPreferredSize(new Dimension(100, 25));
		previewBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = textBox2.getText();

				if (text.isEmpty()) {
					JOptionPane.showMessageDialog(mainPanel, "Text box has been left blank", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					ac.convertToAudio(text);
				}
			}
		});
		botCentPanel.add(remainingCharacters,BorderLayout.CENTER);
		botCentPanel.add(textBox2, BorderLayout.NORTH);
		botCentPanel.add(previewBtn, BorderLayout.SOUTH);

		centrePanel.add(titleLabel2, BorderLayout.NORTH);
		centrePanel.add(botCentPanel, BorderLayout.CENTER);

		//Prepares the selected audio track or tracks for previewing
		JButton okBtn = new JButton("Ok");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String festivalText = textBox2.getText();
				String audioText = textBox.getText();
				if (festivalText.isEmpty() && audioText.isEmpty()) {
					JOptionPane.showMessageDialog(mainPanel, "Please select an audio file or enter text to convert.",
							"Error", JOptionPane.ERROR);
				} else if (festivalText.isEmpty()) {
					// get audio file path
					ownAudio = audioText;
					frame.setOwnAudio(ownAudio);
					frame.setAudioPlayer1(audioPlayerComponent);
					//Start up preview of audio track alongside video
					audioPlayerComponent.start(ownAudio+"");

				} else if (audioText.isEmpty()) {
					// convert festival text to wav file
					// get wav file path
					ac.convertToWav(festivalText);
					ac.wavToMp3();
					fAudio = ac.getFileName() + ".mp3";
					frame.setFestAudio(fAudio);
					frame.setAudioPlayer1(audioPlayerComponent);
					//Start up preview of audio track alongside video
					audioPlayerComponent.start(fAudio+"");

				} else {
					// Select between file paths
					Object[] options = { "Chosen file", "Custom audio", "Use both" };
					int n = JOptionPane.showOptionDialog(mainPanel,
							"Would you like the file you've chosen or the custom audio?",
							"You have chosen two types of audio", JOptionPane.DEFAULT_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
					if (n == 0) {
						ownAudio = audioText;
						frame.setOwnAudio(ownAudio);
						frame.setAudioPlayer1(audioPlayerComponent);
						audioPlayerComponent.start(ownAudio+"");
					} else if (n == 1) {
						ac.convertToWav(festivalText);
						ac.wavToMp3();
						fAudio = ac.getFileName() + ".mp3";
						frame.setFestAudio(fAudio);
						frame.setAudioPlayer1(audioPlayerComponent);
						audioPlayerComponent.start(fAudio+"");
					} else if (n == 2) {
						ownAudio = audioText;
						frame.setOwnAudio(ownAudio);
						
						ac.convertToWav(festivalText);
						ac.wavToMp3();
						fAudio = ac.getFileName() + ".mp3";
						frame.setFestAudio(fAudio);
						
						frame.setAudioPlayer1(audioPlayerComponent);
						frame.setAudioPlayer2(audioPlayerComponent2);
						frame.setAudioIsFinished2(false);
						
						audioPlayerComponent.start(fAudio+"");
						audioPlayerComponent2.start(ownAudio+"");
					}else {
						JOptionPane.showMessageDialog(mainPanel, "Error: Audio selection failed, try again");
					}

				}
				
				//sets ismerged to false, new audio has been added but not merged
				frame.setIsMerged(false);
				//new audio started preview, hasnt finished playing
				frame.setAudioIsFinished(false);
				frame.playNewVideo(frame.getCurrentVideo());
				dispose();
			}
		});
		okBtn.setPreferredSize(new Dimension(100, 25));

		JButton cnclBtn = new JButton("Cancel");
		cnclBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		cnclBtn.setPreferredSize(new Dimension(100, 25));

		botPanel.add(okBtn);
		botPanel.add(cnclBtn);

		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(centrePanel, BorderLayout.CENTER);
		mainPanel.add(botPanel, BorderLayout.SOUTH);

		add(mainPanel);

		setVisible(true);
	}
	

	public PlayAudio getAudioPlayer1() {
		return audioPlayerComponent;
	}
	
	public PlayAudio getAudioPlayer2() {
		return audioPlayerComponent2;
	}

	private void updateCount(){
		remainingCharacters.setText((250-customDoc.getLength())+" characters remaining");
	}

}
