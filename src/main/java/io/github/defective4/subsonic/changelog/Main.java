package io.github.defective4.subsonic.changelog;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import io.github.defective4.subsonic.changelog.api.SubsonicAPI;
import io.github.defective4.subsonic.changelog.db.IndexDatabase;
import io.github.defective4.subsonic.changelog.db.model.AlbumEntity;
import io.github.defective4.subsonic.changelog.io.IOUtils;
import io.github.defective4.subsonic.changelog.model.Album;
import io.github.defective4.subsonic.changelog.model.FriendlyAlbum;
import jakarta.mail.Authenticator;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class Main {
    public static void main(String[] args) {
        try {

            if (args.length < 9) {
                System.err.println(
                        "Usage: <url> <username> <password> <smtp host> <smtp user> <smtp passwword> <emails list file> <sender name> <sender mail>");
                System.exit(1);
            }

            URL url = URI.create(args[0]).toURL();

            IndexDatabase db = new IndexDatabase();
            SubsonicAPI api = new SubsonicAPI(url + "/rest/", args[1], args[2]);

            List<Album> allAlbums = api.getAlbums();
            List<AlbumEntity> diffAE = db
                    .diffAlbums(allAlbums.stream().map(a -> new AlbumEntity(a.getId(), a.getSongCount())).toList());
            List<Album> diffA = diffAE.stream()
                    .map(e -> allAlbums.stream().filter(e2 -> e2.getId().equals(e.getId())).findAny().get()).toList();

            if(diffA.isEmpty()) return;

            Map<String, FriendlyAlbum> albumMap = new HashMap<>();
            for (Album a : diffA) {
                if (!albumMap.containsKey(a.getTitle())) {
                    String title = a.getTitle();
                    List<String> artists = List.of(a.getArtist());
                    BufferedImage art = api.getCoverArt(a.getId(), 128);
                    albumMap.put(a.getTitle(), new FriendlyAlbum(title, artists, art, a.getId()));
                } else
                    albumMap.get(a.getTitle()).addAuthor(a.getArtist());
            }

            List<FriendlyAlbum> albumList = albumMap.values().stream().limit(10).toList();
            String mailTemplate = IOUtils.readTemplate("mail-template.html");
            String mailAlbumTemplate = IOUtils.readTemplate("mail-album.html");

            StringBuilder mailAlbums = new StringBuilder();
            Encoder encoder = Base64.getEncoder();
            for (FriendlyAlbum album : albumList) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                ImageIO.write(album.getCover(), "png", buffer);
                mailAlbums.append(mailAlbumTemplate.formatted(album.getId() + ".png",
                        encoder.encodeToString(buffer.toByteArray()), album.getId(), album.getTitle(),
                        String.join(", ", album.getAuthors().toArray(new String[0])))).append("\n");
            }

            Properties mailProps = new Properties();
            mailProps.setProperty("mail.transport.protocol", "smtp");
            mailProps.setProperty("mail.host", args[3]);
            mailProps.setProperty("mail.user", args[4]);

            Session ses = Session.getInstance(mailProps, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(args[4], args[5]);
                }
            });

            for (String mail : Files.readAllLines(Path.of(args[6]))) {
                MimeMessage msg = new MimeMessage(ses);
                msg.setFrom(String.format("%s <%s>", args[7], args[8]));
                msg.setSubject("Album updates");
                msg.setRecipient(RecipientType.TO, new InternetAddress(mail));

                String name = mail.substring(0, mail.indexOf('@'));

                MimeBodyPart part = new MimeBodyPart();
                part.setContent(
                        mailTemplate.formatted(Character.toUpperCase(name.charAt(0)) + name.substring(1), mailAlbums),
                        "text/html; charset=utf-8");

                MimeMultipart multipart = new MimeMultipart();
                multipart.addBodyPart(part);

                msg.setContent(multipart);
                Transport.send(msg);
            }

            db.storeList(diffAE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
