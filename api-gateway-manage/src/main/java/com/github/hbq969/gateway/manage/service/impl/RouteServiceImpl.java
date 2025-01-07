package com.github.hbq969.gateway.manage.service.impl;

import com.github.hbq969.gateway.manage.dao.RouteDao;
import com.github.hbq969.gateway.manage.pojo.RouteConfig;
import com.github.hbq969.gateway.manage.pojo.RouteInfo;
import com.github.hbq969.gateway.manage.pojo.TemplateInfo;
import com.github.hbq969.gateway.manage.service.RouteService;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.InitScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.h2.tools.Server;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author hbq969@gmail.com
 */
@Service("common-gateway-route-serv-RouteServiceImpl")
@Slf4j
public class RouteServiceImpl implements RouteService, InitializingBean {

    @Qualifier("common-gateway-route-serv-NotifiedFacade")
    @Autowired
    private RouteService routeFacade;

    @Autowired
    private JdbcTemplate jt;

    @Autowired
    private RouteDao dao;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Autowired
    private Environment context;

    @Autowired
    private SpringContext springContext;

    private Server server;

    @Override
    public void afterPropertiesSet() throws Exception {
        createRouteTemplate();
        createRouteConfig();
        startEmbeddedDataSource();
        createUserInfo();
        initData();
    }

    @Override
    public void saveRouteConfig(RouteInfo route) {
        try {
            route.setUpdateTime(System.currentTimeMillis());
            route.multipleRouteInfos().forEach(r -> {
                dao.saveRouteConfig(r.config());
            });
            log.info("保存路由成功: {}", route);
        } catch (Exception e) {
            log.error("保存路由失败", e);
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public void updateRouteConfig(RouteInfo route) {
        try {
            route.setUpdateTime(System.currentTimeMillis());
            if (route.isMultipleUri()) {
                throw new UnsupportedOperationException("更新不支持配置uri多实例信息");
            }
            dao.updateRouteConfig(route.config());
            log.info("更新路由成功: {}", route);
        } catch (Exception e) {
            log.error("更新路由失败", e);
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public void deleteRouteConfig(String id) {
        try {
            dao.deleteRouteConfig(id);
            log.info("删除路由成功: {}", id);
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public void updateStartOrStop(String id, int enabled) {
        dao.updateStartOrStop(id, enabled, System.currentTimeMillis());
    }

    @Override
    public List<RouteInfo> queryAllRouteConfig(int pageNum, int pageSize, String routeSelect,
                                               String routeKey, int enabled) {
        return dao.queryAllRouteConfig(new RowBounds(pageNum, pageSize), routeSelect, routeKey, enabled)
                .stream().map(r -> r.info()).collect(Collectors.toList());
    }

    @Override
    public RouteInfo queryRoute(String id) {
        RouteConfig config = dao.queryRoute(id);
        return Objects.nonNull(config) ? config.info() : null;
    }

    @Override
    public void refreshRouteConfig() {
        routeFacade.refreshRouteConfig();
    }

    @Override
    public List<TemplateInfo> queryRouteTemplateInfos() {
        return dao.queryRouteTemplateInfos();
    }

    private void createRouteConfig() {
        try {
            dao.createRouteConfig();
            log.info("创建路由配置表成功");
        } catch (Exception e) {
        }
    }

    private void createRouteTemplate() {
        try {
            dao.createRouteTemplate();
            log.info("创建路由模版配置表成功");
        } catch (Exception e) {
        }
    }

    private void startEmbeddedDataSource() {
        String enabled = context.getProperty("embedded.enabled", "false");
        log.info("embedded.enabled: {}", enabled);
        if ("true".equals(enabled)) {
            CompletableFuture.runAsync(() -> {
                String port = context.getProperty("embedded.datasource.port", "30135");
                String[] args = new String[]{"-tcpPort", port, "-tcpAllowOthers"};
                try {
                    server = Server.createTcpServer(args).start();
                    log.info("启动h2数据成功");
                } catch (SQLException ex) {
                    log.error("启动h2数据库tcp监听异常", ex);
                }
            });
        }
    }

    private void createUserInfo() {
        try {
            dao.createUserInfo();
            log.info("创建网关dashboard用户信息表成功");
        } catch (Exception e) {
        }
        try {
            dao.createUserAuthorities();
            log.info("创建网关dashboard用户权限表成功");
        } catch (Exception e) {

        }
    }

    private void initData() {
        InitScriptUtils.initial(springContext, "api-gateway-manage-init-script.sql", null);
    }
}
