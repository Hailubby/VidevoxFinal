package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import audio.AudioConverter;
import audio.AudioConverterListener;

public class ProjectPane extends JPanel{
	
	private AudioConverter ac;
	private String filePath;
	private Object key;
	private Boolean isAllSelected = false;
	
	ProjectPane(String projectPath, final AudioConverter ac) {
		this.ac = ac;
		
		setLayout(new BorderLayout());
		
		JLabel title = new JLabel("Video Audio");
		
		JPanel tblPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		final JTable audioListTable = new JTable();
		final ProjectTableModel tableModel = new ProjectTableModel(ac.getAddedAudioMap());
		audioListTable.setModel(tableModel);
		final ListSelectionModel cellSelectionModel = audioListTable.getSelectionModel();
		cellSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (audioListTable.getSelectedRow() > -1) {
					key = audioListTable.getValueAt(audioListTable.getSelectedRow(), 0);
					filePath = tableModel.getFilePath(key.toString());
				}
			}
		});
		ac.addListener(new AudioConverterListener() {
			@Override
			public void addedAudioListChanged() {
				tableModel.refresh();
			}

			@Override
			public void audioFileCreated(String fileName, String filePath) {
				// TODO Auto-generated method stub
				
			}

			
		});
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(200, 300));
		audioListTable.setFillsViewportHeight(true);
		scrollPane.setViewportView(audioListTable);
		
		tblPane.add(scrollPane);
		
		JPanel btnPanel = new JPanel(new BorderLayout());
		JPanel btnPane1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel btnPane2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel btnPane3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		final JButton previewBtn = new JButton("Preview");
		previewBtn.setPreferredSize(new Dimension(175, 25));
		previewBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//new audio player to play at same time as video.
			}
		});
		
//		JButton selectAllBtn = new JButton("Select All");
//		selectAllBtn.setPreferredSize(new Dimension(85, 25));
//		selectAllBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				isAllSelected = true;
//				audioListTable.selectAll();
//				previewBtn.setEnabled(false);
//			}
//		});
		btnPane1.add(previewBtn);
//		btnPane1.add(selectAllBtn);
		
		JButton dltBtn = new JButton("Delete");
		dltBtn.setPreferredSize(new Dimension(175, 25));
		dltBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (audioListTable.getRowCount() > 0) {
					tableModel.removeAudio(key.toString());
					ac.removeAudioFile(filePath);
					tableModel.refresh();
				}
			}
		});
		
		btnPane2.add(dltBtn);
		
		JButton exportBtn = new JButton ("Export Video");
		exportBtn.setPreferredSize(new Dimension(175, 50));
		exportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (audioListTable.getRowCount() > 0) {
					int rows = audioListTable.getRowCount();
					ac.mergeAudioToExport(rows, audioListTable);
					ac.mergeVideo();
				}
			}
		});
		
		btnPane3.add(exportBtn);
		
		btnPanel.add(btnPane1, BorderLayout.NORTH);
		btnPanel.add(btnPane2, BorderLayout.CENTER);
		btnPanel.add(btnPane3, BorderLayout.SOUTH);
		
		add(title, BorderLayout.NORTH);
		add(tblPane, BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);

	}

}
