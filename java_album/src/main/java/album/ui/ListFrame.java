package album.ui;

import album.MainFrame;
import album.model.AlbumImage;
import album.ui.panel.ListPanel;
import album.ui.panel.MainPanel;

import javax.swing.*;
import java.util.List;

public class ListFrame extends JFrame {
    private final ListPanel listPanel;
    public ListFrame(MainFrame mainFrame, MainPanel mainPanel) {
        super("사진 리스트");
        setSize(600, 320);
        listPanel = new ListPanel(mainPanel);
        setContentPane(listPanel);
        // 리스트 프레임 생성 시 메인 프레임 기준 중앙에 뜨도록 위치 설정
        setLocation(mainFrame.getX() + mainFrame.getWidth() / 2 - this.getSize().width / 2, mainFrame.getY() + mainFrame.getHeight() / 2 - this.getSize().height / 2);
        setResizable(false);
        setVisible(true);
    }

    // 현재 조회 중인 항목에 변경이 있을 경우 리스트 패널에 알려주기 위한 메소드
    public void setAlbumImages(List<AlbumImage> albumImages) {
        listPanel.setAlbumImages(albumImages);
    }
}
