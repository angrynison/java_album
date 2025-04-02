package album.model;

import java.util.HashSet;
import java.util.Set;

// Object (java) Relation(mysql) Mapping  
// 테이블 스키마에 맞게 모델 클래스 선언
public class AlbumImage implements Comparable<AlbumImage>{
    private int id;
    private String name;
    private String path;
    private Set<String> tagSet;

    public AlbumImage() {
        tagSet = new HashSet<>();
    }

    public Set<String> getTagSet() {
        return tagSet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTagSet(Set<String> tagSet) {
        this.tagSet.addAll(tagSet);
    }

    public void addTag(Set<String> tags) {
        tagSet.addAll(tags);
    }

    @Override
    public int compareTo(AlbumImage o) {
        return 0;
    }
}
