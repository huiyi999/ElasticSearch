# ElasticSearch

# 1. ElasticSearch Introduction

> - a real-time **distributed** and open source **full-text search** and analytics engine
> - ES VS Solr
> - concepts: node, cluster, index, document, shard, replicas, 



## 1.1 Comparison between Elasticsearch and RDBMS

![Screen Shot 2022-05-18 at 12.55.51 PM](/Users/chy/Library/Application Support/typora-user-images/Screen Shot 2022-05-18 at 12.55.51 PM.png)

## 1.2 Install ES & Kibana

```
docker run --name es-01 --net elastic -p 9200:9200 -p 9300:9300 -it elasticsearch:8.2.0 
docker run --name kib-01 --net elastic -p 5601:5601 kibana:8.2.0 
Kibana enrollment token


docker run -d --name es-01  -p 9200:9200 -p 9300:9300 --net elastic -e "discovery.type=single-node" -it elasticsearch:7.17.1 
docker run -d --name kib-01 --net elastic -p 5601:5601 kibana:7.17.1 

```



```sh
docker-compose up
```



```yml
version: '3.7'

services:
  elasticsearch:
    image: elasticsearch:7.4.0
    ports:
      - '9200:9200'
    environment:
      - "discovery.type=single-node"
      - "COMPOSE_PROJECT_NAME=elasticsearch-server"
    restart: always
    ulimits:
      memlock:
        soft: -1
        hard: -1
      nofile:
        soft: 65536
        hard: 65536
    cap_add:
      - IPC_LOCK
  kibana:
    container_name: kibana
    image: kibana:7.4.0
    ports:
      - '5601:5601'
    restart: always
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    depends_on:
      - elasticsearch

```

![Screen Shot 2022-05-19 at 7.47.50 PM](/Users/chy/Desktop/Screen Shot 2022-05-19 at 7.47.50 PM.png)

## 1.3 Install IK Tokenizer

> download IK tokenizer : https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.4.0/elasticsearch-analysis-ik-7.4.0.zip
>
> https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.17.1/elasticsearch-analysis-ik-7.17.1.zip

> Enter into ES container
>
> ```
> docker exec -it c1 bash
> ```
>
> , turn to /bin,  use command to install
>
> ```
>  ./elasticsearch-plugin install path_url
> ```
>
> Restart ES container

![Screen Shot 2022-05-18 at 4.04.16 PM](/Users/chy/Library/Application Support/typora-user-images/Screen Shot 2022-05-18 at 4.04.16 PM.png)



# 2. Elasticsearch Operations

## 2.1 ES framework

### 2.1.1 Index

> In ES service, can create several index.
>
> each index is divided into 5 slices by default

![Screen Shot 2022-05-18 at 4.13.00 PM](/Users/chy/Library/Application Support/typora-user-images/Screen Shot 2022-05-18 at 4.13.00 PM.png)

### 2.1.2 Type

> According to version...

![Screen Shot 2022-05-18 at 4.15.33 PM](/Users/chy/Library/Application Support/typora-user-images/Screen Shot 2022-05-18 at 4.15.33 PM.png)

### 2.1.3 Doc

> in one type, have several docs, similar to many rows data in DB

![Screen Shot 2022-05-18 at 4.17.09 PM](/Users/chy/Library/Application Support/typora-user-images/Screen Shot 2022-05-18 at 4.17.09 PM.png)



### 2.1.4 Field

> in one Doc, have several fields, similar to many column in one row data in DB

![Screen Shot 2022-05-18 at 4.17.56 PM](/Users/chy/Library/Application Support/typora-user-images/Screen Shot 2022-05-18 at 4.17.56 PM.png)



## 2.2 RESTful Syntax

> **GET:** 
>
> search index: http://ip:port/index
>
> Search doc: http://ip:port/index/type/doc_id
>
> **POST:**
>
> search doc: http://ip:port/index/type/_search 
>
> update doc: http://ip:port/index/type/doc_id/_search 
>
> **PUT:**
>
> Create an index :http://ip:port/index
>
> Create an index with doc attribute : http://ip:port/index/type/_mappings
>
> **DELETE:**
>
> delete index: http://ip:port/index
>
> delete doc: http://ip:port/index/type/doc_id

## 2.3 Index Operations

### 2.3.1 Create index

```
# create an index
PUT /person
{
  "settings": {
    "number_of_shards": 5,
    "number_of_replicas": 1
  }
}
```

### 2.3.2 Check index info

```
# 1. use GET
GET /person
```

> 2. Index management

![Screen Shot 2022-05-18 at 4.29.43 PM](/Users/chy/Library/Application Support/typora-user-images/Screen Shot 2022-05-18 at 4.29.43 PM.png)



### 2.3.3 Delete index

> 1. Index management
> 2. use DELETE

```
DELETE /person
```



## 2.4 ES Field type

> https://www.elastic.co/guide/en/elasticsearch/reference/7.4/mapping.html
>
> Field datatypes



## 2.5 Create index and decide DS

> there is no type in ES 7.4.0; so the following code is error, it can't create "novel" type

```
# error ES6
PUT /book
{
  "settings": {
    "number_of_replicas": 1,
   	"number_of_shards": 5
  },
  "mappings": {
    "novel": {
      "properties": {
        "name": {
          "type": "text",
          "analyzer": "ik_max_word",
          "index": true
        },
        "authoor": {
          "type": "keyword"
        },
        "onsale": {
          "type": "date",
          "format": "yyyy-MM-dd"
        },
        "descr": {
          "type": "text"
        }
      }
    }
  }
}

# ES7
PUT /book
{
  "settings": {
    "number_of_replicas": 1,
   	"number_of_shards": 5
  },
  "mappings": {
      "properties": {
        "name": {
          "type": "text",
          "analyzer": "ik_max_word",
          "index": true
        },
        "authoor": {
          "type": "keyword"
        },
        "onsale": {
          "type": "date",
          "format": "yyyy-MM-dd"
        },
        "descr": {
          "type": "text"
        }
      
    }
  }
}
```



```
# create a new index with an explicit mapping 
PUT /my-index
{
  "mappings": {
    "properties": {
      "age":    { "type": "integer" },  
      "email":  { "type": "keyword"  }, 
      "name":   { "type": "text"  }     
    }
  }
}

# use the put mapping API to add one or more new fields to an existing index
PUT /my-index/_mapping
{
  "properties": {
    "employee-id": {
      "type": "keyword",
      "index": false
    }
  }
}

# view the mapping of an existing index.
GET /my-index/_mapping
```

## 2.6 Doc Operations

> Removal of mapping types in 7.x.
>
> Alternative to mapping types:
>
> 1. Index per document type
> 2. Custom type filed

### 2.6.1 create document

```

# create document
PUT twitter
{
  "mappings": {
      "properties": {
        "type": { "type": "keyword" }, 
        "name": { "type": "text" },
        "user_name": { "type": "keyword" },
        "email": { "type": "keyword" },
        "content": { "type": "text" },
        "tweeted_at": { "type": "date" }
      }
    
  }
}

```

### 2.6.2 Create docID

```

# create docID(id=user-kimchy, type=_doc)
# automatically generate id:   PUT test/_doc{}
PUT twitter/_doc/user-kimchy
{
  "type": "user", 
  "name": "Shay Banon",
  "user_name": "kimchy",
  "email": "shay@kimchy.com"
}

PUT twitter/_doc/tweet-1
{
  "type": "tweet", 
  "user_name": "kimchy",
  "tweeted_at": "2017-10-24T09:00:00Z",
  "content": "Types are going away"
}
```

### 2.6.3 Update doc

```
# cover data
POST twitter/_doc/user-kimchy
{
  "type": "user", 
  "name": "Shay Banon 1",
  "user_name": "kimchy",
  "email": "shay@kimchy.com"
}

# update some fields
# use the endpoint /{index}/_update/{id} 
POST twitter/_update/user-kimchy
{
  "doc":{
    "name":"Shay Banon111"
  }
}
```

### 2.6.4 Search 

```
GET twitter/_search
{
  "query": {
    "bool": {
      "must": {
        "match": {
          "user_name": "kimchy"
        }
      },
      "filter": {
        "match": {
          "type": "tweet" 
        }
      }
    }
  }
}

 # filter field
  "_source":["name","age"],
  # sorting
  "sort": [
    {
      "age": {
        "order": "desc"
      }
    }
  ], 
  # pagination
  "from": 0,
  "size": 2
```

### 2.6.5 Delete

```
DELETE /twitter/_doc/user-kimchy
```



# 3. Elasticsearch With Java

> https://github.com/pengwenclown/ESDemo
>
> https://github.com/DW-Zhou/ES-6.5.4/blob/master/ElasticSearch%E7%9F%A5%E8%AF%86%E7%82%B9.md

## 3.1 Java connect ES

```
# 1. create maven project
# 2. import dependency
# 3. create test class, connect ES

// create connection, util
public class ESClient {

    public static RestHighLevelClient getClient(){
//        create HttpHost object
        HttpHost httpHost = new HttpHost("127.0.0.1",9200);
//      create RestClientBuilder
        RestClientBuilder builder = RestClient.builder(httpHost);
        //      create RestHighLevelClien object
        RestHighLevelClient client = new RestHighLevelClient(builder);

        return client;
    }
}
```



## 3.2 Java create index

```
public class Create_ES_Index {
    String index = "person";
    String type = "man";
    @Test
    public void createIndex() throws IOException {
     //1、 准备关于索引的settings
        Settings.Builder settings = Settings.builder()
                .put("number_of_shards", 3)
                .put("number_of_replicas", 1);


        //2、 准备关于索引的结构mappings
        XContentBuilder mappings = JsonXContent.contentBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("name")
                            .field("type","text")
                        .endObject()
                        .startObject("age")
                            .field("type","integer")
                        .endObject()
                        .startObject("birthday")
                             .field("type","date")
                             .field("format","yyyy-MM-dd")
                        .endObject()
                    .endObject()
                .endObject();

        //2 将settings 和 mappings封装成一个request对象
        CreateIndexRequest request = new CreateIndexRequest(index)
                .settings(settings)
                .mapping(type,mappings);
        //3   通过client对象去链接es并执行创建索引
        RestHighLevelClient client = ESClient.getClient();
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);

        //测试
        System.out.println("response"+response.toString());

    }
```



## 3.3 Java check ifExist & delete

```
   @Test
    public void exists() throws IOException {
        //1 request object
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
        // 2 通过client去检查
        RestHighLevelClient client = ESClient.getClient();
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }
```





# 4. Elasticsearch Search

## 4.1 term & terms Search

> ```
> term的查询是代表完全匹配，搜索之前不会对你的关键字进行分词
> terms是针对一个字段包含多个值 运用
> ```



## 4.2 match query

> ```
> * match查询属于高层查询，它会根据你查询字段类型不一样，采用不同的查询方式
> *
> * match查询，实际底层就是多个term查询，将多个term查询的结果进行了封装
> *
> * 1. 查询的如果是日期或者是数值的话，它会根据你的字符串查询内容转换为日期或者是数值对等
> *
> * 2. 如果查询的内容是一个不可被分的内容（keyword），match查询不会对你的查询的关键字进行分词   == term
> *
> * 3. 如果查询的内容是一个可被分的内容（text）,match则会根据指定的查询内容按照一定的分词规则去分词进行查询
> ```



## 4.3 other query

> 1. id
> 2. ids
> 3. prefix
> 4. fuzzy
> 5. wildcard
> 6. range
> 7. regexp

## 4.4 Scroll

> ES对from+size有限制，from和size两者之和不能超过1w
>
> 原理：
>
> from+size  ES查询数据的方式：
>
> 1. 先将用户指定的关键词进行分词处理
>
> 2. 将分词去词库中进行检索，得到多个文档的id
> 3. 去各个分片中拉去指定的数据   耗时
> 4. 根据数据的得分进行排序       耗时
> 5. 根据from的值，将查询到的数据舍弃一部分，
> 6. 返回查询结果
>
> Scroll+size (deep pagination)    在ES中查询方式
>
> 1. 先将用户指定的关键词进行分词处理
> 2. 将分词去词库中进行检索，得到多个文档的id
> 3. **将文档的id存放在一个ES的上下文中，ES内存**
> 4. 根据你指定给的size的个数去ES中检索指定个数的数据，拿完数据的文档id,会从上下文中移除
> 5. 如果需要下一页的数据，直接去ES的上下文中，找后续内容
> 6. 循环进行4.5操作
>
> **缺点:**Scroll是从内存中去拿去数据的，不适合做实时的查询，拿到的数据不是最新的

## 4.5 delete-by-query

> Delete docs according to term, match query
>
> not recommend to use it when what you wanna delete is most part of data in index; recommend to use reverse operation, create new index aand add those data needed keep

## 4.6 Compound query

> 1. bool query
>
>    复合过滤器，可以将多个查询条件以一定的逻辑组合在一起，and or
>
>    - must : 所有的条件，用must组合在一起，表示AND
>    - must_not:将must_not中的条件，全部不能匹配，表示not的意思，不能匹配该查询条件
>    - should: 所有条件，用should组合在一起，表示or的意思，文档必须匹配一个或者多个查询条件
>    - filter: 过滤器，文档必须匹配该过滤条件，跟must子句的唯一区别是，filter不影响查询的score

> 2. Boosting query
>
>    boosting query 可以帮助我们去影响查询后的score
>
>    - Positive:只有匹配上positive的查询的内容，才会被放到返回的结果集中
>    - negative： 如果匹配上和positive并且也匹配上了negative，就可以降低这样的doc score
>    - Negative_boost： 指定系数，必须小于1.0
>
>    关于查询时，分数是如何计算的：
>
>    - 搜索的keyword在doc中出现的freq越高，score就越高
>    - 指定的doc内容越短，score就越短
>    - 我们在搜索时，指定的keyword也会被分词，这个被分词的内容，被分词库匹配的个数越多，score越高

## 4.7 Filter query

> Query：根据你的查询条件，去计算文档的匹配度得到一个分数，并且根据分数进行排序，不会做缓存
>
> filter： 根据你的查询条件去查询doc，不去计算分数，而且filter会对经常被过滤的数据进行缓存

## 4.8 Highlight query

> input keyword， return result with specific style
>
> returned result, is a Field in a doc, 单独讲Field以highlight形式返回给你

## 4.9 Aggregate query

### 4.9.1 bucket

> calculate metrics, such as a sum or average, from field values

### 4.9.2 metric

> group documents into buckets, also called bins, based on field values, ranges, or other criteria.

### 4.9.3 pipline

> take input from other aggregations instead of documents or fields.



## 4.10 Geo query

> https://www.elastic.co/guide/en/elasticsearch/reference/8.2/geo-queries.html
>
> 1. Geo_bounding_box
> 2. Geo_distance
> 3. Geo_polygon
> 4. geo_shape





