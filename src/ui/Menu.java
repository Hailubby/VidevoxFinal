package ui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

public class Menu extends JMenuBar{
	Menu (final JFrame parent) {
		parent.setJMenuBar(this);
		 JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		 ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		
		JMenu file = new JMenu("File");
		add(file);
		JMenuItem newProject = new JMenuItem("New Project");
		JMenuItem open = new JMenuItem("Open Project");
		JMenuItem swtch = new JMenuItem("Switch Workspace");
		JMenuItem exit = new JMenuItem("Exit");
		file.add(newProject);
		file.add(open);
		file.add(new JSeparator(SwingConstants.HORIZONTAL));
		file.add(swtch);
		file.add(new JSeparator(SwingConstants.HORIZONTAL));
		file.add(exit);
		
		JMenu help = new JMenu("Help");
		add(help);
	}

}
