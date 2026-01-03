package io.github.defective4.subsonic.changelog.api;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import io.github.defective4.subsonic.changelog.model.Album;
import io.github.defective4.subsonic.changelog.model.Song;
import io.github.defective4.subsonic.changelog.model.SubsonicResponse;

public class SubsonicAPI {
    private static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private final String baseURL;
    private final Gson gson = new Gson();

    private final MessageDigest md;
    private final String password;
    private final SecureRandom random = new SecureRandom();
    private String token, salt;
    private final String user;

    public SubsonicAPI(String baseURL, String user, String password) throws NoSuchAlgorithmException {
        this.baseURL = baseURL;
        this.user = user;
        this.password = password;
        md = MessageDigest.getInstance("md5");
    }

    public List<Album> getAlbums() throws IOException {
        List<Album> albums = new ArrayList<>();
        int offset = 0;
        while (true) {
            try (Reader reader = openReader("getAlbumList", Map.of("type", "newest", "size", 500, "offset", offset))) {
                List<Album> list = readResponse(reader).getAlbumList().getAlbum();
                if (list == null || list.isEmpty()) break;
                offset += 500;
                albums.addAll(list);
            }
        }
        return Collections.unmodifiableList(albums);
    }

    public BufferedImage getCoverArt(String id, int size) throws IOException {
        try (InputStream in = openStream("getCoverArt", Map.of("id", id, "size", size))) {
            return ImageIO.read(in);
        }
    }

    public List<Song> getSongs(Album album) throws IOException {
        try (Reader reader = openReader("getMusicDirectory", Map.of("id", album.getId()))) {
            return readResponse(reader).getDirectory().getChild();
        }
    }

    private Reader openReader(String path, Map<String, Object> params) throws IOException {
        return new InputStreamReader(openStream(path, params));
    }

    private InputStream openStream(String path, Map<String, Object> params) throws IOException {
        regenerateToken();
        List<String> pms = new ArrayList<>();
        for (Entry<String, Object> entry : params.entrySet()) {
            pms.add(entry.getKey() + "=" + ue(String.valueOf(entry.getValue())));
        }
        URL url = URI
                .create(String.format(baseURL + path + "?u=%s&t=%s&s=%s&v=%s&c=CHANGELOG&f=json%s", ue(user), token,
                        salt, "1.16.1", pms.isEmpty() ? "" : "&" + String.join("&", pms.toArray(new String[0]))))
                .toURL();
        return url.openStream();
    }

    private SubsonicResponse readResponse(Reader reader) {
        SubsonicResponse resp = gson.fromJson(JsonParser.parseReader(reader).getAsJsonObject().get("subsonic-response"),
                SubsonicResponse.class);
        resp.validate();
        return resp;
    }

    private void regenerateToken() {
        StringBuilder saltBuilder = new StringBuilder(10);
        for (int i = 0; i < saltBuilder.capacity(); i++) saltBuilder.append(CHARS[random.nextInt(CHARS.length)]);
        salt = saltBuilder.toString();
        md.reset();
        md.update(password.getBytes());
        md.update(salt.getBytes());
        StringBuffer tokenBuilder = new StringBuffer();
        for (byte b : md.digest()) {
            String s = Integer.toHexString(b & 0xff);
            if (s.length() == 1) tokenBuilder.append("0");
            tokenBuilder.append(s);
        }
        token = tokenBuilder.toString();
    }

    private static String ue(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
}
