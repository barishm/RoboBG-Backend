package com.robobg.controller;

import com.robobg.dtos.YoutubeVideosDTO;
import com.robobg.service.impl.YoutubeServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/youtube")
public class YoutubeController {

    private final YoutubeServiceImpl youtubeService;

    public YoutubeController(YoutubeServiceImpl youtubeService) {
        this.youtubeService = youtubeService;
    }

    @GetMapping
    public List<YoutubeVideosDTO> returnLast4Videos() {
        return youtubeService.getLast4Videos();
    }
}
