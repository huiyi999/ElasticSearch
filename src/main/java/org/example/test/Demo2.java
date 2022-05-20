package org.example.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;

import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.xcontent.json.JsonXContent;
import org.example.entity.Person;
import org.example.utils.ESClient;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : chy
 * @date: 2022-05-18 8:06 p.m.
 */
@Log4j2
public class Demo2 {
    private RestHighLevelClient client = ESClient.getClient();

    String index = "person";
    String type = "man";

    /**
     * "mappings": {
     * "properties": {
     * "type": { "type": "keyword" },
     * "name": { "type": "text" },
     * "user_name": { "type": "keyword" },
     * "email": { "type": "keyword" },
     * "content": { "type": "text" },
     * "tweeted_at": { "type": "date" }
     * }
     * <p>
     * }
     */
    @Test
    public void createIndex() throws IOException {

        //1、 prepare settings for index
        Settings.Builder settings = Settings.builder()
                .put("number_of_shards", 3)
                .put("number_of_replicas", 1);

        //2、 prepare mappings for index
        //   contentBuilder()            <->  "mappings":
        //   .startObject("properties")  <->  "properties":
        XContentBuilder mappings = JsonXContent.contentBuilder()
                .startObject()
                .startObject("properties")
                .startObject("name")
                .field("type", "text")
                .endObject()

                .startObject("age")
                .field("type", "integer")
                .endObject()

                .startObject("birthday")
                .field("type", "date")
                .field("format", "yyyy-MM-dd")
                .endObject()
                .endObject()
                .endObject();

        // 3. encapsulates settings and mappings to a request object
        CreateIndexRequest request = new CreateIndexRequest(index)
                .settings(settings)
                .mapping(type, mappings);

        // 4. uses client object to connect es and execute create index

        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);

        // test
        System.out.println("response: " + response.toString());
    }

    @Test
    public void exists() throws IOException {
        //1 create request object
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);

        // 2 use client to check
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println("response: " + exists);
    }

    @Test
    public void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest();

        request.indices(index);

        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);

        System.out.println("response: " + response);
    }

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void createDoc() throws IOException {

//        1. json data
        Person person = new Person("001", "jason", 23, new Date());
        String json = mapper.writeValueAsString(person);
        System.out.println(json);

//        2. create request object(create id manually)
        IndexRequest indexRequest = new IndexRequest(index, type, person.getId().toString());
        indexRequest.source(json, XContentType.JSON);

//            3、use client to create
        IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);

        System.out.println(response.getResult().toString());
    }

    @Test
    public void updateDoc() throws IOException {
//        create map, set modified content
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "李四");
        String docId = "001";
        // update by id

//        create request object
        UpdateRequest updateRequest = new UpdateRequest(index, type, docId);
        updateRequest.doc(map);

        UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update.getResult().toString());
    }

    @Test
    public void deleteDoc() throws IOException {
        String docId = "001";
        //        create request object
        DeleteRequest deleteRequest = new DeleteRequest(index, type, docId);

        DeleteResponse delete = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.getResult().toString());
    }

    // batch processing
    @Test
    public void bulkCreateDoc() throws IOException {
        Person p1 = new Person("001", "jason1", 21, new Date());
        Person p2 = new Person("002", "jason2", 22, new Date());
        Person p3 = new Person("003", "jason3", 23, new Date());
        Person p4 = new Person("004", "jason4", 24, new Date());

        String json1 = mapper.writeValueAsString(p1);
        String json2 = mapper.writeValueAsString(p2);
        String json3 = mapper.writeValueAsString(p3);
        String json4 = mapper.writeValueAsString(p4);

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest(index, type, p1.getId()).source(json1, XContentType.JSON));
        bulkRequest.add(new IndexRequest(index, type, p2.getId()).source(json2, XContentType.JSON));
        bulkRequest.add(new IndexRequest(index, type, p3.getId()).source(json3, XContentType.JSON));
        bulkRequest.add(new IndexRequest(index, type, p4.getId()).source(json4, XContentType.JSON));

        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }

    @Test
    public void bulkDeleteDoc() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new DeleteRequest(index, type, "0001"));
        bulkRequest.add(new DeleteRequest(index, type, "0002"));
        BulkResponse delete = client.bulk(bulkRequest, RequestOptions.DEFAULT);
    }
}
