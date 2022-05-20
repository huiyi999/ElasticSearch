package org.example.test;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.example.utils.ESClient;
import org.junit.Test;
/**
 * @author : chy
 * @date: 2022-05-18 8:00 p.m.
 */
public class Demo1 {

    @Test
    public void testConnection(){
        RestHighLevelClient client = ESClient.getClient();
        System.out.println("OK!");

    }

}
