package com.itk.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itk.takeout.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
