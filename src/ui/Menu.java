package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class Menu extends JMenuBar{
	
	private File videoFolder;
	private File audioFolder;
	private File projectAudioFolder;
	private String projectPath;
	
	Menu (final MediaPlayer parent) {
		parent.setJMenuBar(this);
		 JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		 ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		
		JMenu file = new JMenu("File");
		add(file);
		JMenuItem open = new JMenuItem("Open Project");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser fc = new FileChooser(parent, "FOLDERS");
				projectPath = fc.getPath();
				if (!projectPath.isEmpty() && (projectPath != null)) {
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
					parent.getVideoPlayer().stop();
					parent.dispose();
					new MediaPlayer(projectPath);
				}
			}
		});
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		file.add(open);
		file.add(new JSeparator(SwingConstants.HORIZONTAL));
		file.add(exit);

	}

}
