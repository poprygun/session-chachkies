package io.microsamples.sessions.sessionchachkies.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse arg1,
                           Object arg2, ModelAndView arg3) {
        log.info("👀 Remote IP {}", request.getRemoteHost());
        log.info("👀 {}", request.getSession().getId());
    }

}
