package com.petal.gateway.swagger;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
 
/**
 * swagger全局过滤器
 * <p>
 * 作用: 解决gateway整合knife4j后接口请求没带上服务名称问题
 */
@Slf4j
@Component
public class SwaggerGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path=exchange.getRequest().getPath().toString();
        if (!path.endsWith(SwaggerProvider.API_URI)){
            return chain.filter(exchange);
        }
        String[] pathArray=path.split("/");
        String basePath=pathArray[1];
        ServerHttpResponse originalResponse = exchange.getResponse();
        // 定义新的消息头
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getResponse().getHeaders());
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (Objects.equals(getStatusCode(), HttpStatus.OK) && body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        List<String> list = new ArrayList<String>();
                        dataBuffers.forEach(dataBuffer -> {
                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);
                            DataBufferUtils.release(dataBuffer);
                            list.add(new String(content, StandardCharsets.UTF_8));
                        });
                        String s = listToString(list);
                        int length = s.getBytes().length;
                        headers.setContentLength(length);
                        JSONObject jsonObject= JSONUtil.parseObj(s);
                        jsonObject.set("basePath",basePath);
                        s=jsonObject.toString();
                        return bufferFactory().wrap(s.getBytes());
                    }));
                }
                return super.writeWith(body);
            };
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                //由于修改了请求体的body，导致content-length长度不确定，因此使用分块编码
                httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
                httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                return httpHeaders;
            }
 
            private String listToString(List<String> list){
                StringBuilder stringBuilder=new StringBuilder();
                for (String s:list){
                    stringBuilder.append(s);
                }
                return stringBuilder.toString();
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }
 
    @Override
    public int getOrder() {
        return -2;
    }
}