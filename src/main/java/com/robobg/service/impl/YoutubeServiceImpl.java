package com.robobg.service.impl;

import com.robobg.dtos.YoutubeVideosDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
public class YoutubeServiceImpl {
    @Value("${YOUTUBE_API_KEY}")
    private String youtubeApiKey;
    @Value("${CHANNEL_ID}")
    private String channelId;
    private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/playlistItems";


    @Cacheable(value = "youtubeVideosCache")
    public List<YoutubeVideosDTO> getLast4Videos() {
        System.out.println("Cache miss: fetching videos from YouTube API...");
        List<YoutubeVideosDTO> youtubeVideosDTOList = new ArrayList<>();

        String uploadsPlaylistId = "UU" + channelId.substring(2); // Convert channel ID to uploads playlist ID

        UriComponentsBuilder builder = fromHttpUrl(YOUTUBE_API_URL)
                .queryParam("part", "snippet")
                .queryParam("maxResults", "4")
                .queryParam("playlistId", uploadsPlaylistId)
                .queryParam("key", youtubeApiKey);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(builder.toUriString(), Map.class);

        if (response != null && response.containsKey("items")) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

            for (Map<String, Object> item : items) {
                Map<String, Object> snippet = Collections.unmodifiableMap((Map<String, Object>) item.get("snippet"));
                if (snippet.containsKey("resourceId")) {
                    Map<String, Object> resourceId = (Map<String, Object>) snippet.get("resourceId");
                    String videoId = (String) resourceId.get("videoId");

                    if (videoId != null) {
                        YoutubeVideosDTO dto = new YoutubeVideosDTO();
                        dto.setVideoId(videoId);
                        youtubeVideosDTOList.add(dto);
                    }
                }
            }
        }

        return youtubeVideosDTOList;
    }

}

