package com.example.oembed.service;

import com.example.oembed.domain.OembedEndpoint;
import com.example.oembed.domain.OembedProvider;
import com.example.oembed.domain.OembedResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OembedService {

    private static Map<String, OembedProvider> providers = new HashMap<>();

    /**
     * providers 값을 담아주기 위한 함수
     * @throws IOException
     */
    @PostConstruct
    public void init() throws IOException  {
        // http client 생성
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            // get 메서드와 URL 설정
            HttpGet httpGet = new HttpGet("https://oembed.com/providers.json");
            // agent 정보 설정
            httpGet.addHeader("Content-type", "application/json");
            // get 요청
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            Gson create = new GsonBuilder()
                    .setLenient()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES) //_로 표시
                    .create();

            String jsonString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            Type type = new TypeToken<List<OembedProvider>>() {}.getType();
            List<OembedProvider> oembedRequestProviders = create.fromJson(jsonString, type);
            providers.putAll(
                    oembedRequestProviders.stream()
                            .collect(Collectors.toMap(OembedProvider::getProviderUrl, Function.identity())));
        } catch (Exception e) {
        } finally {
            httpClient.close();
        }
    }

    /**
     * @param paramUrl ex. https://vimeo.com/20097015
     * @return ex. vimeo
     * @throws MalformedURLException
     */
    public String getProviderName(String paramUrl) throws MalformedURLException  {
        URL url = new URL(paramUrl);
        String result = "";
        String[] split = url.getHost().split("\\.");
        // split 데이터가 2개일 경우
        if (split.length == 2) {
            result = split[0];
        } else if (split.length == 3) {
            result = split[1];
        }
        return result;
    }

    /**
     * @param requestUrl ex. https://vimeo.com/20097015
     * @param encode UTF-8
     * @return ex. https://vimeo.com/api/oembed?format=json&url=https://vimeo.com/20097015
     * @throws MalformedURLException //URL, 프로토콜을 다루는 클래스에서 잘못된 인자로 프로토콜을 인식할수 없을떄 예외
     */
    public String getOembedUri(String requestUrl, String encode) throws MalformedURLException  {
        final String providerName = this.getProviderName(requestUrl);

        OembedEndpoint endpoint = providers.entrySet()
                .stream()
                .filter(v->v.getKey().contains(providerName))
                .findFirst()
                .map(x->x.getValue().getEndpoints().get(0)).orElse(null);

        String endpointUrl="";
        if(endpoint!=null){
            endpointUrl = endpoint.getUrl();
        }
        if (endpointUrl.contains("oembed.")) {
            // provider endpoint url + ?format=json&url= + encode data
            if (endpointUrl.contains("{format}")) {
                endpointUrl = endpointUrl.replace("{format}", "json");
            }
            endpointUrl = endpointUrl + "?url=" + encode;
        } else {
            endpointUrl = endpointUrl + "?format=json&url=" + encode;
        }
        return endpointUrl;
    }
    /**
     * @param oembedUrl ex. https://vimeo.com/api/oembed?format=json&url=https://vimeo.com/20097015
     * @return OembedResponse
     * @throws Exception
     */
    public OembedResponse getOembedResponse(String oembedUrl) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            //  헤더에 json형식으로 엔티티에 저장
            HttpGet httpGet = new HttpGet(oembedUrl);
            httpGet.addHeader("Content-type", "application/json");
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(entity,"UTF-8");
            Gson create = new GsonBuilder()
                    .setLenient()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
            return create.fromJson(responseString, OembedResponse.class);
        } catch (Exception e) {
            throw e;
        } finally {
            httpClient.close();
        }
    }
}
