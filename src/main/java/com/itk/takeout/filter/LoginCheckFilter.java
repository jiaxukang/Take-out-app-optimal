package com.itk.takeout.filter;

import com.alibaba.fastjson.JSON;
import com.itk.takeout.common.BaseContext;
import com.itk.takeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * check user login
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("request: {}",request.getRequestURI());

        // get url
        String requestURI = request.getRequestURI();
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        // check url is need to solve
        boolean check = check(urls, requestURI);

        // if true, then pass
        if(check){
            filterChain.doFilter(request,response);
            return;
        }
        if (request.getSession().getAttribute("employee")!= null){
            log.info("user is {}", request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        if(request.getSession().getAttribute("user")!=null){
            Long userID = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userID);

            filterChain.doFilter(request,response);
            return;
        }


        // return intercept result
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * check if url is pass
     * @param urls
     * @param requestUrl
     * @return
     */
    public boolean check(String[] urls,String requestUrl){
        for (String url: urls){
            boolean match = PATH_MATCHER.match(url, requestUrl);
            if (match){
                return true;
            }
        }
        return false;
    }
}
