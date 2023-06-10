package com.itk.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itk.takeout.common.BaseContext;
import com.itk.takeout.common.CustomException;
import com.itk.takeout.entity.*;
import com.itk.takeout.mapper.OrderMapper;
import com.itk.takeout.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;


    @Override
    @Transactional
    public void submit(Orders order) {
        //get current id
        Long id = BaseContext.getCurrentId();

        //search shopping cart info
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, id);
        List<ShoppingCart> shoppingCart = shoppingCartService.list(wrapper);

        if (shoppingCart ==null){
            throw  new CustomException("shopping cart is none");
        }

        //get user data
        User user = userService.getById(id);
        //get address book data
        AddressBook addressBook = addressBookService.getById(order.getAddressBookId());

        if(addressBook==null){
            throw  new CustomException("address book is none");
        }

        //insert data to order info
        Long orderId = IdWorker.getId();
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails =shoppingCart.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        order.setId(orderId);
        order.setNumber(String.valueOf(orderId));
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setStatus(2);
        order.setAmount(new BigDecimal(amount.get()));
        order.setUserId(id);
        order.setUserName(user.getName());
        order.setConsignee(addressBook.getConsignee());
        order.setPhone(addressBook.getPhone());
        order.setAddress((addressBook.getProvinceName() ==null ? "":addressBook.getProvinceName())+
                (addressBook.getCityName() == null ? "":addressBook.getCityName())+
                (addressBook.getDistrictName() ==null? "": addressBook.getDistrictName())+
                (addressBook.getDetail()== null? "": addressBook.getDetail()));
        this.save(order);

        //insert data to order detail info
        orderDetailService.saveBatch(orderDetails);

        //clean shopping cart info
        shoppingCartService.remove(wrapper);
    }
}
