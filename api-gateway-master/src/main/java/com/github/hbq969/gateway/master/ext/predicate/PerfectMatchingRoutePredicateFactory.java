package com.github.hbq969.gateway.master.ext.predicate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
@Slf4j
public class PerfectMatchingRoutePredicateFactory extends
    AbstractRoutePredicateFactory<PerfectMatchingRoutePredicateFactory.Config> {

  public static final String HEADER_KEY = "header";

  public static final String HEADER_VAL = "value";

  public PerfectMatchingRoutePredicateFactory() {
    super(Config.class);
  }


  public PerfectMatchingRoutePredicateFactory(Class<Config> clz) {
    super(clz);
  }

  @Override
  public Predicate<ServerWebExchange> apply(Config config) {
    return (GatewayPredicate) exchange -> {
      List<String> values = exchange.getRequest().getHeaders()
          .getOrDefault(config.header, Collections.emptyList());
      if (values.isEmpty()) {
        return false;
      }
      return values.stream().anyMatch(v -> v.equals(config.value));
    };
  }

  @Override
  public List<String> shortcutFieldOrder() {
    return Arrays.asList(HEADER_KEY, HEADER_VAL);
  }

  @Override
  public String name() {
    return super.name();
  }

  @Data
  public static class Config {

    @NotEmpty
    private String header;
    private String value;
  }

}