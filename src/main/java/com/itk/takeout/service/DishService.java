package com.itk.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itk.takeout.dto.DishDto;
import com.itk.takeout.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    public void remove(List<Long> id);
}
