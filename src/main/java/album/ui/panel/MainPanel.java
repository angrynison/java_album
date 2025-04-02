package album.ui.panel;

import album.MainFrame;
import album.db.Database;
import album.model.AlbumImage;
import album.sort.AlbumImageOrderByNameComparator;
import album.ui.AddImageFrame;
import album.ui.ListFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static album.MainFrame.DEFAULT_WIDTH;
import static album.ui.AddImageFrame.DEFAULT_HEIGHT;

public class MainPanel extends JPanel {
    public final int ORDER_BY_INSERTION_OPTION = 0;
    public final int ORDER_BY_INSERTION_DESC_OPTION = 1;
    public final int ORDER_BY_NAME_OPTION = 2;
    public final int ORDER_BY_NAME_DESC_OPTION = 3;

    private final int MAX_WIDTH = 800; //1260;
    private final int MAX_HEIGHT = 800; //1080;

    private AddImageFrame addImageFrame;
    private ListFrame listFrame;
    private List<AlbumImage> albumImages;
    private List<AlbumImage> searchAlbumImages;
    private ImagePanel selectedImagePanel = new ImagePanel();
    private JLabel tagLabel = new JLabel("");
    private JLabel nameLabel = new JLabel("");
    private int index = 0;
    private MainFrame mainFrame;
    private JTextField searchTextField = new JTextField();
    private boolean isSearch = false;

    public MainPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(null);
        albumImages = Database.getInstance().getImages();

        JButton addImageButton = new JButton("사진 추가");
        addImageButton.addActionListener(e -> {
            if (addImageFrame == null) {
                addImageFrame = new AddImageFrame(mainFrame, this);
                addImageFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // 위 상단 X(닫힘)을 눌렀을 때
                        super.windowClosing(e);
                        addImageFrame = null;
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                        // dispose를 호출하여 닫았을 때
                        super.windowClosed(e);
                        addImageFrame = null;
                    }
                });
            } else {
                // 이미 창이 있는 경우 해당 창의 포커스를 요청해 상단에 띄도록 한다.
                addImageFrame.requestFocus();
                // 창의 위치가 메인 프레임을 기준으로 중앙에 오도록 한다.
                addImageFrame.setLocation(mainFrame.getX() + mainFrame.getWidth() / 2 - addImageFrame.getSize().width / 2,
                        mainFrame.getY() + mainFrame.getHeight() / 2 - addImageFrame.getSize().height / 2);
            }
        });

        JButton prevImageButton = new JButton("이전");
        prevImageButton.addActionListener(e -> {
            if (albumImages.size() == 0) {
                JOptionPane.showMessageDialog(this, "사진이 없습니다.");
                return;
            }
            if ((!isSearch && 0 == index) || (isSearch && 0 == index)) {
                JOptionPane.showMessageDialog(this, "첫 번째 사진입니다.");
                return;
            }
            --index;
            showImage();
        });

        JButton nextImageButton = new JButton("다음");
        nextImageButton.addActionListener(e -> {
            if (albumImages.size() == 0) {
                JOptionPane.showMessageDialog(this, "사진이 없습니다.");
                return;
            }
            if ((!isSearch && albumImages.size() <= index + 1) || (isSearch && searchAlbumImages.size() <= index + 1)) {
                JOptionPane.showMessageDialog(this, "마지막 사진입니다.");
                return;
            }
            ++index;
            showImage();
        });

        JButton removeImageButton = new JButton("삭제");
        removeImageButton.addActionListener(e -> {
            if (albumImages.size() == 0) {
                JOptionPane.showMessageDialog(this, "사진이 없습니다.");
                return;
            }
            AlbumImage targetAlbumImage = albumImages.get(index);
            albumImages.remove(targetAlbumImage);
            Database.getInstance().removeImage(targetAlbumImage);
            if ((!isSearch && index != albumImages.size()) || (isSearch && searchAlbumImages.size() != index)) {
                showImage();
            } else {
                --index;
                showImage();
            }
            if (isSearch) {
                // 검색 모드
                listFrame.setAlbumImages(searchAlbumImages);
            } else {
                // 일반 모드
                listFrame.setAlbumImages(albumImages);
            }
        });

        JButton orderByButton = new JButton("정렬");
        orderByButton.addActionListener(e -> {
            // 옵션 다이얼로그를 띄운다.
            int selected = JOptionPane.showOptionDialog(this,
                    "정렬을 선택하세요.",
                    "정렬 선택",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new Object[]{"삽입순서", "삽입순서역순", "이름", "이름역순"},
                    "");
            if (selected == -1) {
                // 선택을 안했을 경우 종료
                return;
            }
            // 버튼 순서대로 눌렀을 때 결과가 0, 1, 2, 3 으로 나옴, 그거에 맞게 정렬 시킨다.
            if (selected == ORDER_BY_INSERTION_OPTION) {
                // id 순으로 불러온다.
                albumImages = Database.getInstance().getImages();
            } else if (selected == ORDER_BY_INSERTION_DESC_OPTION) {
                // id 역순으로 불러온다.
                albumImages = Database.getInstance().getImagesDesc();
            } else if (selected == ORDER_BY_NAME_OPTION) {
                albumImages = Database.getInstance().getImages();
                // id 순으로 불러와서
                // 어플리케이션에서 이름 순으로 정렬한다.
                albumImages.sort(new AlbumImageOrderByNameComparator(false));
            } else if (selected == ORDER_BY_NAME_DESC_OPTION) {
                albumImages = Database.getInstance().getImages();
                // id 순으로 불러와서
                // 어플리케이션에서 이름 역순으로 정렬한다.
                albumImages.sort(new AlbumImageOrderByNameComparator(true));
            }
            // 정렬된 데이터를 리스트 프레임에 알린다.
            listFrame.setAlbumImages(albumImages);
            // 검색 모드는 종료 시키고
            isSearch = false;
            index = 0;
            showImage();
        });

        JButton changeNameButton = new JButton("이름 변경");
        changeNameButton.addActionListener(e -> {
            if (albumImages.size() == 0) {
                JOptionPane.showMessageDialog(this, "사진이 없습니다.");
                return;
            }
            String name = JOptionPane.showInputDialog(this, "이름 입력");
            if (name == null) {
                return;
            }
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "이름을 입력해주세요.");
                return;
            }
            AlbumImage albumImage = albumImages.get(index);
            nameLabel.setText(name);
            // 이름을 변경 시키면 그에 달려 있던 태그도 변경해야되는데 해당 함수에 로직이 들어가 있음.
            Database.getInstance().updateName(albumImage.getName(), name);
            listFrame.setAlbumImages(albumImages);
        });

        JButton addTagButton = new JButton("태그 추가");
        addTagButton.addActionListener(e -> {
            if (albumImages.size() == 0) {
                JOptionPane.showMessageDialog(this, "사진이 없습니다.");
                return;
            }
            String tag = JOptionPane.showInputDialog(this, "태그 입력");
            if (tag == null) {
                return;
            }
            if (tag.isEmpty()) {
                JOptionPane.showMessageDialog(this, "추가할 태그를 입력해주세요.");
                return;
            }
            AlbumImage albumImage = albumImages.get(index);
            Set<String> tagSet = albumImage.getTagSet();
            if (!tagSet.contains(tag)) {
                tagSet.add(tag);
                tagLabel.setText(tagLabel.getText() + " #" + tag);
                // 메인 패널에서는 추가된 태그를 바로 DB에 저장한다.
                Database.getInstance().saveTag(albumImage.getName(), tag);
                listFrame.setAlbumImages(albumImages);
            } else {
                JOptionPane.showMessageDialog(this, "이미 추가된 태그입니다.");
            }
        });

        JButton removeTagButton = new JButton("태그 삭제");
        removeTagButton.addActionListener(e -> {
            if (albumImages.size() == 0) {
                JOptionPane.showMessageDialog(this, "사진이 없습니다.");
                return;
            }
            String tag = JOptionPane.showInputDialog(this, "삭제할 태그 입력");
            if (tag == null) {
                return;
            }
            if (tag.isEmpty()) {
                JOptionPane.showMessageDialog(this, "태그를 입력해주세요.");
                return;
            }
            AlbumImage albumImage = albumImages.get(index);
            Set<String> tagSet = albumImage.getTagSet();
            if (tagSet.contains(tag)) {
                tagSet.remove(tag);
                StringBuilder tagStringBuilder = new StringBuilder();
                for (String tempTag : tagSet) {
                    tagStringBuilder.append(" #").append(tempTag);
                }
                tagLabel.setText(tagStringBuilder.toString());
                // 메인 패널에서는 삭제된 태그를 DB에서 바로 삭제한다.
                Database.getInstance().removeTag(albumImage.getName(), tag);
                listFrame.setAlbumImages(albumImages);
            } else {
                JOptionPane.showMessageDialog(this, "없는 태그 입니다.");
            }
        });

        searchTextField.addActionListener(e -> search());

        JButton searchButton = new JButton("검색");
        searchButton.addActionListener(e -> search());

        JButton listButton = new JButton("리스트");
        listButton.addActionListener(e -> {
            if (listFrame == null) { // listFrame이 null일 경우에 창이 안떠있다고 가정
                // 창을 1개만 띄우기 위한 로직
                listFrame = new ListFrame(mainFrame, this);
                listFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // 위 상단 X(닫힘)을 눌렀을 때
                        super.windowClosing(e);
                        listFrame = null;
                    }
                });
            } else {
                listFrame.requestFocus();
                listFrame.setLocation(mainFrame.getX() + mainFrame.getWidth() / 2 - listFrame.getSize().width / 2,
                        mainFrame.getY() + mainFrame.getHeight() / 2 - listFrame.getSize().height / 2);
            }
        });

        addImageButton.setBounds(20, 20, 100, 40);
        prevImageButton.setBounds(135, 20, 60, 40);
        nextImageButton.setBounds(210, 20, 60, 40);
        removeImageButton.setBounds(285, 20, 60, 40);
        orderByButton.setBounds(360, 20, 60, 40);
        changeNameButton.setBounds(435, 20, 100, 40);
        addTagButton.setBounds(550, 20, 100, 40);
        removeTagButton.setBounds(665, 20, 100, 40);
        searchTextField.setBounds(780, 20, 160, 40);
        searchButton.setBounds(955, 20, 60, 40);
        listButton.setBounds(1030, 20, 80, 40);
        nameLabel.setBounds(20, 70, 200, 30);
        selectedImagePanel.setBounds(20, 100, 0, 0);
        tagLabel.setBounds(20, 70, 0, 0);

        nameLabel.setFont(new Font("나눔고딕", Font.BOLD, 25));
        tagLabel.setFont(new Font("나눔고딕", Font.BOLD, 25));

        add(addImageButton);
        add(prevImageButton);
        add(nextImageButton);
        add(removeImageButton);
        add(orderByButton);
        add(changeNameButton);
        add(addTagButton);
        add(removeTagButton);
        add(searchTextField);
        add(searchButton);
        add(listButton);
        add(nameLabel);
        add(selectedImagePanel);
        add(tagLabel);

        if (albumImages.size() != 0) {
            showImage();
        }
        //listButton.doClick();
    }

    private void showImage() {
        if ((!isSearch && albumImages.size() == 0) || (isSearch && searchAlbumImages.size() == 0)) {
            // 현재 저장된 이미지가 없을 때
            nameLabel.setText("");
            tagLabel.setText("");
            mainFrame.setNoImageSize();
            tagLabel.setBounds(20, 70, 0, 0);
            selectedImagePanel.setImage(null);
            return;
        }
        if (isSearch) {
            // 검색 모드
            showImage(searchAlbumImages.get(index));
        } else {
            // 일반 모드
            showImage(albumImages.get(index));
        }
        repaint();
    }

    private void search() {
        String searchText = searchTextField.getText();
        if (searchText.isEmpty()) {
            // 빈 칸으로 검색을 할 경우 초기로 돌아간다.
            isSearch = false;
            albumImages = Database.getInstance().getImages();
            listFrame.setAlbumImages(albumImages);
        } else {
            // 검색 모드
            isSearch = true;
            searchAlbumImages = albumImages.stream().filter(albumImage -> {
                for (String tag : albumImage.getTagSet()) {
                    if (tag.contains(searchText)) {
                        return true;
                    }
                }
                return albumImage.getName().contains(searchTextField.getText());
            }).collect(Collectors.toList());
            listFrame.setAlbumImages(searchAlbumImages);
        }
        // 어떤 데이터를 읽을지 정해졌으니 내용을 다시 그린다.
        index = 0;
        showImage();
    }

    private void showImage(AlbumImage targetAlbumImage) {
        // AddImagePanel에 사진 등록하는 로직과 동일함.
        ImageIcon selectedImageIcon = new ImageIcon(targetAlbumImage.getPath());
        int width = selectedImageIcon.getIconWidth();
        int height = selectedImageIcon.getIconHeight();
        double rate = (double) width / height;
        if (rate > 1) { // 너비가 더 크다
            if (width > MAX_WIDTH) {
                width = MAX_WIDTH;
                height = (int) (MAX_WIDTH / rate);
            }
        } else { // 높이가 더 크다
            if (height > MAX_HEIGHT) {
                height = MAX_HEIGHT;
                width = (int) (MAX_HEIGHT / rate);
            }
        }
        StringBuilder tagStringBuilder = new StringBuilder();
        for (String tag : targetAlbumImage.getTagSet()) {
            tagStringBuilder.append(" #").append(tag);
        }
        nameLabel.setText(targetAlbumImage.getName());
        tagLabel.setText(tagStringBuilder.toString());
        mainFrame.setSize(DEFAULT_WIDTH + 40 + (DEFAULT_WIDTH < width ? width - DEFAULT_WIDTH : 0), DEFAULT_HEIGHT + height + 30);
        tagLabel.setBounds(20, DEFAULT_HEIGHT + height - 70, mainFrame.getWidth() - 40, 30);
        selectedImagePanel.setSize(width, height);
        selectedImagePanel.setImageSize(width, height);
        selectedImagePanel.setImage(selectedImageIcon.getImage());
    }

    public void addAlbumImage(AlbumImage albumImage) {
        // mainPanel에 새로운 앨범 데이터가 추가 된 것을 알린다.
        isSearch = false;
        albumImages.add(albumImage);
        if (albumImages.size() == 1) {
            index = 0;
        }
        showImage();
        // listFrame에 새로운 앨범 데이터가 추가 된 것을 알린다.
        if (listFrame != null) {
        	listFrame.setAlbumImages(albumImages);
        }
    }

    public void selectImage(int index) {
        // listFrame에서 더블 클릭한 이미지를 보이도록 한다.
        this.index = index;
        showImage();
    }

    public List<AlbumImage> getAlbumImages() {
        return albumImages;
    }
}
