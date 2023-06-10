package com.itk.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itk.takeout.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders order);
}
