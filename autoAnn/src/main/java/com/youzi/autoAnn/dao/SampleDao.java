package com.youzi.autoAnn.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.youzi.autoAnn.entity.User;

@Mapper
public interface SampleDao {
	List<User> findAll();
	
	User findById(String id);
}
