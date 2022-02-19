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

    //@ModelAttribute 강제로 전달받은 파라미터를 Model에 담아서 전달하도록 할 때 필요한 어노테이션
    //타입에 관계없이 무조건 Model에 담아서 전달되므로, 파라미터로 전달된 데이터를 다시 화면에서 사용해야 할 경우에 유용하게 사용
    @PostMapping("/")
    public String postMethodName(Model model, @ModelAttribute("searchForm") OembedSearchForm form) throws Exception {
        try {
            String url = form.getUrl();
            // StandardCharsets == charset(컴퓨터에서 문자를 표현하기 위해, 각 문자를 정수값에 대응시켜 놓은체계)를 포함 할 상수를 정의
            String encode = URLEncoder.encode(url, "UTF-8"); //StandardCharsets.UTF_8
            String oembedUri = embedService.getOembedUri(url, encode);
            OembedResponse oembedResponse = embedService.getOembedResponse(oembedUri);
            model.addAttribute("oembedResponse", oembedResponse); //html에 oembedResponse안에 있는 데이터가 보이도록 한다.
        } catch (Exception e) {
            throw e;
        }
        return "resultForm";
    }

}
