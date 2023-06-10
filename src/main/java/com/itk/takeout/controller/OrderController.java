package com.itk.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itk.takeout.common.BaseContext;
import com.itk.takeout.common.R;
import com.itk.takeout.dto.OrdersDto;
import com.itk.takeout.entity.Employee;
import com.itk.takeout.entity.OrderDetail;
import com.itk.takeout.entity.Orders;
import com.itk.takeout.entity.ShoppingCart;
import com.itk.takeout.service.OrderDetailService;
import com.itk.takeout.service.OrderService;
import com.itk.takeout.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * submit order
     * @param order
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order){
        orderService.submit(order);
        return R.success("submit success");
    }

    /**
     * get staff paginate search
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    @Transactional
    public R<Page<OrdersDto>> userPage(int page, int pageSize){
        //page constructor
        Page<Orders> pageInfo = new Page(page,pageSize);
        Page<OrdersDto> orderDtoPage = new Page<>();


        //condition constructor
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //query
        orderService.page(pageInfo, queryWrapper);

        List<OrdersDto> list = pageInfo.getRecords().stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            //获取orderId,然后根据这个id，去orderDetail表中查数据
            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> details = orderDetailService.list(wrapper);
            BeanUtils.copyProperties(item, ordersDto);
            //之后set一下属性
            ordersDto.setOrderDetails(details);
            return ordersDto;
        }).collect(Collectors.toList());
        BeanUtils.copyProperties(pageInfo, orderDtoPage, "records");
        orderDtoPage.setRecords(list);
        return R.success(orderDtoPage);
    }

    /**
     * get staff paginate search
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, Long number, String beginTime, String endTime){
        //page constructor
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> dtoPage = new Page<>(page,pageSize);

        //condition constructor
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(number!=null, Orders::getId, number);
        queryWrapper.gt(!StringUtils.isEmpty(beginTime), Orders::getOrderTime,beginTime)
                .lt(!StringUtils.isEmpty(endTime), Orders::getOrderTime,endTime);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //query
        orderService.page(pageInfo, queryWrapper);

        //set orderDto
        List<OrdersDto> ordersDtos = pageInfo.getRecords().stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();

            //get order id
            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> queryWrappers = new LambdaQueryWrapper<>();
            queryWrappers.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> orderDetailList = orderDetailService.list(queryWrappers);

            //copy
            BeanUtils.copyProperties(item,ordersDto);
            ordersDto.setOrderDetails(orderDetailList);
            return ordersDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        dtoPage.setRecords(ordersDtos);
        return R.success(dtoPage);
    }

    /**
     * order again
     * @param map
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Map<String,String> map){
        Long orderId = Long.valueOf(map.get("id"));

        //get order detail
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,orderId);
        List<OrderDetail> details = orderDetailService.list(queryWrapper);

        //get current id
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = details.stream().map((item) ->{
            ShoppingCart shoppingCart = new ShoppingCart();
            //Copy
            BeanUtils.copyProperties(item,shoppingCart);
            //userId
            shoppingCart.setUserId(userId);
            //set local time
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartService.saveBatch(shoppingCarts);
        return R.success("order success");
    }

    @PutMapping
    public R<String> status(@RequestBody Map<String,String> map){
        int status = Integer.valueOf(map.get("status"));
        Long orderId= Long.valueOf(map.get("id"));

        LambdaUpdateWrapper<Orders> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(Orders::getId, orderId);
        queryWrapper.set(Orders::getStatus,status);

        orderService.update(queryWrapper);
        return R.success("update success");
    }
}
