package com.github.hbq969.gateway.master.route.dao;

import com.github.hbq969.gateway.master.route.model.RouteInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hbq969@gmail.com
 */
@Repository("common-gateway-route-dao-RouteDao")
@Mapper
public interface RouteDao {

    void createRouteConfig();

    List<RouteInfo> queryAllRouteConfig();
}
