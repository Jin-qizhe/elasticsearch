package com.example.elasticsearch.service;

import com.example.elasticsearch.entity.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author jin
 * @Date 2022/12/16 15:01
 * @Description TODO
 */
@Service
public interface UserService extends ElasticsearchRepository<User, String> {
    List<User> getByName(String name);
}
