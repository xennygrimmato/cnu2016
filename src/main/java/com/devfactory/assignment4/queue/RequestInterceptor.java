package com.devfactory.assignment4.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaibhavtulsyan on 11/07/16.
 */

@Component
public class RequestInterceptor extends HandlerInterceptorAdapter {

    private HttpServletRequestWrapper wrappedRequest;

    @Autowired
    AWSQueueService aqs = new AWSQueueService();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod ) handler;
        this.wrappedRequest = new HttpServletRequestWrapper(
                (HttpServletRequest) request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        HandlerMethod handlerMethod = (HandlerMethod ) handler;
        try {
            System.out.println("Post-Handling begins.");
            long startTime = System.currentTimeMillis();
            long timestamp = startTime;
            String url = request.getRequestURL().toString();
            String ipAddress = request.getRemoteUser();
            String method = request.getMethod();

            //String data = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            Map<String,String> parameters = new HashMap<String, String>();

            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String key = (String) parameterNames.nextElement();
                String val = request.getParameter(key);
                //System.out.println("A= <"+key+"> Value<"+val+">");
                parameters.put(key, val);
            }

            String body = "";

            int responseCode = response.getStatus();
            AuditLog log = new AuditLog(url, ipAddress, timestamp, responseCode, parameters, method, body);
            System.out.println("Calling sendMessage(log)");
            aqs.sendMessage(log);
            System.out.println("Completed sendMessage(log)");

        } catch(Exception e) {

        }
    }
}
