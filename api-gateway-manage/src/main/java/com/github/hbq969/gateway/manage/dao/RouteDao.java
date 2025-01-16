package com.github.hbq969.gateway.manage.dao;

import java.util.List;

import com.github.hbq969.gateway.manage.pojo.RouteConfig;
import com.github.hbq969.gateway.manage.pojo.TemplateInfo;
import io.swagger.annotations.Api;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

/**
 * @author hbq969@gmail.com
 */
@Repository("common-gateway-route-dao-RouteDao")
@Mapper
public interface RouteDao {

    void createUserInfo();

    void createUserAuthorities();

    void createRouteConfig();

    void saveRouteConfig(RouteConfig route);

    void updateRouteConfig(RouteConfig route);

    void deleteRouteConfig(@Param("app") String app, @Param("roleName") String roleName, @Param("id") String id);

    void updateStartOrStop(@Param("app") String app, @Param("roleName") String roleName, @Param("id") String id, @Param("enabled") int enabled
            , @Param("updateTime") long updateTime);

    List<RouteConfig> queryAllRouteConfig(RowBounds rb,
                                          @Param("app") String app,
                                          @Param("roleName") String roleName,
                                          @Param("routeSelect") String routeSelect,
                                          @Param("routeKey") String routeKey,
                                          @Param("enabled") int enabled);

    RouteConfig queryRoute(@Param("id") String id);

    RouteConfig queryRouteBySession(@Param("app") String app, @Param("roleName") String roleName, @Param("id") String id);

    void createRouteTemplate();

    List<TemplateInfo> queryRouteTemplateInfos();
}
