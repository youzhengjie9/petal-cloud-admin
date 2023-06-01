package com.petal.common.security.resource.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petal.common.base.enums.ResponseType;
import com.petal.common.base.utils.ResponseResult;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 自定义权限不足处理器
 *
 * @author youzhengjie
 * @date 2023/05/26 22:58:35
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper){
        this.objectMapper=objectMapper;
    }

    @Override
    @SneakyThrows
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        ResponseResult<Object> result = ResponseResult.build(ResponseType.FORBIDDEN);
        response.setStatus(HttpStatus.FORBIDDEN.value());
//        System.out.println(accessDeniedException);
//        System.out.println(accessDeniedException.getMessage());
        if (accessDeniedException != null) {
            result.setMsg("权限不足,拒绝访问");
            result.setData(accessDeniedException.getMessage());
        }
        PrintWriter printWriter = response.getWriter();
        printWriter.append(objectMapper.writeValueAsString(result));

    }
}
