package uz.kundalik.site.util;// package uz.orom.site.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class YouTubeUrlUtil {
    // Bu regex turli xil YouTube URL formatlarini (watch?v=, youtu.be/, embed/) qo'llab-quvvatlaydi
    private static final String YOUTUBE_ID_PATTERN = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2Fvideos%2F|youtu.be%2F)[^#\\&\\?]*";
    private static final Pattern compiledPattern = Pattern.compile(YOUTUBE_ID_PATTERN);

    private YouTubeUrlUtil() {}

    public static String extractVideoId(String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.trim().isEmpty()) {
            return null;
        }
        Matcher matcher = compiledPattern.matcher(youtubeUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}