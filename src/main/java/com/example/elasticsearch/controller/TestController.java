package com.example.elasticsearch.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.elasticsearch.entity.User;
import com.example.elasticsearch.service.UserService;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author jin
 * @Date 2022/12/16 11:42
 * @Description TODO
 * https://wenku.baidu.com/view/b50feaf5270c844769eae009581b6bd97f19bcc0.html
 * https://blog.csdn.net/gybshen/article/details/111469217
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
//    @Autowired
//    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public Object addBook(User book) throws IOException {
        IndexRequest indexRequest = new IndexRequest("christy", "user", "11");
        indexRequest.source("{\"name\":\"?????????????????????\",\"age\":685,\"bir\":\"1685-01-01\",\"introduce\":\"?????????????????????????????????????????????????????????\"," +
                "\"address\":\"?????????\"}", XContentType.JSON);

        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse.status());
        return "flag";
    }

    @PostMapping("/addTest")
    public void addTest(@RequestBody User user) {
        // id?????????????????????????????????
        user.setId(UUID.randomUUID().toString());
        user.setName("?????????");
        user.setBir(new Date());
        user.setIntroduce("??????????????????????????????????????????????????????????????????????????????????????????");
        user.setAddress("???????????????");
        userService.save(user);
    }

    @PostMapping("/deleteByid")
    public void deleteByid(String id) {
        userService.deleteById(id);
    }

    @PostMapping("/delAll")
    public void delAll() {
        userService.deleteAll();
    }

    @PostMapping("/getOne")
    public User getOne(String id) {
        Optional<User> optional = userService.findById(id);
        User user = optional.get();
        return user;
    }

    @PostMapping("/getByName")
    public List<User> getByName(String name) {
        List<User> users = userService.getByName(name);
        return users;
    }

    @PostMapping("/getByWord")
    public List<User> getByWord(String name) throws IOException, ParseException {
        // ??????????????????
        SearchRequest searchRequest = new SearchRequest("christy");
        // ??????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder
                .query(QueryBuilders.termQuery("introduce", name))
                .query(QueryBuilders.termQuery("name", name))
                .from(0)// ????????????(?????????-1)*size??????
                .size(10)// ??????????????????
                .sort("age", SortOrder.DESC)// ??????
                .highlighter(new HighlightBuilder().field("*")
                        .requireFieldMatch(false).preTags("<span style='color:red;'>").postTags("</span>"));//????????????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<User> userList = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            User user = new User();
            user.setId(hit.getId());
//            user.setAge(Integer.parseInt(sourceAsMap.get("age").toString()));
            user.setBir(new SimpleDateFormat("yyyy-MM-dd").parse(sourceAsMap.get("bir").toString()));
            user.setIntroduce(sourceAsMap.get("introduce").toString());
            user.setName(sourceAsMap.get("name").toString());
            user.setAddress(sourceAsMap.get("address").toString());

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.containsKey("name")) {
                user.setName(highlightFields.get("name").fragments()[0].toString());
            }

            if (highlightFields.containsKey("introduce")) {
                user.setIntroduce(highlightFields.get("introduce").fragments()[0].toString());
            }

            if (highlightFields.containsKey("address")) {
                user.setAddress(highlightFields.get("address").fragments()[0].toString());
            }

            userList.add(user);
        }
        return userList;
    }

    @PostMapping("/getByWord1")
    public SearchHits<User> getByWord1(String name) {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("name", name))
                .should(QueryBuilders.matchQuery("introduce", name)));
        ElasticsearchRestTemplate elasticsearchRestTemplate = new ElasticsearchRestTemplate(restHighLevelClient);
        SearchHits<User> search = elasticsearchRestTemplate.search(builder.build(), User.class);
        return search;
    }

    @PostMapping("/getByWord2")
    public List<User> getByWord2(String name) throws IOException {
        List<User> users = new ArrayList<>();
        // ??????????????????
        SearchRequest searchRequest = new SearchRequest("christy");
        // ??????????????????
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("name", name));
//        sourceBuilder.query(QueryBuilders.matchQuery("introduce", name));
        //???SearchSourceBuilder??????????????????????????????
        searchRequest.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            User user = JSONObject.parseObject(JSONObject.toJSONString(map), User.class);
            users.add(user);
        }
        return users;
    }

    @PostMapping("/getByWord3")
    /**
     * https://www.cnblogs.com/davidwang456/articles/12573201.html
     */
    public List<User> getByWord3(String name) throws IOException {
        List<User> users = new ArrayList<>();
        // ??????????????????
        SearchRequest searchRequest = new SearchRequest("christy");
        // ??????????????????
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //??????????????????
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", name);//??????
        MatchPhraseQueryBuilder matchPhraseQueryBuilder2 = QueryBuilders.matchPhraseQuery("introduce", name);//??????
        // ???boolQueryBuilder???????????????????????????????????????or?????????
        BoolQueryBuilder childBoolQueryBuilder = new BoolQueryBuilder()
                .should(matchQueryBuilder)
                .should(matchPhraseQueryBuilder2);
        //?????????????????????boolQueryBuilder???
        boolQueryBuilder
                .must(childBoolQueryBuilder);

        // ????????????--->??????DSL????????????
        sourceBuilder.query(boolQueryBuilder);
        // ?????????
        sourceBuilder.from(0);
        // ?????????????????????
        sourceBuilder.size(100);
        // ????????????????????????????????????????????????
//        sourceBuilder.fetchSource(new String[]{"name", "introduce"}, new String[]{});
        // ??????????????????
//        sourceBuilder.sort("introduce", SortOrder.ASC);
        // ?????????????????????2s
//        sourceBuilder.timeout(new TimeValue(2000));

        // ??????SearchSourceBuilder????????????
        searchRequest.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            User user = JSONObject.parseObject(JSONObject.toJSONString(map), User.class);
            users.add(user);
        }
        return users;
    }

}
