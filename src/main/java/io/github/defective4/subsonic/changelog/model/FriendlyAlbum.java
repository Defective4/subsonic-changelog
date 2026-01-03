package io.github.defective4.subsonic.changelog.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendlyAlbum {
    private final List<String> authors;
    private final BufferedImage cover;
    private final String id;
    private final String title;

    public FriendlyAlbum(String title, List<String> authors, BufferedImage cover, String id) {
        this.id = id;
        this.title = title;
        this.authors = new ArrayList<>(authors);
        this.cover = cover;
    }

    public boolean addAuthor(String e) {
        if (!authors.contains(e)) return authors.add(e);
        return false;
    }

    public List<String> getAuthors() {
        return Collections.unmodifiableList(authors);
    }

    public BufferedImage getCover() {
        return cover;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

}
