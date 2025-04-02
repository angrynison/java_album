package album.ui.panel;

import album.model.AlbumImage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ListPanel extends JPanel {
    private JTable albumImageListTable;
    private List<AlbumImage> albumImages;

    public ListPanel(MainPanel mainPanel) {
        setLayout(null);
        albumImageListTable = new JTable();
        //albumImageListTable.setTableHeader(null);
        albumImageListTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"이름", "태그", "경로"}
        ) {
            // 테이블 수정을 못하도록한다.
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        albumImageListTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    // 더블클릭한 행을 메인 패널에서 보여주도록 한다.
                    int row = albumImageListTable.getSelectedRow();
                    mainPanel.selectImage(row);
                }
            }
        });
        // 다중 선택을 못하게한다.
        albumImageListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // 테이블 윤곽선을 그리지 않는다.
        albumImageListTable.setShowGrid(false);

        albumImages = mainPanel.getAlbumImages();
        // 불러온 데이터를 테이블에 뿌려준다.
        for (AlbumImage albumImage : albumImages) {
            StringBuilder tagStringBuilder = new StringBuilder();
            for (String tag : albumImage.getTagSet()) {
                tagStringBuilder.append(" #").append(tag);
            }
            ((DefaultTableModel) albumImageListTable.getModel()).addRow(new Object[]{albumImage.getName(), tagStringBuilder.toString(), albumImage.getPath()});
        }

        // 스크롤을 지정해서 사이즈를 넘어간 내용은 스크롤을 통해 볼 수 있도록 한다.
        JScrollPane albumImageScrollPane = new JScrollPane();
        albumImageScrollPane.setViewportView(albumImageListTable);

        albumImageScrollPane.setBounds(15, 15, 570, 250);
        add(albumImageScrollPane);
    }

    public void setAlbumImages(List<AlbumImage> albumImages) {
        this.albumImages = albumImages;
        // 기존에 있던 테이블 행들을 다 지운다.
        ((DefaultTableModel)albumImageListTable.getModel()).setRowCount(0);
        // 새로운 앨범 이미지들을 불러와 리스팅한다.
        for (AlbumImage albumImage : albumImages) {
            StringBuilder tagStringBuilder = new StringBuilder();
            for (String tag : albumImage.getTagSet()) {
                tagStringBuilder.append(" #").append(tag);
            }
            ((DefaultTableModel) albumImageListTable.getModel()).addRow(new Object[]{albumImage.getName(), tagStringBuilder.toString(), albumImage.getPath()});
        }
    }
}
