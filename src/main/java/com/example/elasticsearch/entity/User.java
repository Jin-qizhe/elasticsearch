package com.example.elasticsearch.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 *
 * @Document: 代表一个文档记录
 * indexName: 用来指定索引名称
 * type: 用来指定索引类型
 * @Id: 用来将对象中id和ES中_id映射
 * @Field: 用来指定ES中的字段对应Mapping
 * type: 用来指定ES中存储类型
 * analyzer: 用来指定使用哪种分词器
 *
 */


@Data
@Document(indexName = "christy", createIndex = true)
public class User {
    @Id //用来将对象中id属性与文档中_id 一一对应
    private String id;

    // 用在属性上 代表mapping中一个属性 一个字段 type:属性 用来指定字段类型 analyzer:指定分词器
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String name;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Date)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date bir;

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String introduce;

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String address;
}
