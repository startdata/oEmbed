package com.example.oembed.controller;

import com.example.oembed.domain.OembedResponse;
import com.example.oembed.domain.OembedSearchForm;
import com.example.oembed.service.OembedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.MalformedURLException;
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
    public String postMethodName(Model model, @Validated @ModelAttribute("searchForm") OembedSearchForm form,
                                 BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            return "searchForm";
        }

        try {
            String url = form.getUrl();
            String encode = URLEncoder.encode(url, StandardCharsets.UTF_8);
            String oembedUri = embedService.getOembedUri(url, encode);
            OembedResponse oembedResponse = embedService.getOembedResponse(oembedUri);
            model.addAttribute("oembedResponse", oembedResponse);
        } catch (MalformedURLException me) {
            bindingResult.reject("malformedUrl", null, "잘못된 URL입니다.");
            return "searchForm";
        } catch (Exception e) {
            bindingResult.reject("", null, "잘못된 접근입니다.");
            return "searchForm";
        }
        return "resultForm";
    }

}
