package com.itk.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itk.takeout.common.R;
import com.itk.takeout.entity.User;
import com.itk.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * mobile login
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map user, HttpSession session){
        //get account
        String account = user.get("phone").toString();
        //get password
        String password = user.get("code").toString();


        //check user account and password
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getPhone,account);
        userLambdaQueryWrapper.eq(User::getIdNumber,password);

        User user1 = userService.getOne(userLambdaQueryWrapper);
        if (user1 == null){
            return R.error("login failed");
        }
        session.setAttribute("user",user1.getId());
        return R.success(user1);
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
        session.removeAttribute("user");

        return R.success("logout success");
    }
}
