package com.github.hbq969.gateway.manage.service.impl;

import com.github.hbq969.code.sm.login.session.UserContext;
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

    @Value("${spring.application.name}")
    private String app;

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
            route.setApp(app);
            route.setRoleName(UserContext.get().getRoleName());
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
        boolean check = false;
        try {
            RouteConfig rc = dao.queryRouteBySession(app, UserContext.get().getRoleName(), route.getId());
            if (rc != null) {
                route.setApp(app);
                route.setRoleName(UserContext.get().getRoleName());
                route.setUpdateTime(System.currentTimeMillis());
                if (route.isMultipleUri()) {
                    throw new UnsupportedOperationException("更新不支持配置uri多实例信息");
                }
                dao.updateRouteConfig(route.config());
                log.info("更新路由成功: {}", route);
                check = true;
            }
        } catch (Exception e) {
            log.error("更新路由失败", e);
        }
        if (!check) {
            throw new UnsupportedOperationException("路由不在权限范围内或更新路由异常，请检查");
        }
    }

    @Override
    public void deleteRouteConfig(String id) {
        boolean check = false;
        try {
            RouteConfig rc = dao.queryRouteBySession(app, UserContext.get().getRoleName(), id);
            if (rc != null) {
                dao.deleteRouteConfig(app, UserContext.get().getRoleName(), id);
                log.info("删除路由成功: {}", id);
                check = true;
            }
        } catch (Exception e) {
            log.error("删除路由异常", e);
        }
        if (!check) {
            throw new UnsupportedOperationException("路由不在权限范围内或删除路由异常，请检查");
        }
    }

    @Override
    public void updateStartOrStop(String id, int enabled) {
        boolean check = false;
        try {
            RouteConfig rc = dao.queryRouteBySession(app, UserContext.get().getRoleName(), id);
            if (rc != null) {
                dao.updateStartOrStop(app, UserContext.get().getRoleName(), id, enabled, System.currentTimeMillis());
                log.info("启停路由成功: {}", id);
                check = true;
            }
        } catch (Exception e) {
            log.error("启停路由异常", e);
        }
        if (!check) {
            throw new UnsupportedOperationException("路由不在权限范围内或启停路由异常，请检查");
        }
    }

    @Override
    public List<RouteInfo> queryAllRouteConfig(int pageNum, int pageSize, String routeSelect,
                                               String routeKey, int enabled) {
        return dao.queryAllRouteConfig(new RowBounds(pageNum, pageSize), app, UserContext.get().getRoleName(), routeSelect, routeKey, enabled)
                .stream().map(r -> r.info()).collect(Collectors.toList());
    }

    @Override
    public RouteInfo queryRoute(String id) {
        RouteConfig config = dao.queryRouteBySession(app, UserContext.get().getRoleName(), id);
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
