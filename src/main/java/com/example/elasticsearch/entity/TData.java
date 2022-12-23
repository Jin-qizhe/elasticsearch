package com.example.elasticsearch.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * <p>
 *
 * </p>
 *
 * @author jin
 * @since 2022-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("T_DATA")
@Document(indexName = "testdata", createIndex = true)
public class TData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("ID")
    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @TableField("BZDDDM")
    private String bzdddm;

    @TableField("JZWMC")
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String jzwmc;

    @TableField("PCSDM")
    private String pcsdm;

    @TableField("PCSMC")
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String pcsmc;

    @TableField("JCWH")
    private String jcwh;

    @TableField("TYPE")
    private Integer type;


}
