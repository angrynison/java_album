package album;

import album.db.Database;
import album.ui.panel.MainPanel;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public static final int DEFAULT_WIDTH = 1280;
    public static final int NO_IMAGE_HEIGHT = 120;

    public MainFrame() {
        super("앨범");
        setSize(DEFAULT_WIDTH, NO_IMAGE_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new MainPanel(this));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // 현재 사용중인 해상도를 가져와 창을 중앙에 위치시킨다.
        setLocation(dim.width / 2 - this.getSize().width / 2, 200);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        // flatlaf 라이브러리 사용해서 Swing 기본 UI 테마 변경
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        // 데이터 베이스 초기화
        Database.getInstance();
        new MainFrame();
    }

    public void setNoImageSize() {
        // 현재 저장된 이미지가 없을 때 프레임 사이즈 조정
        setSize(DEFAULT_WIDTH, NO_IMAGE_HEIGHT);
    }
}
