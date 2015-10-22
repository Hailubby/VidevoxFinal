package vidivox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class StripAudioFrame extends JFrame{
	
	StripAudioFrame(final MediaPlayer frame) {
		
		setTitle("Strip Audio");
		setSize(250, 110);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getRootPane().setBorder(BorderFactory.createMatteBorder(0, 3, 0, 3, Color.WHITE));
		setLocationRelativeTo(frame);
		
		final AudioConverter ac = new AudioConverter(this);
		
		final JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		botPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
		
		//sets default selected radio button to remove all audio option
		final JRadioButton rmAudio = new JRadioButton("Remove all audio", true);
		JRadioButton originalVidAudio = new JRadioButton("Remove added audio", false);
		
		buttonPanel.add(rmAudio);
		buttonPanel.add(originalVidAudio);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rmAudio);
		group.add(originalVidAudio);
		
		mainPanel.add(buttonPanel, BorderLayout.NORTH);
		
		JButton okBtn = new JButton("Ok");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (rmAudio.isSelected()) {
					//Checks if video has audio to remove
					if ((ac.isAudioless(frame.getCurrentVideo()) == null) || (ac.isAudioless(frame.getCurrentVideo()).equals(""))){
						JOptionPane.showMessageDialog(frame, "There is no audio available to be stripped");
					}
					else {
					String output = JOptionPane.showInputDialog(frame, "Name the audioless video, please include the current extension");
						
						File f = new File("CustomVidivoxFolder/"+output);
						
						//Ensures output filename is valid
						if (output == null) {
							return;
						} else if (output.length() == 0) {
							JOptionPane.showMessageDialog(frame, "No name was selected");
						} else if (f.exists()){
							JOptionPane.showMessageDialog(frame, "This file already exists.");
						} else {
							//Strips the audio and autoplays audioless video
							ac.removeAudio(frame.getCurrentVideo(), output);
							frame.playNewVideo("CustomVidivoxFolder/"+output);
						}
						
						dispose();
						}
				}
				else {
					//Reverts to video selection before merging
					if(frame.getIsMerged() == true) {
						File file = new File(frame.getCurrentVideo());
						file.delete();
						frame.playNewVideo(frame.getOriginalVideoPath());
						dispose();
					}
					else {
						JOptionPane.showMessageDialog(frame,"There has been no new audio merged with the original video.");
					}
				}
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
		
		mainPanel.add(botPanel, BorderLayout.SOUTH);
		
		add(mainPanel);
		setVisible(true);
	}
}
