package com.github.hbq969.gateway.manage.feign;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author : hbq969@gmail.com
 * @description : gateway端点feign
 * @createTime : 2023/8/28 14:00
 */
public interface MasterEndpoint {

    @RequestMapping(path = "/gateway-dashboard/gateway/refresh", method = RequestMethod.POST)
    String refresh();

    @RequestMapping(path = "/gateway-dashboard/route-ext/load", method = RequestMethod.POST)
    @ResponseBody
    Map load();
}
