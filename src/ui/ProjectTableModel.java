package ui;

import java.util.Arrays;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import audio.AddedAudio;

public class ProjectTableModel extends AbstractTableModel{
	
	private HashMap<String, AddedAudio> addedAudioMap;
	private Object[] keys;
	
	public ProjectTableModel(HashMap<String, AddedAudio> addedAudioMap) {
		this.addedAudioMap = addedAudioMap;
		keys = addedAudioMap.keySet().toArray();
	}
	
	String[] columnNames = { "Audio name", "Start time" };
	
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
		return addedAudioMap.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return keys[row];
		}
		else {
			return addedAudioMap.get(keys[row]).getStartTime();
		}
	}

	public void refresh() {
		keys = addedAudioMap.keySet().toArray();
		this.fireTableDataChanged();
	}
	
	public String getFilePath(String fileName) {
		return addedAudioMap.get(fileName).getAudioPath();
	}

	public void removeAudio(String key) {
		addedAudioMap.remove(key);
	}

}
