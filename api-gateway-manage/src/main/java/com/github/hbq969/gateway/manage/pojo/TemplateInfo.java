package com.github.hbq969.gateway.manage.pojo;

import lombok.Data;

/**
 * @author hbq969@gmail.com
 */
@Data
public class TemplateInfo {

  private int tid;
  private String name;
  private String uri;
  private String predicates;
  private String filters;
  private int order;
}
