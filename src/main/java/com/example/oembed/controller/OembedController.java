package com.example.oembed.controller;

import com.example.oembed.domain.OembedResponse;
import com.example.oembed.domain.OembedSearchForm;
import com.example.oembed.service.OembedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class OembedController {

    private final OembedService embedService;

    @GetMapping("/")
    public String searchForm(Model model) {
        model.addAttribute("searchForm", new OembedSearchForm());
        return "searchForm";
    }

    @PostMapping("/")
    public String postMethodName(Model model, @ModelAttribute("searchForm") OembedSearchForm form) throws Exception {
        try {
            String url = form.getUrl();
            String encode = URLEncoder.encode(url, StandardCharsets.UTF_8);
            String oembedUri = embedService.getOembedUri(url, encode);
            OembedResponse oembedResponse = embedService.getOembedResponse(oembedUri);
            model.addAttribute("oembedResponse", oembedResponse);
        } catch (Exception e) {
            throw e;
        }
        return "resultForm";
    }

}
