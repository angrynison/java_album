package album.db;

import album.model.AlbumImage;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Database {
    // 싱글톤 패턴 생성
    private static Database instance;

    private Connection connection;

    public Database() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/testdb?serverTimezone=UTC";
            connection = DriverManager.getConnection(url, "root", "1234");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 싱글턴 패턴
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    //데이터베이스에서 이미지 정보 불러오는 메소드
    public List<AlbumImage> getImages() {
  
        List<AlbumImage> albumImages = new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from image");
            while (rs.next()) {
                AlbumImage albumImage = new AlbumImage();
                albumImage.setId(rs.getInt("id"));
                albumImage.setName(rs.getString("name"));
                albumImage.setPath(rs.getString("path"));
                // albumimage class에서 만든 함수 이용
                // 조회된 이미지에 대한 태그 정보를 데이터베이스에서 불러 태그 정보를 객체에 담아줌
                albumImage.addTag(getTags(albumImage.getName()));
                albumImages.add(albumImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return albumImages;
    }

    	// 데이터 베이스로부터 사진을 역순으로 가져오는 메소드
    public List<AlbumImage> getImagesDesc() {
        List<AlbumImage> albumImages = new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from image order by id desc");
            // desc 를 이용해 역순으로 가져오기 
            while (rs.next()) {
            	  // albumimage class에서 만든 함수 이용
                AlbumImage albumImage = new AlbumImage();
                albumImage.setId(rs.getInt("id"));
                albumImage.setName(rs.getString("name"));
                albumImage.setPath(rs.getString("path"));
                albumImage.addTag(getTags(albumImage.getName()));
                albumImages.add(albumImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return albumImages;
    }

    // 설정한 태그 데이터베이스로부터 불러오기 
    public Set<String> getTags(String name) {
        Statement stmt = null;
        Set<String> tagSet = new HashSet<>();
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from image_tag where image_name = '" + name + "'");
            while (rs.next()) {
                tagSet.add(rs.getString("tag"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return tagSet;
    }

    public void saveImage(AlbumImage albumImage) {
        String sql = "insert into image(name, path) values(?, ?)";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, albumImage.getName());
            pstmt.setString(2, albumImage.getPath());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        Set<String> tagSet = albumImage.getTagSet();
        for (String tag : tagSet) {
            saveTag(albumImage.getName(), tag);
        }
    }

    public void saveTag(String imageName, String tag) {
        PreparedStatement pstmt = null;
        try {
            String sql = "insert into image_tag(image_name, tag) values(?, ?)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, imageName);
            pstmt.setString(2, tag);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeImage(AlbumImage targetAlbumImage) {
        PreparedStatement pstmt = null;
        try {
            String sql = "delete from image where name = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, targetAlbumImage.getName());
            pstmt.executeUpdate();
            pstmt.close();

            sql = "delete from image_tag where image_name = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, targetAlbumImage.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<AlbumImage> getImagesOrderByName() {
        List<AlbumImage> albumImages = new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from image order by name");
            while (rs.next()) {
                AlbumImage albumImage = new AlbumImage();
                albumImage.setId(rs.getInt("id"));
                albumImage.setName(rs.getString("name"));
                albumImage.setPath(rs.getString("path"));
                albumImage.addTag(getTags(albumImage.getName()));
                albumImages.add(albumImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return albumImages;
    }

    public void updateName(String targetName, String name) {
        PreparedStatement pstmt = null;
        try {
            String sql = "update image set name = ? where name = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, targetName);
            pstmt.executeUpdate();
            pstmt.close();

            sql = "update image_tag set image_name = ? where image_name = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, targetName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeTag(String image, String tag) {
        PreparedStatement pstmt = null;
        try {
            String sql = "delete from image_tag where image_name = ? and tag = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, image);
            pstmt.setString(2, tag);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
