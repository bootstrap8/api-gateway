package com.github.hbq969.gateway.manage.service.impl;

import com.github.hbq969.gateway.manage.pojo.RouteInfo;
import com.github.hbq969.gateway.manage.service.RouteService;
import com.github.hbq969.code.common.decorde.DefaultOptionalFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("common-gateway-route-serv-NotifiedFacade")
@Slf4j
public class NotifiedFacade extends DefaultOptionalFacade<String, RouteService> implements
        RouteService {

    @Autowired
    private Environment context;

    @Override
    public Optional<RouteService> query() {
        String way = context.getProperty("route.notified.way", "http");
        return query(way);
    }

    @Override
    public void saveRouteConfig(RouteInfo route) {
        getService().saveRouteConfig(route);
    }

    @Override
    public void deleteRouteConfig(String id) {
        getService().deleteRouteConfig(id);
    }

    @Override
    public void refreshRouteConfig() {
        getService().refreshRouteConfig();
    }
}
