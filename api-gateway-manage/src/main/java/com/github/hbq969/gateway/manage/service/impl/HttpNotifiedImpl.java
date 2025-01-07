package com.github.hbq969.gateway.manage.service.impl;

import com.github.hbq969.gateway.manage.feign.MasterEndpoint;
import com.github.hbq969.gateway.manage.pojo.RouteInfo;
import com.github.hbq969.gateway.manage.service.RouteService;
import com.github.hbq969.code.common.decorde.OptionalFacade;
import com.github.hbq969.code.common.decorde.OptionalFacadeAware;
import com.github.hbq969.code.common.utils.GsonUtils;
import com.github.hbq969.code.common.utils.TimeUnitHandler;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ConditionalOnProperty(prefix = "route.notified", name = "way", havingValue = "http")
@Component("common-gateway-route-serv-HttpNotifiedImpl")
@Slf4j
public class HttpNotifiedImpl implements RouteService, OptionalFacadeAware<String, RouteService> {

    private OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS).build();

    @Autowired
    private MasterEndpoint endpoint;

    @Autowired
    private OptionalFacade<String, RouteService> facade;

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
        return "http";
    }

    @Override
    public void saveRouteConfig(RouteInfo route) {
        TimeUnitHandler.MILLISECONDS.sleep(500).handle(() -> {
            String body = GsonUtils.toJson(route);
            log.info("新增路由，调用路由刷新端点: {}", body);
            Map result = endpoint.load();
            log.info("保存路由通知重载结果: {}", result);
        });
    }

    @Override
    public void deleteRouteConfig(String id) {
        TimeUnitHandler.MILLISECONDS.sleep(500).handle(() -> {
            String body = GsonUtils.toJson("{\"id\":\"" + id + "\"}");
            log.info("删除路由，调用路由刷新端点: {}", body);
            Map result = endpoint.load();
            log.info("删除路由通知重载结果: {}", result);
        });
    }

    @Override
    public void refreshRouteConfig() {
        TimeUnitHandler.MILLISECONDS.sleep(500).handle(() -> {
            log.info("刷新路由，调用路由刷新端点");
            Map result = endpoint.load();
            log.info("刷新路由通知重载结果: {}", result);
        });
    }

    static class ResultCallback implements Callback {

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            log.error("刷新路由失败", e);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            log.info("刷新路由成功");
        }
    }


}
