package io.github.defective4.subsonic.changelog.model;

import java.util.List;

public class MusicDirectory {
    private final List<Song> child;

    public MusicDirectory(List<Song> child) {
        this.child = child;
    }

    public List<Song> getChild() {
        return child;
    }

}
