package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import audio.AudioConverter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class Menu extends JMenuBar{
	
	private File videoFolder;
	private File audioFolder;
	private File projectAudioFolder;
	private String projectPath;
	
	Menu (final MediaPlayer parent, final AudioConverter ac) {
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
					}else {
						if (projectAudioFolder.list().length > 0) {
							File[] files = projectAudioFolder.listFiles();
							for(File file : files) {
								file.delete();
							}
						}
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
				if (!ac.getIsExported()) {
					int n = JOptionPane.showConfirmDialog( parent, "Your video has not been exported yet. Are you sure you want to exit?","Exit Option",JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				} else {
					System.exit(0);
				}
			}
		});
		
		file.add(open);
		file.add(new JSeparator(SwingConstants.HORIZONTAL));
		file.add(exit);

	}

}
