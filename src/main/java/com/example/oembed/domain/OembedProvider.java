package com.example.oembed.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OembedProvider {
    private String providerName;
    private String providerUrl;
    private List<OembedEndpoint> endpoints;
}
