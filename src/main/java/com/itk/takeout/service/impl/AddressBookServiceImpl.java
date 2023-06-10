package com.itk.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.itk.takeout.entity.AddressBook;
import com.itk.takeout.mapper.AddressBookMapper;
import com.itk.takeout.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
