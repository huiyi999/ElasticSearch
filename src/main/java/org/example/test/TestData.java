package org.example.test;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.xcontent.json.JsonXContent;
import org.example.entity.SmsLogs;
import org.example.utils.ESClient;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : chy
 * @date: 2022-05-19 7:07 p.m.
 */
public class TestData {
    RestHighLevelClient client = ESClient.getClient();
    String index = "sms_logs_index";
    String type = "sms_logs_type";
    ObjectMapper mapper= new ObjectMapper();

    @Test
    public void createSmsLogsIndex() throws IOException {
        Settings.Builder settings = Settings.builder()
                .put("number_of_shards", 3)
                .put("number_of_replicas", 1);

        XContentBuilder mappings = JsonXContent.contentBuilder()
                .startObject()
                .startObject("properties")
                .startObject("createDate")
                .field("type", "text")
                .endObject()
                .startObject("sendDate")
                .field("type", "date")
                .field("format", "yyyy-MM-dd")
                .endObject()
                .startObject("longCode")
                .field("type", "text")
                .endObject()
                .startObject("mobile")
                .field("type", "text")
                .endObject()
                .startObject("corpName")
                .field("type", "text")
                .field("analyzer", "ik_max_word")
                .endObject()
                .startObject("smsContent")
                .field("type", "text")
                .field("analyzer", "ik_max_word")
                .endObject()
                .startObject("state")
                .field("type", "integer")
                .endObject()
                .startObject("operatorId")
                .field("type", "integer")
                .endObject()
                .startObject("province")
                .field("type", "text")
                .endObject()
                .startObject("ipAddr")
                .field("type", "text")
                .endObject()
                .startObject("replyTotal")
                .field("type", "integer")
                .endObject()
                .startObject("fee")
                .field("type", "integer")
                .endObject()
                .endObject()
                .endObject();

        CreateIndexRequest request = new CreateIndexRequest(index)
                .settings(settings)
                .mapping(type, mappings);

        RestHighLevelClient client = ESClient.getClient();
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }

    @Test
    public void addData() throws IOException, InterruptedException {
        /**
         * add data
         * PUT /sms_logs_index/_doc/0001
         * {
         *   "corpName": "途虎养车",
         *   "createDate": "2020-01-22",
         *   "fee": 1,
         *   "ipAddr": "10.123.98.0",
         *   "longCode": 106900000009,
         *   "mobile": "1738989222222",
         *   "operatorid": 2,
         *   "province": "河北",
         *   "relyTotal": 10,
         *   "sendDate": "2020-02-22",
         *   "smsContext": "【途虎养车】亲爱的灯先生，您的爱车已经购买",
         *   "state": 0
         * }
         */

        String index = "sms-logs-index";
        String type = "sms-logs-type";
        String longcode = "1008687";
        String mobile = "138340658";

        List<String> companies = new ArrayList<>();
        companies.add("腾讯课堂");
        companies.add("阿里旺旺");
        companies.add("海尔电器");
        companies.add("海尔智家公司");
        companies.add("格力汽车");
        companies.add("苏宁易购");

        List<String> provinces = new ArrayList<>();
        provinces.add("北京");
        provinces.add("重庆");
        provinces.add("上海");
        provinces.add("晋城");

        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 1; i < 16; i++) {
            Thread.sleep(1000);
            SmsLogs s1 = new SmsLogs();
            s1.setId(i);
            s1.setCreateDate(new Date());
            s1.setSendDate(new Date());
            s1.setLongCode(longcode + i);
            s1.setMobile(mobile + 2 * i);
            s1.setCorpName(companies.get(i % 5));
            s1.setSmsContent(SmsLogs.doc.substring((i - 1) * 100, i * 100));
            s1.setState(i % 2);
            s1.setOperatorId(i % 3);
            s1.setProvince(provinces.get(i % 4));
            s1.setIpAddr("127.0.0." + i);
            s1.setReplyTotal(i * 3);
            s1.setFee(i * 6 );

            String json1 = mapper.writeValueAsString(s1);
            bulkRequest.add(new IndexRequest(index, type, s1.getId().toString()).source(json1, XContentType.JSON));
            System.out.println("数据" + i + s1.toString());

            BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        }
    }
}
