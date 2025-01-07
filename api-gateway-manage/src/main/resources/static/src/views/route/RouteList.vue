<template>
  <el-button type="info" :icon="SwitchButton" circle @click="logout" title="注销" class="logout"/>
  <div class="container">
    <el-divider content-position="left">查询条件</el-divider>
    <el-form :inline="true" class="demo-form-inline" size="small">
      <el-form-item label="路由选项" prop="routeSelect" style="width: 150px">
        <el-select v-model="fq.routeSelect" class="m-2" placeholder="请选择"
                   @change="showSelect">
          <el-option v-for="item in data.routeMetaSelects" :key="item.value" :label="item.label"
                     :value="item.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="路由关键字" prop="routeKey">
        <el-input v-model="fq.routeKey" placeholder="请输入关键字..." @keyup.enter="queryRoutes" style="width: 100px"/>
      </el-form-item>
      <el-form-item label="是否启用" prop="enabled">
        <el-select v-model="fq.enabled" class="m-2" placeholder="请选择" style="width: 100px">
          <el-option v-for="item in data.enabledSelects" :key="item.value" :label="item.label"
                     :value="item.value"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="queryRoutes">查询</el-button>
        <el-button type="warning" @click="refreshRoute">刷新路由</el-button>
        <el-button type="primary" :icon="Edit" circle @click="addRoute" title="添加路由"/>
      </el-form-item>
    </el-form>

    <el-divider content-position="left">路由列表</el-divider>
    <el-table :data="data.routeData" style="width: 100%" :border="true" table-layout="fixed"
              :stripe="true" size="small"
              :highlight-current-row="true" :header-cell-style="headerCellStyle">
      <el-table-column fixed="left" label="操作" width="180" header-align="center" align="center">
        <template #default="scope">
          <div v-if="scope.row.id == 'api-gateway-manage'">
            网关管理页面
          </div>
          <div v-else>
            <el-button link type="primary" size="small" @click="editRoute(scope)">编辑</el-button>
            <el-popconfirm title="你确定要复制本条路由吗?" @confirm="copyRoute(scope)"
                           icon-color="red" confirm-button-type="danger">
              <template #reference>
                <el-button link type="primary" size="small">复制</el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm title="你确定要删除本条路由吗?" @confirm="delConfirm(scope)"
                           icon-color="red"
                           confirm-button-type="danger">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm title="你确定要修改路由状态吗?" @confirm="startOrStop(scope)"
                           confirm-button-type="danger"
                           icon-color="red">
              <template #reference>
                <el-button link type="danger" size="small" v-if="scope.row.enabled == 1">停止
                </el-button>
                <el-button link type="success" size="small" v-else>启动</el-button>
              </template>
            </el-popconfirm>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="路标标识" :show-overflow-tooltip="true"
                       header-align="center" width="250"/>
      <el-table-column prop="uri" label="uri" :show-overflow-tooltip="true" header-align="center" width="400"/>
      <el-table-column prop="order" label="优先级(越小越高)" :show-overflow-tooltip="true"
                       header-align="center" width="130"/>
      <el-table-column label="启用状态" :show-overflow-tooltip="true" header-align="center"
                       align="center" width="80">
        <template #default="scope">
          <el-tag class="ml-2" type="success" v-if="scope.row.enabled==1">{{
              scope.row.fmtEnabled
            }}
          </el-tag>
          <el-tag class="ml-2" type="info" v-else>{{ scope.row.fmtEnabled }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="fmtUpdateTime" label="修改时间" :show-overflow-tooltip="true"
                       header-align="center"/>
    </el-table>
    <el-pagination class="page" v-model:page-size="fq.pageSize" v-model:current-page="fq.pageNum"
                   layout="->, total, sizes, prev, pager, next, jumper" v-model:total="fq.total"
                   @size-change="queryRoutes"
                   @current-change="queryRoutes" @prev-click="queryRoutes" @next-click="queryRoutes"
                   :small="true" :background="true"
                   :page-sizes="[5, 10, 20, 50, 100]"/>

  </div>
</template>

<script>
import request from '@/network'
import {reactive} from 'vue'
import {DocumentAdd, Edit, SwitchButton} from '@element-plus/icons-vue'
import {msg} from '@/utils/Utils'
import router from '@/router/index'

export default {
  name: "Route",
  data() {
    return {Edit, DocumentAdd, SwitchButton}
  },
  setup() {
    const fq = reactive({
      routeSelect: 'route_id',
      routeKey: '',
      enabled: '-1',
      pageNum: 1,
      pageSize: 10,
      total: 0,
    })

    const data = reactive({
      routeMetaSelects: [],
      enabledSelects: [
        {label: '请选择', value: '-1'},
        {label: '启用', value: '1'},
        {label: '停用', value: '0'}
      ],
      routeData: []
    })

    return {fq, data}
  },
  methods: {
    // 表格头
    headerCellStyle()  {
      // 添加表头颜色
      return {backgroundColor: '#f5f5f5', color: '#333', fontWeight: 'bold'};
    },
    // 查询查询条件下拉列表
    getRoutMetaSelects() {
      request({
        url: '/route/queryRouteOptions'
      }).then(res => {
        console.log('请求到路由类型配置: {}', res)
        if (res.data.state == 'OK') {
          this.data.routeMetaSelects = res.data.body;
        }
      }).catch(e => {
        console.error(e);
      });
    },
    // 条件切换触发
    showSelect() {
      console.log('选择的下拉列值: ', this.routeSelect)
    },
    // 查询触发
    queryRoutes() {
      console.log('查询路由列表')
      request({
        url: '/route/queryAllRouteConfig',
        params: this.fq
      }).then(res => {
        // console.log('请求到路由数据: ', res)
        if (res.data.state == 'OK') {
          this.fq.total = res.data.body.total;
          this.data.routeData = res.data.body.list;
          this.data.routeData.forEach(item => {
            if (item.enabled == 1) {
              item.fmtEnabled = '启用'
            } else {
              item.fmtEnabled = '停用'
            }
          })
        }
      }).catch(e => {
        console.error(e);
      })
    },
    delConfirm(scope) {
      this.delRoute(scope);
    },
    // 删除路由
    delRoute(scope) {
      const routeId = scope.row.id
      console.log('删除选中的元素: ', routeId)
      request({
        url: '/route/deleteRouteConfig',
        method: 'post',
        data: {id: routeId}
      }).then(res => {
        if (res.data.state == 'OK') {
          msg('操作成功', 'success')
          this.queryRoutes()
        }
      }).catch(e => {
        alert('删除失败')
      })
    },
    // 编辑路由
    editRoute(scope) {
      scope.row.type = 'edit'
      console.log('编辑选中的元素: ', scope.row);
      router.push({
        path: '/detail',
        query: scope.row
      })
    },
    // 启停状态改变
    startOrStop(scope) {
      console.log('启停选中的元素: ', scope.row);
      const newEnabled = scope.row.enabled == 1 ? 0 : 1;
      request({
        url: '/route/startOrStop',
        method: 'post',
        data: {id: scope.row.id, enabled: newEnabled}
      }).then(res => {
        if (res.data.state == 'OK') {
          msg('修改成功', 'success')
          this.queryRoutes()
        }
      }).catch(e => {
        msg('修改失败', 'error')
      })
    },
    // 刷新路由配置
    refreshRoute() {
      request({
        url: '/route/refresh',
        method: 'post'
      }).then(res => {
        if (res.data.state == 'OK') {
          msg('操作成功', 'success')
        }
      }).catch(e => {
        alert('刷新失败');
      })
    },
    // 添加路由
    addRoute() {
      router.push({
        path: '/detail',
        query: {type: 'add'}
      });
    },
    // 复制路由
    copyRoute(scope) {
      scope.row.type = 'copy'
      console.log('复制选中的元素: ', scope.row);
      router.push({
        path: '/detail',
        query: scope.row
      })
    },
    logout(){
      window.location.href='/logout'
    }
  },
  created() {
    console.log('加载完Route.vue视图')
    this.getRoutMetaSelects()
    this.queryRoutes();
  }
}
</script>

<style scoped>
.container {
  flex-grow: 1;
  padding: 20px 1%;
  overflow: auto;
  width: 90%;
}
.logout {
  position: absolute;
  right: 10px;
  top: 10px;
}
</style>
