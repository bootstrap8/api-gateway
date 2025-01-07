package com.github.hbq969.gateway.master.route.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author : hbq969@gmail.com
 * @description : 路由刷新
 * @createTime : 2024/9/29 09:21
 */
@Component
public class RoutePubCenter implements ApplicationEventPublisherAware, InitializingBean {

    @Autowired
    private RdbRouteDefinitionRepository repository;

    private ApplicationEventPublisher pub;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.pub = applicationEventPublisher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        notified("程序启动");
    }

    public void notified(String type) {
        this.repository.reloadImmediately(type);
        this.pub.publishEvent(new RefreshRoutesEvent(type));
    }

    @Scheduled(cron = "${spring.cloud.gateway.reload.cron: 0 */10 * * * ?}")
    public synchronized void schedule() {
        notified("周期加载");
    }
}
