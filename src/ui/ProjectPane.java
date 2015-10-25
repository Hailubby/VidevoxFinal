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
import audio.AudioPlayer;
import ui.utils.VideoOptions;

public class ProjectPane extends JPanel{
	
	private AudioConverter ac;
	private String filePath;
	private Object key;
	private Boolean isAllSelected = false;
	private JTable audioListTable;
	private ProjectTableModel tableModel;
	private AudioPlayer previewPlayer = new AudioPlayer();
	
	ProjectPane(String projectPath, final AudioConverter ac, final VideoOptions vidOption, final MediaPlayer mainFrame) {
		this.ac = ac;
		
		setLayout(new BorderLayout());
		
		JLabel title = new JLabel("Video Audio");
		
		JPanel tblPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		audioListTable = new JTable();
		tableModel = new ProjectTableModel(ac.getAddedAudioMap());
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
		previewBtn.setPreferredSize(new Dimension(85, 25));
		previewBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (audioListTable.getRowCount() > 0 && (audioListTable.getSelectedRow() > -1)) {
					previewPlayer.stop();
					previewPlayer = new AudioPlayer(filePath, vidOption);
					vidOption.setPreviewIsFinished(false);
					vidOption.stopBtnFunctionality();
					vidOption.playBtnFuntionality();
					mainFrame.setPlayButton("Pause");
				}
			}
		});
		
		JButton selectAllBtn = new JButton("Select All");
		selectAllBtn.setPreferredSize(new Dimension(85, 25));
		selectAllBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (audioListTable.getRowCount() > 0 && !isAllSelected) {
					isAllSelected = true;
					audioListTable.selectAll();
					previewBtn.setEnabled(false);
				} else if (audioListTable.getRowCount() > 0 && isAllSelected) {
					isAllSelected = false;
					audioListTable.clearSelection();
					previewBtn.setEnabled(true);
				}
			}
		});
		btnPane1.add(previewBtn);
		btnPane1.add(selectAllBtn);
		
		JButton dltBtn = new JButton("Delete");
		dltBtn.setPreferredSize(new Dimension(175, 25));
		dltBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (audioListTable.getRowCount() > 0 && (audioListTable.getSelectedRow() > -1)) {
					if (isAllSelected) {
						rmvAllTableContent();
						isAllSelected = false;
						previewBtn.setEnabled(true);
					} else {
						tableModel.removeAudio(key.toString());
						ac.removeAudioFile(filePath);
						tableModel.refresh();
						if (audioListTable.getRowCount() < 0) {
							ac.setIsExported(true);
						}
					}
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
					Boolean successful = ac.mergeAudioToExport(rows, audioListTable);
					if (successful) {
						ac.mergeVideo();
						rmvAllTableContent();
					}
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
	
	private void rmvAllTableContent() {
		for (int i = audioListTable.getRowCount() - 1; i >=0; i--) {
			key = audioListTable.getValueAt(0, 0);
			filePath = tableModel.getFilePath(key.toString());
			tableModel.removeAudio(key.toString());
			ac.removeAudioFile(filePath);
			tableModel.refresh();
			ac.setIsExported(true);
		}
	}
	
	public void playPreview() {
		previewPlayer.play();
	}
	
	public void pausePreview() {
		previewPlayer.pause();
	}
	
	public void stopPreview() {
		previewPlayer.stop();
	}
	
	public AudioPlayer getPreviewPlayer() {
		return previewPlayer;
	}
	

}
