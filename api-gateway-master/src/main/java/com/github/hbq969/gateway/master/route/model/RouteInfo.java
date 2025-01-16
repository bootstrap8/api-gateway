package com.github.hbq969.gateway.master.route.model;

import com.github.hbq969.gateway.master.utils.GsonUtils;
import com.google.common.reflect.TypeToken;
import lombok.Data;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

/**
 * @author hbq969@gmail.com
 */
@Data
public class RouteInfo {

    private String id;
    private String uri;
    private String predicates;
    private String filters;
    private int order;
    private String app;
    private String roleName;

    public RouteDefinition entry() {
        RouteDefinition fin = new RouteDefinition();
        fin.setId(id);
        try {
            fin.setUri(new URI(uri));
        } catch (URISyntaxException e) {
        }
        fin.setPredicates(GsonUtils.parseArray(predicates, new TypeToken<List<PredicateDefinition>>() {
        }));
        fin.setFilters(GsonUtils.parseArray(filters, new TypeToken<List<FilterDefinition>>() {
        }));
        fin.setOrder(order);
        return fin;
    }

    public void to(RouteDefinition fin) {
        this.id = fin.getId();
        this.uri = fin.getUri().toString();
        if (Objects.nonNull(fin.getPredicates())) {
            this.predicates = GsonUtils.toJSONString(fin.getPredicates());
        }
        if (Objects.nonNull(fin.getFilters())) {
            this.filters = GsonUtils.toJSONString(fin.getFilters());
        }
        this.order = fin.getOrder();
    }
}
