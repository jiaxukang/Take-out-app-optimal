package com.itk.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itk.takeout.entity.Category;


public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
