package com.github.hbq969.gateway.master.route.service;

import com.github.hbq969.gateway.master.route.dao.RouteDao;
import com.github.hbq969.gateway.master.route.model.RouteInfo;
import com.github.hbq969.gateway.master.utils.GsonUtils;
import com.github.hbq969.gateway.master.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author hbq969@gmail.com
 */
@Component("common-gateway-route-RdbRouteDefinitionRepository")
@Slf4j
public class RdbRouteDefinitionRepository implements RouteDefinitionRepository, InitializingBean {

    private Map<String, RouteDefinition> routeDefinitionEntry = new ConcurrentHashMap<>();

    private List<RouteDefinition> routeDefinitionList = new Vector<>();

    @Autowired
    @Qualifier("common-gateway-route-dao-RouteDao")
    private RouteDao dao;

    private CountDownLatch cdl = new CountDownLatch(1);

    @Autowired
    private JdbcTemplate jt;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;


    @Override
    public void afterPropertiesSet() throws Exception {
        createRouteConfig();
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        try {
            cdl.await();
        } catch (InterruptedException e) {
        }
        return Flux.fromIterable(routeDefinitionList);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        throw new UnsupportedOperationException();
    }

    //    @EventListener({RefreshRoutesEvent.class})
    public synchronized void reloadImmediately(String type) {
        log.info("重载路由配置");
        try {
            routeDefinitionEntry.clear();
            routeDefinitionList.clear();
            List<RouteInfo> all = dao.queryAllRouteConfig();
            all.forEach(r -> {
                RouteDefinition f = r.entry();
                if (log.isDebugEnabled()) {
                    log.debug("加载到路由: {}", GsonUtils.toJSONString(f));
                }
                routeDefinitionEntry.put(r.getId(), f);
                routeDefinitionList.add(f);
            });
            log.info("加载到路由配置: {} 条", routeDefinitionList.size());
        } finally {
            cdl.countDown();
        }
    }

    @EventListener(RefreshRoutesEvent.class)
    void showEventInfo(RefreshRoutesEvent event) {
        if (event != null && event.getSource() instanceof String) {
            log.info("监听到路由变化事件: {}", event.getSource());
        }
    }

    private void createRouteConfig() {
        try {
            dao.createRouteConfig();
            log.info("创建路由配置表成功");
        } catch (Exception e) {
        }
        SqlUtils.initDataSql(jt, "/", "api-gateway-master-init-script.sql", activeProfile);
    }
}
