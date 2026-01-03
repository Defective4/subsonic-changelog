package io.github.defective4.subsonic.changelog.db;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;

import io.github.defective4.subsonic.changelog.db.model.AlbumEntity;
import io.github.defective4.subsonic.changelog.db.model.SongEntity;

public class IndexDatabase {
    private final SessionFactory factory;

    public IndexDatabase() {
        factory = new MetadataSources().addAnnotatedClass(AlbumEntity.class).addAnnotatedClass(SongEntity.class)
                .buildMetadata().buildSessionFactory();
    }

    public List<AlbumEntity> diffAlbums(List<AlbumEntity> albums) {
        List<AlbumEntity> match = factory
                .fromTransaction(ses -> ses.createNativeQuery("select * from `albums`", AlbumEntity.class).list());
        return albums.stream()
                .filter(a -> match.stream().filter(e2 -> e2.getId().equals(a.getId())).findAny().isEmpty()).toList();
    }

    public List<SongEntity> diffSongs(List<SongEntity> songs) {
        List<SongEntity> match = factory
                .fromTransaction(ses -> ses.createNativeQuery("select * from `songs`", SongEntity.class).list());
        return songs.stream().filter(
                song -> match.stream().filter(matched -> matched.getId().equals(song.getId())).findAny().isEmpty())
                .toList();
    }

    public void store(Object obj) {
        factory.inTransaction(ses -> ses.persist(obj));
    }

    public void storeList(List<?> objs) {
        factory.inTransaction(ses -> objs.forEach(ses::persist));
    }
}
