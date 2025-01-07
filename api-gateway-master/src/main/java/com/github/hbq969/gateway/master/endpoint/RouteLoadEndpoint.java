package com.github.hbq969.gateway.master.endpoint;

import com.github.hbq969.gateway.master.route.service.RdbRouteDefinitionRepository;
import com.github.hbq969.gateway.master.route.service.RoutePubCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : hbq969@gmail.com
 * @description : 路由重载端点
 * @createTime : 2024/9/6 14:48
 */
@RestControllerEndpoint(id = "route-ext")
@Component
@Slf4j
public class RouteLoadEndpoint {

    @Autowired
    private RdbRouteDefinitionRepository repository;

    @Autowired
    private RoutePubCenter center;

    @PostMapping(value = {"/load"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map load() {
        Map result = new HashMap();
        try {
            center.notified("页面修改");
            result.put("result", "成功");
        } catch (Exception e) {
            log.error("重载路由失败", e);
            result.put("result", e.getMessage());
        }
        return result;
    }
}
