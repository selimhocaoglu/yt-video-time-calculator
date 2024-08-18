package com.selimhocaoglu.ytvideotimecalculator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class YoutubeService {
    private final String API_KEY = "";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getVideoDuration(String videoId, Double targetTime, Double playbackSpeed) throws Exception {
        String url = "https://www.googleapis.com/youtube/v3/videos?id=" + videoId + "&key=" + API_KEY + "&part=contentDetails";
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);

        JsonNode responseJson = objectMapper.readTree(result);

        if (responseJson.has("error")) {
            JsonNode error = responseJson.get("error");
            String message = error.get("message").asText();
            throw new IllegalArgumentException(message);
        }

        JsonNode items = responseJson.get("items");
        JsonNode contentDetails = items.get(0).get("contentDetails");

        if (contentDetails == null || !contentDetails.has("duration")) {
            throw new IllegalArgumentException("Video not found or missing duration information.");
        }

        String duration = contentDetails.get("duration").asText();
        int totalSeconds = parseDurationInSeconds(duration);
        String formattedDuration = formatDuration(totalSeconds);

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("Video duration: ").append(formattedDuration);

        if (playbackSpeed != null && playbackSpeed > 0) {
            double adjustedTime = totalSeconds / playbackSpeed;
            String formattedAdjustedTime = formatDuration((int) adjustedTime);
            responseBuilder.append("\nAt ").append(playbackSpeed).append("x speed, the video will take: ")
                    .append(formattedAdjustedTime);
        }

        if (targetTime != null && targetTime > 0) {
            double requiredSpeed = totalSeconds / targetTime;
            responseBuilder.append("\nTo finish in ").append(targetTime).append(" seconds, you need to watch at: ")
                    .append(String.format("%.2f", requiredSpeed)).append("x speed.");
        }

        return responseBuilder.toString();
    }

    private int parseDurationInSeconds(String duration) {
        int hours = 0, minutes = 0, seconds = 0;

        duration = duration.replace("PT", "");
        if (duration.contains("H")) {
            String[] parts = duration.split("H");
            hours = Integer.parseInt(parts[0]);
            duration = parts.length > 1 ? parts[1] : "";
        }
        if (duration.contains("M")) {
            String[] parts = duration.split("M");
            minutes = Integer.parseInt(parts[0]);
            duration = parts.length > 1 ? parts[1] : "";
        }
        if (duration.contains("S")) {
            seconds = Integer.parseInt(duration.replace("S", ""));
        }

        return hours * 3600 + minutes * 60 + seconds;
    }

    private String formatDuration(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder formattedDuration = new StringBuilder();
        if (hours > 0) {
            formattedDuration.append(hours).append(" hours ");
        }
        if (minutes > 0 || hours > 0) {
            formattedDuration.append(minutes).append(" minutes ");
        }
        formattedDuration.append(seconds).append(" seconds");

        return formattedDuration.toString();
    }
}
