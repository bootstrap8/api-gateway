package com.github.hbq969.gateway.manage.service.impl;

import com.github.hbq969.gateway.manage.service.RouteService;
import com.github.hbq969.code.common.decorde.OptionalFacade;
import com.github.hbq969.code.common.decorde.OptionalFacadeAware;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : 通过服务发现刷新路由
 * @createTime : 2024/9/27 16:03
 */
@ConditionalOnProperty(prefix = "route.notified", name = "way", havingValue = "discovery")
@Component("common-gateway-route-serv-DiscoveryNotifiedImpl")
@Slf4j
public class DiscoveryNotifiedImpl implements RouteService, OptionalFacadeAware<String, RouteService> {

    @Autowired
    private OptionalFacade<String, RouteService> facade;

    @Autowired
    private DiscoveryClient client;

    @Value("${route.notified.master-service-id: API-GATEWAY-MASTER}")
    private String masterServiceId;

    @Value("${gateway.endpoint.refresh.uri: /gateway-dashboard/gateway/refresh}")
    private String refreshUri;

    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public void afterPropertiesSet() throws Exception {
        OptionalFacadeAware.super.afterPropertiesSet();
        log.info("路由变化通知方式：{}", getKey());
    }

    @Override
    public OptionalFacade<String, RouteService> getOptionalFacade() {
        return this.facade;
    }

    @Override
    public String getKey() {
        return "discovery";
    }

    @Override
    public void refreshRouteConfig() {
        List<ServiceInstance> sis = client.getInstances(masterServiceId);
        if (CollectionUtils.isNotEmpty(sis)) {
            sis.forEach(serviceInstance -> {
                String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + refreshUri;
                log.info("调用路由变化通知接口: {}", url);
                okHttpClient.newCall(new Request.Builder()
                        .url(url)
                        .post(RequestBody.create("", MediaType.get("application/json"))).build()).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        log.error(String.format("刷新路由失败 %s", url), e);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        log.info("刷新路由成功, {}", url);
                    }
                });
            });
        }
    }
}
