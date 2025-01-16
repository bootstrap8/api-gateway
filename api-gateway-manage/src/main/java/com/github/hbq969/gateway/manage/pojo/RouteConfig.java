package com.github.hbq969.gateway.manage.pojo;

import com.github.hbq969.code.common.utils.GsonUtils;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;

import java.util.List;
import java.util.Objects;

/**
 * @author hbq969@gmail.com
 */
@Data
public class RouteConfig {

  private String id;
  private String uri;
  private String predicates;
  private String filters;
  private int order;
  private int enabled;
  private long updateTime;
  private String fmtUpdateTime;
  private int tempId;
  private String app;
  private String roleName;

  public RouteInfo info() {
    RouteInfo info = new RouteInfo();
    info.setId(id);
    info.setUri(uri);
    if (Objects.nonNull(predicates)) {
      info.setPredicates(GsonUtils.parseArray(predicates, new TypeToken<List<PredicateDefinition>>() {
      }));
    }
    if (Objects.nonNull(filters)) {
      info.setFilters(GsonUtils.parseArray(filters, new TypeToken<List<FilterDefinition>>() {
      }));
    }
    info.setOrder(order);
    info.setEnabled(enabled);
    info.setUpdateTime(updateTime);
    info.setTempId(tempId);
    info.setApp(app);
    info.setRoleName(roleName);
    return info;
  }
}
