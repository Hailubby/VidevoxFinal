package vidivox;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;

public class FileChooser {
	
	private String path;
	private File recentDirPath = new File("CustomVidivoxFolder/.recentPath");
	private String recentPath = "\\";
	
	FileChooser(Container container) {
		JFileChooser fileChooser = new JFileChooser();
		
		if(!recentDirPath.exists()) {
			try {
				//creates hidden file containing path of most recent directory if not already created
				recentDirPath.createNewFile();
				//sets initial directory
				fileChooser.setCurrentDirectory(new File("\\"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			//if file already exists, set current directory to the most recent one
			findRecentPath(recentDirPath);
			fileChooser.setCurrentDirectory(new File(recentPath));
		}
		
		fileChooser.setDialogTitle("Choose File");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int result = fileChooser.showOpenDialog(container);
		if( result == JFileChooser.APPROVE_OPTION) {
			path = fileChooser.getSelectedFile().getAbsolutePath();
			updateRecentPath(path);
		} else if ( result == JFileChooser.CANCEL_OPTION) {
		    return;
		}
		
	}
	
	//updates the recent path in the hidden file
	private void updateRecentPath(String recentPath) {
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("CustomVidivoxFolder/.recentPath")));
			pw.write(path);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//reads the hidden file to find the most recent path
	private void findRecentPath(File recentDir) {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(recentDir));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			recentPath = br.readLine();
			if (recentPath == null){
				recentPath = "\\";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public String getPath() {
		return path;
	}
}
