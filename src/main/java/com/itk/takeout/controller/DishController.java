package com.itk.takeout.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itk.takeout.common.R;
import com.itk.takeout.dto.DishDto;
import com.itk.takeout.entity.Category;
import com.itk.takeout.entity.Dish;
import com.itk.takeout.entity.Employee;
import com.itk.takeout.service.CategoryService;
import com.itk.takeout.service.DishFlavorService;
import com.itk.takeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * dish management
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * add dish
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        // delete cache
        Set key = redisTemplate.keys("dish_*");
        redisTemplate.delete(key);

        return R.success("save success!");
    }

    /**
     * dish page
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page={} pageSize={} name={}", page, pageSize, name);

        //page constructor
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //condition constructor
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //query
        dishService.page(pageInfo, queryWrapper);

        //object copy
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryID = item.getCategoryId();

            Category category =categoryService.getById(categoryID);

            String name1 = category.getName();
            dishDto.setCategoryName(name1);
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * get stuff information by id
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * update dish
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        // delete cache
        Set key = redisTemplate.keys("dish_*");
        redisTemplate.delete(key);


        return R.success("save success!");
    }

    /**
     * set status
     * @param ids
     * @return
     */
    @PostMapping("status/{status}")
    public R<String> status(@PathVariable int status, @RequestParam("ids") String ids){
        // delete cache
        Set key = redisTemplate.keys("dish_*");
        redisTemplate.delete(key);

        String[] list = ids.split(",");
        for(String id: list){
            Dish dish = dishService.getById(id);
            if (dish == null){
                return R.error("no infomation");
            }
            dish.setStatus(status);
            dishService.updateById(dish);
        }


        return R.success("save success!");
    }

    /**
     * delete dish
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("delete +{}", ids);
        dishService.remove(ids);


        return R.success("save success!");
    }

    /**
     * get dish list
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(DishDto dish){
        List<DishDto> dishDtoList =null;
        // get cache data
        String key = "dish"+dish.getCategoryId()+"_"+dish.getStatus();
        dishDtoList = (List<DishDto>)redisTemplate.opsForValue().get(key);

        // if get data, return
        if (dishDtoList!=null){
            return R.success(dishDtoList);
        }

        //if not, store it to cache
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        lambdaQueryWrapper.like(dish.getName() != null, Dish::getName, dish.getName());
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lambdaQueryWrapper);

        dishDtoList = list.stream().map((item)->{
            // get current dish id
            Long dishID = item.getId();
            DishDto dishDto = dishService.getByIdWithFlavor(dishID);
            BeanUtils.copyProperties(item,dishDto);
            return dishDto;
        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
//    /**
//     * get dish list
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//
//        lambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        lambdaQueryWrapper.like(dish.getName() != null, Dish::getName, dish.getName());
//        lambdaQueryWrapper.eq(Dish::getStatus,1);
//        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(lambdaQueryWrapper);
//        return R.success(list);
//    }
}
