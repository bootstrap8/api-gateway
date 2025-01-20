package com.github.hbq969.gateway.manage.web;

import com.github.hbq969.code.common.log.api.Log;
import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.hbq969.code.common.utils.GsonUtils;
import com.github.hbq969.code.common.utils.ResourceUtils;
import com.github.hbq969.gateway.manage.pojo.RouteConfig;
import com.github.hbq969.gateway.manage.pojo.RouteInfo;
import com.github.hbq969.gateway.manage.pojo.TemplateInfo;
import com.github.hbq969.gateway.manage.service.RouteService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hbq969@gmail.com
 */
@RestController("manage-web-RouteCtrl")
@RequestMapping(path = "/ui-gw/route")
@Api(description = "路由管理")
@Slf4j
public class RouteCtrl {

    @Qualifier("common-gateway-route-serv-RouteServiceImpl")
    @Autowired
    private RouteService service;

    @ApiOperation("获取路由选项列表")
    @RequestMapping(path = "/queryRouteOptions", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMessage<?> queryRouteOptions() {
        try (InputStream in = ResourceUtils.read("/", "route-options.json", null)) {
            String str = IOUtils.toString(in, "utf-8");
            List<Map> options = GsonUtils.parseArray(str, new TypeToken<List<Map>>() {
            });
            return ReturnMessage.success(options);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("分页查询路由信息")
    @RequestMapping(path = "/queryAllRouteConfig", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMessage<PageInfo> queryAllRouteConfig(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "routeSelect", defaultValue = "route_id") String routeSelect,
            @RequestParam(name = "routeKey", required = false) String routeKey,
            @RequestParam(name = "enabled", defaultValue = "-1") int enabled) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        List<RouteConfig> routes = service.queryAllRouteConfig(pageNum, pageSize, routeSelect,
                routeKey, enabled).stream().map(r -> r.config()).collect(Collectors.toList());
        PageInfo<RouteConfig> pageInfo = new PageInfo<>(routes);
        pageInfo.setTotal(page.getTotal());
        return ReturnMessage.success(pageInfo);
    }

    @ApiOperation("根据id查询路由")
    @RequestMapping(path = "/queryRouteConfig", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMessage<?> queryRouteConfig(@RequestParam(name = "id") String id) {
        return ReturnMessage.success(service.queryRoute(id));
    }

    @ApiOperation("保存路由")
    @RequestMapping(path = "/saveRouteConfig", method = RequestMethod.POST)
    @ResponseBody
    @Log(collectResult = true)
    public ReturnMessage<?> saveRouteConfig(@RequestBody RouteInfo route) {
        service.saveRouteConfig(route);
        service.refreshRouteConfig();
        return ReturnMessage.success("保存成功");
    }

    @ApiOperation("更新路由")
    @RequestMapping(path = "/updateRouteConfig", method = RequestMethod.POST)
    @ResponseBody
    @Log(collectResult = true)
    public ReturnMessage<?> updateRouteConfig(@RequestBody RouteInfo route) {
        service.updateRouteConfig(route);
        service.refreshRouteConfig();
        return ReturnMessage.success("更新路由成功");
    }

    @ApiOperation("删除路由")
    @RequestMapping(path = "/deleteRouteConfig", method = RequestMethod.POST)
    @ResponseBody
    @Log(collectResult = true)
    public ReturnMessage<?> deleteRouteConfig(@RequestBody Map info) {
        String id = MapUtils.getString(info, "id");
        service.deleteRouteConfig(id);
        service.refreshRouteConfig();
        return ReturnMessage.success(id);
    }

    @ApiOperation("修改启停状态")
    @RequestMapping(path = "/startOrStop", method = RequestMethod.POST)
    @ResponseBody
    @Log(collectResult = true)
    public ReturnMessage<?> startOrStop(@RequestBody Map info) {
        String id = MapUtils.getString(info, "id");
        int enabled = MapUtils.getIntValue(info, "enabled", 1);
        service.updateStartOrStop(id, enabled);
        service.refreshRouteConfig();
        return ReturnMessage.success(id);
    }


    @ApiOperation("刷新路由")
    @RequestMapping(path = "/refresh", method = RequestMethod.POST)
    @ResponseBody
    @Log(collectResult = true)
    public ReturnMessage<?> refresh() {
        service.refreshRouteConfig();
        return ReturnMessage.success("刷新成功");
    }

    @ApiOperation("查询路由模板列表")
    @RequestMapping(path = "/queryRouteTemplates", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage<List<TemplateInfo>> queryRouteTemplates() {
        return ReturnMessage.success(service.queryRouteTemplateInfos());
    }
}
