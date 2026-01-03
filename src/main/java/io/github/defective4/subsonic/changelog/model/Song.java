package io.github.defective4.subsonic.changelog.model;

public class Song {
    private final String id, title, album, artist, coverArt, albumId;

    public Song(String id, String title, String album, String artist, String coverArt, String albumId) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.coverArt = coverArt;
        this.albumId = albumId;
    }

    public String getAlbum() {
        return album;
    }

    public String getAlbumId() {
        return albumId;
    }

    public String getArtist() {
        return artist;
    }

    public String getCoverArt() {
        return coverArt;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

}
