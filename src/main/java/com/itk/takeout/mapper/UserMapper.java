package com.itk.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itk.takeout.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
