package com.itk.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itk.takeout.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
