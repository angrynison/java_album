package album.ui.panel;

import album.db.Database;
import album.model.AlbumImage;
import album.ui.AddImageFrame;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

import static album.ui.AddImageFrame.DEFAULT_HEIGHT;
import static album.ui.AddImageFrame.DEFAULT_WIDTH;

public class AddImagePanel extends JPanel {
    // 불러올 사진의 최대 너비
    private final int MAX_WIDTH = 800;//1240;
    // 불러올 사진의 최대 높이
    private final int MAX_HEIGHT = 800;//1080;

    private final int screenWidth;
    private final int screenHeight;
    private ImagePanel selectedImagePanel = new ImagePanel();
    private Set<String> tagSet = new HashSet<>();
    private JLabel tagLabel = new JLabel("");
    private String absolutePath;
    private String fileName;

    public AddImagePanel(AddImageFrame addImageFrame, MainPanel mainPanel) {
        setLayout(null);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // 현재 해상도의 너비와 높이를 구해놓음
        screenWidth = dim.width;
        screenHeight = dim.height;
        JButton openImageButton = new JButton("사진 열기");
        openImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            fileChooser.setDialogTitle("사진 열기");
            fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
            fileChooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    } else {
                        String lowerFileName = f.getName().toLowerCase();
                        return lowerFileName.endsWith(".jpg") || lowerFileName.endsWith("png");
                    }
                }

                @Override
                public String getDescription() {
                    return "jpg or png";
                }
            });
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                // 선택된 파일
                File selectedFile = fileChooser.getSelectedFile();
                // 절대 경로를 불러옴
                absolutePath = selectedFile.getAbsolutePath();
                // 파일 이름을 불러옴
                fileName = selectedFile.getName();
                ImageIcon selectedImageIcon = new ImageIcon(selectedFile.getAbsolutePath());
                int width = selectedImageIcon.getIconWidth();
                int height = selectedImageIcon.getIconHeight();
                // 해상도가 큰 사진의 경우 너비 / 높이 비율을 구하고 더 큰 값을 기준으로 비율 축소 시킨다.
                double rate = (double) width / height;
                if (rate > 1) { // 너비가 더 크다
                    if (width > MAX_WIDTH) {
                        width = MAX_WIDTH;
                        height = (int) (MAX_WIDTH / rate); // 비율 축소
                    }
                } else { // 높이가 더 크다
                    if (height > MAX_HEIGHT) {
                        height = MAX_HEIGHT;
                        width = (int) (MAX_HEIGHT / rate); // 비율 축소
                    }
                }
                tagSet.clear();
                tagLabel.setText("");
                // 불러온 사진에 대해 사진 추가 사이즈를 변경함
                addImageFrame.setSize(DEFAULT_WIDTH + 40 + (DEFAULT_WIDTH < width ? width - DEFAULT_WIDTH : 0), DEFAULT_HEIGHT + height);
                // 사진을 불러왔을 때 현재 스크린을 넘어가는지 체크하는 2 분기문
                if (screenHeight < addImageFrame.getY() + addImageFrame.getHeight()) {
                    addImageFrame.setLocation(addImageFrame.getX(), screenHeight - addImageFrame.getHeight());
                }
                if (screenWidth < addImageFrame.getX() + addImageFrame.getWidth()) {
                    addImageFrame.setLocation(screenWidth - addImageFrame.getWidth(), addImageFrame.getY());
                }
                tagLabel.setBounds(20, DEFAULT_HEIGHT + height - 100, addImageFrame.getWidth() - 40, 30);
                // 이미지 패널 크기 조절
                selectedImagePanel.setSize(width, height);
				selectedImagePanel.setImageSize(width, height);
                // 이미지 패널에 이미지 지정
                selectedImagePanel.setImage(selectedImageIcon.getImage());
            }
        });
        openImageButton.setBounds(20, 20, 100, 40);
        add(openImageButton);

        JButton addTagButton = new JButton("태그 추가");
        addTagButton.addActionListener(e -> {
            String tag = JOptionPane.showInputDialog(this, "추가할 태그 입력");
            // tagSet에 이미 저장되어 있는지 확인한다.
           if (!tagSet.contains(tag)) {
                // 없으면 태그를 추가한다.
                tagSet.add(tag);
                // 태그 라벨을 수정한다.
                tagLabel.setText(tagLabel.getText() + " #" + tag);
            } else {
                // 이미 있으면 알림
                JOptionPane.showMessageDialog(this, "이미 추가된 태그입니다.");
            }
        });
        addTagButton.setBounds(140, 20, 100, 40);
        add(addTagButton);

        JButton removeTagButton = new JButton("태그 삭제");
        removeTagButton.addActionListener(e -> {
            String tag = JOptionPane.showInputDialog(this, "삭제할 태그 입력");
            if (tagSet.contains(tag)) {
                // 있으면 삭제한다.
                tagSet.remove(tag);
                // 태그 라벨을 수정한다. 객체가 아닌 String 타입으로 관리하므로 처음부터 다시 라벨 텍스트를 만들어냄
                StringBuilder tagStringBuilder = new StringBuilder();
                for (String tempTag : tagSet) {
                    tagStringBuilder.append(" #").append(tempTag);
                }
                tagLabel.setText(tagStringBuilder.toString());
            } else {
                // 삭제할 태그가 없음
                JOptionPane.showMessageDialog(this, "없는 태그 입니다.");
            }
        });
        removeTagButton.setBounds(260, 20, 100, 40);
        add(removeTagButton);

        JButton saveButton = new JButton("저장 하기");
        saveButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "이름 입력");
            if (name != null && !name.isEmpty()) {
                // 상대 경로를 지정한다.
                String relativeDir = "./images/" + fileName;
                // 데이터베이스에 담기 위한 객체를 생성해 Database 객체에 전달한다.
                AlbumImage albumImage = new AlbumImage();
                albumImage.setName(name);
                // 경로는 상대경로를 이용하고, 불러온 파일은 아래서 프로젝트 ./images 경로로 복사한다.
                albumImage.setPath(relativeDir);
                albumImage.setTagSet(tagSet);
                Database.getInstance().saveImage(albumImage);
                // 데이터베이스에는 저장됬지만, 현재 어플리케이션 데이터에는 해당 정보가 없으므로 mainPanel에 앨범 데이터가 추가된 것을 알린다.
                mainPanel.addAlbumImage(albumImage);
                File file = new File(absolutePath);
                File newFile = new File(relativeDir);
                try {
                    Path imageDir = Path.of("./images");
                    // images 디렉토리가 없는 경우 생성하고
                    if (!Files.exists(imageDir)) {
                        Files.createDirectory(imageDir);
                    }
                    // 파일을 프로젝트 images 경로로 복사한다.
                    Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                // 사진 추가 창을 닫는다.
                addImageFrame.dispose();
            } else {
                // 이름이 입력되지 않음.
                JOptionPane.showMessageDialog(this, "이름을 입력해주세요.");
            }
        });
        saveButton.setBounds(380, 20, 100, 40);
        add(saveButton);

        selectedImagePanel.setBounds(20, 70, 0, 0);
        add(selectedImagePanel);

        tagLabel.setBounds(20, 70, 0, 0);
        tagLabel.setFont(new Font("나눔고딕", Font.BOLD, 25));
        add(tagLabel);
    }
}
