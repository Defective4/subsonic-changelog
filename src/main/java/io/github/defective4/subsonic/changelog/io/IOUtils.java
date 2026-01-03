package io.github.defective4.subsonic.changelog.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class IOUtils {
    private IOUtils() {}

    public static String readTemplate(String res) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (Reader reader = new InputStreamReader(IOUtils.class.getResourceAsStream("/" + res),
                StandardCharsets.UTF_8)) {
            while (true) {
                int r = reader.read();
                if (r < 0) break;
                builder.append((char) r);
            }
        }

        return builder.toString();
    }
}
