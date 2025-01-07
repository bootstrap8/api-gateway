package com.github.hbq969.gateway.manage.service.impl;


import java.util.Objects;

import com.github.hbq969.gateway.manage.service.RouteService;
import com.github.hbq969.code.common.decorde.OptionalFacade;
import com.github.hbq969.code.common.decorde.OptionalFacadeAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(prefix = "route.notified", name = "way", havingValue = "kafka")
@Component("common-gateway-route-serv-KafkaNotifiedImpl")
@Slf4j
public class KafkaNotifiedImpl implements RouteService, OptionalFacadeAware<String, RouteService> {

    @Autowired
    private OptionalFacade<String, RouteService> facade;

    @Autowired(required = false)
    private KafkaTemplate kafka;

    @Override
    public void afterPropertiesSet() throws Exception {
        throw new UnsupportedOperationException("不支持kafka方式的路由变化通知");
    }

    @Override
    public OptionalFacade<String, RouteService> getOptionalFacade() {
        return this.facade;
    }

    @Override
    public String getKey() {
        return "kafka";
    }

    @Override
    public void refreshRouteConfig() {
        if (Objects.nonNull(kafka)) {
            log.info("通过kafka刷新路由: {}");
            kafka.send("GATEWAY-ROUTE-CHANGE", "{}");
        }
    }
}
