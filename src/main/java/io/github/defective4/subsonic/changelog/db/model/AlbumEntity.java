package io.github.defective4.subsonic.changelog.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "albums")
public class AlbumEntity {

    @Id
    private String id;

    @Column
    private int songs;

    public AlbumEntity() {}

    public AlbumEntity(String id, int songs) {
        this.id = id;
        this.songs = songs;
    }

    public String getId() {
        return id;
    }

    public int getSongs() {
        return songs;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSongs(int songs) {
        this.songs = songs;
    }

}
