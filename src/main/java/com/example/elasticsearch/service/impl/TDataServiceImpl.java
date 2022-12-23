package com.example.elasticsearch.service.impl;

import com.example.elasticsearch.entity.TData;
import com.example.elasticsearch.mapper.TDataMapper;
import com.example.elasticsearch.service.ITDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jin
 * @since 2022-12-21
 */
@Service
public class TDataServiceImpl extends ServiceImpl<TDataMapper, TData> implements ITDataService {

}
