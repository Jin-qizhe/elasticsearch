package com.example.elasticsearch.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.elasticsearch.entity.TData;
import com.example.elasticsearch.service.ITDataService;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author jin
 * @since 2022-12-21
 */
@RestController
@RequestMapping("/tData")
public class TDataController {

    @Autowired
    private ITDataService itDataService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @GetMapping("/list")
    public String list(String name) {
        long start = System.currentTimeMillis();
        QueryWrapper<TData> qw = new QueryWrapper<>();
        qw.like("pcsmc", name);
        List<TData> list = itDataService.list(qw);
        long end = System.currentTimeMillis();
        return end - start + "";
    }

    @GetMapping("/page")
    public Page<TData> page(String name, Integer current, Integer size) {
        Long start = System.currentTimeMillis();
        Page<TData> page = new Page<>(current, size);
        QueryWrapper<TData> qw = new QueryWrapper<>();
        qw.like("pcsmc", name);
        page = itDataService.page(page, qw);
        Long end = System.currentTimeMillis();
        System.out.println(end - start);
        return page;
    }

    @PostMapping("/addDataIndex")
    public String addDataIndex() {
        final Integer[] num = {0};
        Long start = System.currentTimeMillis();
        QueryWrapper<TData> qw = new QueryWrapper<>();
        qw.eq("type", 0);
        List<TData> list = itDataService.list(qw);
        if (list.size() > 0) {
            list.parallelStream().forEach(l -> {
                IndexRequest indexRequest = new IndexRequest("testdata", "data", l.getId().toString());
                String source = JSONUtil.toJsonStr(l);
                indexRequest.source(source, XContentType.JSON);
                try {
                    IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
                    if (StrUtil.isNotEmpty(indexResponse.getIndex())) {
                        l.setType(2);
                        num[0]++;
                    }
                    itDataService.updateById(l);
                    System.out.println("--------------" + indexResponse + "--------------");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        Long end = System.currentTimeMillis();
        return "共处理" + num[0] + "条数据。共用" + (end - start) / 1000 + "秒";
    }

    @PostMapping("/elList")
    public Long elList(String keyWord) throws IOException {
        Long start = System.currentTimeMillis();

        List<TData> dataList = new ArrayList<>();

        // 创建搜索请求
        SearchRequest searchRequest = new SearchRequest("testdata");

        // 创建搜索对象
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //构造查询条件
        sourceBuilder.query(QueryBuilders.matchQuery("pcsmc", keyWord));

        sourceBuilder.size(128234);

        // 设置SearchSourceBuilder查询属性
        searchRequest.source(sourceBuilder);


        //查询
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            TData tData = JSONObject.parseObject(JSONObject.toJSONString(map), TData.class);
            dataList.add(tData);
        }
        System.out.println(dataList.size());

        Long end = System.currentTimeMillis();
        return end - start;
    }

}

