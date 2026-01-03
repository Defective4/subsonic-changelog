package io.github.defective4.subsonic.changelog;

import java.util.ArrayList;
import java.util.List;

import io.github.defective4.subsonic.changelog.api.SubsonicAPI;
import io.github.defective4.subsonic.changelog.db.IndexDatabase;
import io.github.defective4.subsonic.changelog.db.model.AlbumEntity;
import io.github.defective4.subsonic.changelog.db.model.SongEntity;
import io.github.defective4.subsonic.changelog.model.Album;
import io.github.defective4.subsonic.changelog.model.Song;

public class Main {
    public static void main(String[] args) {
        try {
            IndexDatabase db = new IndexDatabase();
            SubsonicAPI api = new SubsonicAPI("https://music.raspberry.local/rest/", "Defective",
                    "");

            List<Album> allAlbums = api.getAlbums();
            List<AlbumEntity> diffAE = db
                    .diffAlbums(allAlbums.stream().map(a -> new AlbumEntity(a.getId(), a.getSongCount())).toList());
            List<Album> diffA = diffAE.stream()
                    .map(e -> allAlbums.stream().filter(e2 -> e2.getId().equals(e.getId())).findAny().get()).toList();

            List<Song> allSongs = new ArrayList<>();
            for (Album a : allAlbums) allSongs.addAll(api.getSongs(a));

            List<SongEntity> diffSE = db
                    .diffSongs(allSongs.stream().map(song -> new SongEntity(song.getId())).toList());
            List<Song> diffS = diffSE.stream()
                    .map(se -> allSongs.stream().filter(song -> song.getId().equals(se.getId())).findAny().get())
                    .filter(song -> diffA.stream().filter(album -> album.getId().equals(song.getAlbumId())).findAny()
                            .isEmpty())
                    .toList();

            db.storeList(diffAE);
            db.storeList(diffSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
