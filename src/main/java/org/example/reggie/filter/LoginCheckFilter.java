package org.example.reggie.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    // to match path
    public static final AntPathMatcher pathMatcher= new AntPathMatcher();
    // need to override the method of Filter
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // get the request URI
        String requestURI = request.getRequestURI();
        // whether to intercept or not
        //list the URI that do not need to intercept
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login",
        };

        if (check(urls, requestURI)) {
            log.info("requestURI that do not need to intercept: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        // if user has logged in
        if (request.getSession().getAttribute("employee") != null) {
            log.info("user has logged in");
            filterChain.doFilter(request, response);
            return;
        }
        // intercept the request
        log.info("user has not logged in");
        log.info("intercept request: {}", requestURI);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
        //filterChain.doFilter(request, response);
    }

    //check if requestURI matches url in urls
    public boolean check(String[] urls, String requestURI) {
        for (String url:urls) {
            if (pathMatcher.match(url,requestURI)) {
                return true;
            }
        }
        return false;
    }
}
