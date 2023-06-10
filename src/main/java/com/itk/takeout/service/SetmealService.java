package com.itk.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itk.takeout.dto.SetmealDto;
import com.itk.takeout.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {


    /**
     * save setmeal and setmeal dish
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * remove with dish
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    public SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);
}
