package ui;

import java.awt.Container;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooser {
	private String path = "";
	private String fileName;
	
	public FileChooser(Container container, String fileType) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("\\"));
		
		if(fileType.equals("FOLDERS")) {
			fileChooser.setDialogTitle("Choose Folder");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		else if (fileType.equals("AUDIO")) {
			fileChooser.setDialogTitle("Choose Audio");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("mp3 Files", "mp3");
			fileChooser.setFileFilter(filter);
		}
		else {
			fileChooser.setDialogTitle("Choose Video");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("avi files", "avi");
			fileChooser.setFileFilter(filter);
		}
		
		int result = fileChooser.showOpenDialog(container);
		if( result == JFileChooser.APPROVE_OPTION) {
			path = fileChooser.getSelectedFile().getAbsolutePath();
			fileName = fileChooser.getSelectedFile().getName();
			//updateRecentPath(path);
		} else if ( result == JFileChooser.CANCEL_OPTION) {
		    path = path;
		}
	}
	
	public String getPath() {
		return path;
	}
	
	public String getFileName() {
		return fileName;
	}

}
