package com.itk.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itk.takeout.common.CustomException;
import com.itk.takeout.dto.DishDto;
import com.itk.takeout.dto.SetmealDto;
import com.itk.takeout.entity.Dish;
import com.itk.takeout.entity.DishFlavor;
import com.itk.takeout.entity.Setmeal;
import com.itk.takeout.entity.SetmealDish;
import com.itk.takeout.mapper.SetmealMapper;
import com.itk.takeout.service.SetmealDishService;
import com.itk.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * add combo
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // save dish base info
        this.save(setmealDto);

        //save combo and dish related info
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes=setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper1 = new LambdaQueryWrapper();
        queryWrapper1.in(Setmeal::getId,ids);
        queryWrapper1.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper1);
        if (count>0){
            throw new CustomException("meal is selling, cannot delete");
        }
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //get dish information
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto= new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);


        //get dish flavor information
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> flavors = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(flavors);

        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //update setmeal table
        this.updateById(setmealDto);

        //update flavor table
        // clear current flavor table
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());

        setmealDishService.remove(queryWrapper);


        // add current flavor table
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }
}
