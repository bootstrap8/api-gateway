insert into tab_gateway_route(route_id,uri,predicates,filters,route_order,enabled,update_time,temp_id)
values('api-gateway-manage','http://localhost:30139','[{"args":{"pattern":"/api/web/api-gateway-manage/**"},"name":"Path"}]','[{"name": "RewritePath","args": {"regexp": "/api/web/api-gateway-manage/(?<remaining>.*)","replacement": "/${remaining}"}}]',0,1,1686826889000,1);
insert into tab_gateway_route(route_id,uri,predicates,filters,route_order,enabled,update_time,temp_id)
values('api-gateway-eureka-api','http://localhost:30140','[{"name": "Path","args": {"pattern": "/api/web/api-gateway-eureka/**"}}]','[{"name": "RewritePath","args": {"regexp": "/api/web/api-gateway-eureka/(?<remaining>.*)","replacement": "/${remaining}"}}]',0,1,1686826889000,1);
insert into tab_gateway_route(route_id,uri,predicates,filters,route_order,enabled,update_time,temp_id)
values('api-gateway-eureka-static','http://localhost:30140','[{"name": "Path","args": {"pattern": "/eureka/**"}}]','[]',0,1,1686826889000,1);