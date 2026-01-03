package io.github.defective4.subsonic.changelog.model;

import java.util.List;

public class AlbumList {
    private final List<Album> album;

    public AlbumList(List<Album> album) {
        this.album = album;
    }

    public List<Album> getAlbum() {
        return album;
    }

}
