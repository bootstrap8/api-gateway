package com.github.hbq969.gateway.manage.pojo;

import com.github.hbq969.code.common.utils.StrUtils;
import com.github.hbq969.code.common.utils.GsonUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hbq969@gmail.com
 */
@Data
@Slf4j
public class RouteInfo implements Cloneable {

    private String id;
    private String uri;
    private List<PredicateDefinition> predicates;
    private List<FilterDefinition> filters;
    private int order;
    private int enabled;
    private long updateTime;
    private int tempId;
    private String app;
    private String roleName;

    /**
     * uri是否包含多个负载地址
     *
     * @return
     */
    public boolean isMultipleUri() {
        return StrUtils.strNotEmpty(uri) && (uri.contains(",") || uri.contains(";"));
    }

    public List<RouteInfo> multipleRouteInfos() {
        List<RouteInfo> ris = new ArrayList<>(8);
        if (isMultipleUri()) {
            List<PredicateDefinition> weightPredicates = getWeightPredicates();
            List<PredicateDefinition> unWeightPredicates = getUnWeightPredicates();
            log.info("权重谓语: {}", GsonUtils.toJson(weightPredicates));
            log.info("非权重谓语: {}", GsonUtils.toJson(unWeightPredicates));
            List<String> uris = Splitter.onPattern("[,;]").trimResults().splitToList(uri);
            for (int i = 0; i < uris.size(); i++) {
                try {
                    RouteInfo info = (RouteInfo) this.clone();
                    info.setId(Joiner.on("_").join(info.getId(), "weight", i + 1));
                    info.setUri(uris.get(i));
                    List<PredicateDefinition> pds = new ArrayList<>();
                    pds.addAll(unWeightPredicates);
                    pds.add(weightPredicates.get(i));
                    info.setPredicates(pds);
                    ris.add(info);
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
            return ris;
        } else {
            ris.add(this);
            return ris;
        }
    }

    public RouteConfig config() {
        RouteConfig config = new RouteConfig();
        config.setId(id);
        config.setUri(uri);
        config.setPredicates(GsonUtils.toJson(predicates));
        config.setFilters(GsonUtils.toJson(filters));
        config.setOrder(order);
        config.setEnabled(enabled);
        config.setUpdateTime(updateTime);
        config.setFmtUpdateTime(DateFormatUtils.format(updateTime, "yyyy-MM-dd HH:mm:ss"));
        config.setTempId(tempId);
        config.setApp(app);
        config.setRoleName(roleName);
        return config;
    }

    List<PredicateDefinition> getWeightPredicates() {
        return predicates.stream().filter(p ->
                        StringUtils.equals("Weight", p.getName()))
                .collect(Collectors.toList());
    }

    List<PredicateDefinition> getUnWeightPredicates() {
        return predicates.stream().filter(p ->
                        !StringUtils.equals("Weight", p.getName()))
                .collect(Collectors.toList());
    }

}
