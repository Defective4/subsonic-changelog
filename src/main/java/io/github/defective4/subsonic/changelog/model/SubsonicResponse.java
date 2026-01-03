package io.github.defective4.subsonic.changelog.model;

public class SubsonicResponse {
    public static enum Status {
        failed, ok;
    }

    private final AlbumList albumList;
    private final MusicDirectory directory;
    private final Status status;

    public SubsonicResponse(Status status, AlbumList albumList, MusicDirectory directory) {
        this.status = status;
        this.albumList = albumList;
        this.directory = directory;
    }

    public AlbumList getAlbumList() {
        return albumList;
    }

    public MusicDirectory getDirectory() {
        return directory;
    }

    public Status getStatus() {
        return status;
    }

    public void validate() {
        if (status != Status.ok) throw new IllegalStateException();
    }

}
