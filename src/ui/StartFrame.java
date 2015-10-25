package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class StartFrame extends JFrame{
	
	StartFrame frame;
	private FileChooser fc;
	private JPanel mainPanel;
	private JTextField workSpaceText;
	private JTextField projNameText;
	
	private File videoFolder;
	private File audioFolder;
	private File projectAudioFolder;
	private File projectFolder;
	
	private JTextField projectPathTxt;
	private String projectPath;
	private JRadioButton existingWrkSpace;
	private JRadioButton newWrkSpace;
	private JButton browseBtn1;
	private JButton browseBtn2;
	private ButtonGroup group = new ButtonGroup();
	
	StartFrame() {
		setTitle("Select Workspace");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(555, 175);
		setLocation(200,200);
		getRootPane().setBorder(BorderFactory.createMatteBorder(0, 3, 0, 3, Color.WHITE));
		
		frame = this;
	}
	
	public static void main(String[] args) {
		
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, fall back to cross-platform
		    try {
		        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		    } catch (Exception ex) {
		        ex.printStackTrace();
		    }
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartFrame mainFrame = new StartFrame();
					mainFrame.attachMainPanel();
					mainFrame.attachExistingWorkPanel();
					mainFrame.attachNewWorkPane();
					mainFrame.attachBtnPanel();
					mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void attachMainPanel() {

		// MAIN PANEL
		mainPanel = new JPanel(new BorderLayout());
		frame.setContentPane(mainPanel);
	}

	public void attachExistingWorkPanel() {
		JPanel existProjectPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		JLabel existingProject = new JLabel("Open existing project: ");
		projectPathTxt = new JTextField("");
		projectPathTxt.setPreferredSize(new Dimension(290, 25));
		
		browseBtn1 = new JButton("Browse");
		browseBtn1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fc = new FileChooser(frame, "FOLDERS");
				projectPathTxt.setText(fc.getPath());
			}
		});
		
		existingWrkSpace = new JRadioButton("", true);
		group.add(existingWrkSpace);
		existingWrkSpace.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				projectPathTxt.setEnabled(true);
				browseBtn1.setEnabled(true);
				workSpaceText.setEnabled(false);
				browseBtn2.setEnabled(false);
				projNameText.setEnabled(false);
			}
			
		});
		
		existProjectPane.add(existingProject);
		existProjectPane.add(projectPathTxt);
		existProjectPane.add(browseBtn1);
		existProjectPane.add(existingWrkSpace);
		
		mainPanel.add(existProjectPane, BorderLayout.NORTH);
	}
	
	public void attachNewWorkPane() {
		JPanel newWorkPane = new JPanel(new BorderLayout());
		
		JPanel topPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel midPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel botPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		JLabel orLbl = new JLabel("Or");
		topPane.add(orLbl);
		
		JLabel slctWrkSpcLbl = new JLabel("Select workspace: ");
		workSpaceText = new JTextField("");
		workSpaceText.setPreferredSize(new Dimension(314, 25));
		workSpaceText.setEnabled(false);
		
		browseBtn2 = new JButton("Browse");
		browseBtn2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fc = new FileChooser(frame, "FOLDERS");
				workSpaceText.setText(fc.getPath());
			}
		});
		browseBtn2.setEnabled(false);
		
		newWrkSpace = new JRadioButton("", false);
		group.add(newWrkSpace);
		newWrkSpace.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				projectPathTxt.setEnabled(false);
				browseBtn1.setEnabled(false);
				workSpaceText.setEnabled(true);
				browseBtn2.setEnabled(true);
				projNameText.setEnabled(true);
			}
			
		});
		
		midPane.add(slctWrkSpcLbl);
		midPane.add(workSpaceText);
		midPane.add(browseBtn2);
		midPane.add(newWrkSpace);
		
		JLabel projectNameLbl = new JLabel("Enter project name: ");
		projNameText = new JTextField("");
		projNameText.setPreferredSize(new Dimension(305, 25));
		projNameText.setEnabled(false);
		
		botPane.add(projectNameLbl);
		botPane.add(projNameText);
		
		newWorkPane.add(topPane, BorderLayout.NORTH);
		newWorkPane.add(midPane, BorderLayout.CENTER);
		newWorkPane.add(botPane, BorderLayout.SOUTH);
		
		mainPanel.add(newWorkPane, BorderLayout.CENTER);
	}
	
	public void attachBtnPanel(){
		JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
		JButton okBtn = new JButton ("Ok");
		okBtn.setPreferredSize(new Dimension(75, 25));
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (newWrkSpace.isSelected()) {
					projectFolder= new File("" + workSpaceText.getText()+ "/" + projNameText.getText());
					videoFolder = new File("" + workSpaceText.getText()+ "/" + projNameText.getText() + "/Videos");
					audioFolder = new File("" + workSpaceText.getText()+ "/" + projNameText.getText() + "/.Audio");
					projectAudioFolder = new File("" + workSpaceText.getText() + "/" + projNameText.getText() + "/ProjectAudio");
					
					projectFolder.mkdir();
					videoFolder.mkdir();
					audioFolder.mkdir();
					projectAudioFolder.mkdir();
					
					projectPath = "" + workSpaceText.getText()+ "/" + projNameText.getText();
				}
				else {
					projectPath = projectPathTxt.getText();
					videoFolder = new File("" + projectPath + "/Videos");
					audioFolder = new File("" + projectPath + "/.Audio");
					projectAudioFolder = new File("" + projectPath + "/ProjectAudio");
					if(!videoFolder.exists()) {
						videoFolder.mkdir();
					}
					if(!audioFolder.exists()) {
						audioFolder.mkdir();
					}
					if(!projectAudioFolder.exists()) {
						projectAudioFolder.mkdir();
					}
				}
				
				frame.dispose();
				new MediaPlayer(projectPath);
			}
		});
		
		JButton cnclBtn = new JButton("Cancel");
		cnclBtn.setPreferredSize(new Dimension(75, 25));
		
		btnPane.add(okBtn);
		btnPane.add(cnclBtn);
		
		mainPanel.add(btnPane, BorderLayout.SOUTH);
	}

}
