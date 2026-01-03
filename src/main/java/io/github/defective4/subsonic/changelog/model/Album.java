package io.github.defective4.subsonic.changelog.model;

public class Album {
    private final String id, title, name, album, artist, coverArt;
    private final int songCount;

    public Album(String id, String title, String name, String album, String artist, String coverArt, int songCount) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.album = album;
        this.artist = artist;
        this.coverArt = coverArt;
        this.songCount = songCount;
    }

    public String getAlbum() {
        return album;
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

    public String getName() {
        return name;
    }

    public int getSongCount() {
        return songCount;
    }

    public String getTitle() {
        return title;
    }

}
