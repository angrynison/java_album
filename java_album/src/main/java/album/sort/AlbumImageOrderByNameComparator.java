package album.sort;

import album.model.AlbumImage;

import java.util.Comparator;

public class AlbumImageOrderByNameComparator implements Comparator<AlbumImage> {
    private final boolean desc;

    public AlbumImageOrderByNameComparator(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int compare(AlbumImage o1, AlbumImage o2) {
        if (desc) {
            return o2.getName().compareTo(o1.getName());
        }
        return o1.getName().compareTo(o2.getName());
    }
}
