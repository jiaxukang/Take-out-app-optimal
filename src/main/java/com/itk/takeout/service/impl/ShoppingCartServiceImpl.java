package com.itk.takeout.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itk.takeout.entity.ShoppingCart;
import com.itk.takeout.mapper.ShoppingCartMapper;
import com.itk.takeout.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
