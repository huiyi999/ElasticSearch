package org.example.utils;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author : chy
 * @date: 2022-05-18 7:56 p.m.
 */
public class ESClient {

    public static RestHighLevelClient getClient() {
        // create HttpHost object
        HttpHost httpHost = new HttpHost("localhost", 9200);
        // HttpHost httpHost = new HttpHost("127.0.0.1", 9200);


        RestClientBuilder builder = RestClient.builder(httpHost);

        RestHighLevelClient client = new RestHighLevelClient(builder);


        return client;
    }
}
