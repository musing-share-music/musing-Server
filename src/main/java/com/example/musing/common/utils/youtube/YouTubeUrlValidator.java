package com.example.musing.common.utils.youtube;

import java.util.regex.*;

public class YouTubeUrlValidator {

    // YouTube URL을 검증하기 위한 정규식
    private static final String YOUTUBE_URL_REGEX =
            "^(https?://)?(www\\.)?(youtube|youtu|youtube-nocookie)\\.(com|be)/.+$";

    // YouTube 영상 ID 추출
    private static final String YOUTUBE_ID_REGEX = "(?<=watch\\?v=|/videos\\/)([a-zA-Z0-9_-]+)";

    public static boolean isValidYouTubeUrl(String url) {
        Pattern pattern = Pattern.compile(YOUTUBE_URL_REGEX);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    public static String extractYouTubeVideoId(String url) {
        Pattern pattern = Pattern.compile(YOUTUBE_ID_REGEX);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;  // URL에서 ID를 추출할 수 없는 경우
    }

    public static String generateEmbedUrl(String videoId) {
        return "https://www.youtube.com/embed/" + videoId;
    }
}