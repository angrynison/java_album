package album.ui;

import album.MainFrame;
import album.ui.panel.AddImagePanel;
import album.ui.panel.MainPanel;

import javax.swing.*;

public class AddImageFrame extends JFrame {
    // 사진 추가 프레임의 기본 너비
    public static final int DEFAULT_WIDTH = 500;
    // 사진 추가 프레임의 기본 높이
    public static final int DEFAULT_HEIGHT = 200;
    // 이미지가 없을 때 사진 추가 프레임의 기본 높이
    public static final int NO_IMAGE_HEIGHT = 120;

    public AddImageFrame(MainFrame mainFrame, MainPanel mainPanel) {
        super("사진 추가");
        setSize(DEFAULT_WIDTH, NO_IMAGE_HEIGHT);
        setContentPane(new AddImagePanel(this, mainPanel));
        // 리스트 프레임 생성 시 메인 프레임 기준 중앙에 뜨도록 위치 설정
        setLocation(mainFrame.getX() + mainFrame.getWidth() / 2 - this.getSize().width / 2, mainFrame.getY() + mainFrame.getHeight() / 2 - this.getSize().height / 2);
        setResizable(false);
        setVisible(true);
    }
}
