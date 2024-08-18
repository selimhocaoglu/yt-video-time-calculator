package com.selimhocaoglu.ytvideotimecalculator.controller;

import com.selimhocaoglu.ytvideotimecalculator.service.YoutubeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class YoutubeController {

    private final YoutubeService youtubeService;

    public YoutubeController(YoutubeService youtubeService) {
        this.youtubeService = youtubeService;
    }

    @GetMapping("/video-duration")
    public ResponseEntity<String> getVideoDuration(@RequestParam String videoId,
                                                   @RequestParam(required = false) Double targetTime,
                                                   @RequestParam(required = false) Double playbackSpeed) {
        try {
            String response = youtubeService.getVideoDuration(videoId, targetTime, playbackSpeed);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while processing the request.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
