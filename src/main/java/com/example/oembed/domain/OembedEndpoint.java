package com.example.oembed.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OembedEndpoint {
    private List<String> schemes;
    private String url;
    private String discovery;
}
