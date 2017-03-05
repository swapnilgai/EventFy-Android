package com.java.eventfy.utils;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 3/1/17.
 */
public class GetHttpRequestFactory extends RestTemplate {

    private SimpleClientHttpRequestFactory factory;

    private static GetHttpRequestFactory ourInstance = new GetHttpRequestFactory();

    public static GetHttpRequestFactory getInstance() {
        return ourInstance;
    }

    private GetHttpRequestFactory() {
        factory =  new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(2000);
        factory.setConnectTimeout(2000);
    }

    public SimpleClientHttpRequestFactory getRequestFactory(){
        return factory;
    }
}
