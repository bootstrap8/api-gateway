package com.github.hbq969.gateway.manage.service;

import java.util.List;

import com.github.hbq969.gateway.manage.pojo.RouteInfo;
import com.github.hbq969.gateway.manage.pojo.TemplateInfo;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hbq969@gmail.com
 */
public interface RouteService {

  void refreshRouteConfig();

  @Transactional(rollbackFor = Exception.class)
  default void saveRouteConfig(RouteInfo route) {
    throw new UnsupportedOperationException();
  }

  @Transactional(rollbackFor = Exception.class)
  default void updateRouteConfig(RouteInfo route) {
    throw new UnsupportedOperationException();
  }

  @Transactional(rollbackFor = Exception.class)
  default void deleteRouteConfig(String id) {
    throw new UnsupportedOperationException();
  }

  @Transactional(rollbackFor = Exception.class)
  default void updateStartOrStop(String id, int enabled) {
    throw new UnsupportedOperationException();
  }

  default List<RouteInfo> queryAllRouteConfig(int pageNum, int pageSize, String routeSelect,
      String routeKey, int enabled) {
    throw new UnsupportedOperationException();
  }

  default RouteInfo queryRoute(String id) {
    throw new UnsupportedOperationException();
  }

  default List<TemplateInfo> queryRouteTemplateInfos() {
    throw new UnsupportedOperationException();
  }
}
