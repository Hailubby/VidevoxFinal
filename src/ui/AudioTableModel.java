package ui;

import java.io.File;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;


public class AudioTableModel extends AbstractTableModel{
	
	private HashMap<String, String> audioPaths;
	private Object[] keys;
	
	public AudioTableModel(String folderPath) {
		audioPaths = loadFiles(folderPath);
		keys = audioPaths.keySet().toArray();
	}
	
	String[] columnNames = { "Audio name" };
	
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		// return no. keys of hashmap
		return audioPaths.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		
		return keys[row];
	}
	
	
	private HashMap<String, String> loadFiles(String projectPath) {
		HashMap<String, String> hm = new HashMap();
		String directory = projectPath + "/.Audio";
		File audioDir = new File(directory);
		
		if (audioDir.list().length > 0) {
			File[] files = audioDir.listFiles();
			for(File file : files) {
				hm.put(file.getName(), file.getAbsolutePath());
			}
			return hm;
		}
		return hm;
	}
	
	public String getFilePath(String fileName) {
		return audioPaths.get(fileName);
	}
	
	public void addAudioFile(String key, String path) {
		audioPaths.put(key, path);
		keys = audioPaths.keySet().toArray();
		this.fireTableDataChanged();
	}
}
