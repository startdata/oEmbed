package com.example.oembed.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OembedResponse {
    private String title;
    private String type;
    private String version;
    private String providerName;
    private String providerUrl;
    private String authorName;
    private String authorUrl;
    private String isPlus;
    private String html;
    private String width;
    private String height;
    private String duration;
    private String description;
    private String thumbnailUrl;
    private String thumbnailWidth;
    private String thumbnailHeight;
    private String thumbnailUrlWithPlayButton;
    private String uploadDate;
    private String videoId;
    private String uri;
}