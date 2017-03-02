package com.java.eventfy.utils;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 * Created by swapnil on 3/1/17.
 */
public class GetHttpRequestFactory  {

    private HttpComponentsClientHttpRequestFactory factory;

    private static GetHttpRequestFactory ourInstance = new GetHttpRequestFactory();

    public static GetHttpRequestFactory getInstance() {
        return ourInstance;
    }

    private GetHttpRequestFactory() {
        factory =  new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(2000);
        factory.setConnectTimeout(2000);
    }

    public HttpComponentsClientHttpRequestFactory getRequestFactory(){
        return factory;
    }
}
