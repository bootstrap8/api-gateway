<template>
  <div class="container">
    <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" :inline="false" size="small" status-icon>
      <el-form-item label="路由模板">
        <el-select v-model="tempSelect.tid" placeholder="请选择对应的模板"
                   @change="tempChange(tempSelect.tid)"
                   :disabled="tplDisabled" :popper-append-to-body="false" popper-class="eloption">
          <el-option v-for="(item, index) in templates" :label="item.name" :value="item.tid"/>
        </el-select>
      </el-form-item>
      <el-form-item label="路由标识" prop="rid">
        <el-input v-model="ruleForm.rid" placeholder="请输入路由标识..." :disabled="ridDisabled" @blur="orderReset()"/>
      </el-form-item>
      <el-form-item label="路由uri" prop="uri">
        <el-input v-model="ruleForm.uri" placeholder="请输入uri..."/>
      </el-form-item>
      <el-form-item label="路由谓语" prop="predicates">
        <el-input v-model="ruleForm.predicates" type="textarea" :rows="row" :cols="col" style="font-size: small"/>
        <el-link @click="formatPredicates">
          格式化
          <el-icon class="el-icon--right">
            <icon-view/>
          </el-icon>
        </el-link>
      </el-form-item>
      <el-form-item label="路由过滤" prop="filters">
        <el-input v-model="ruleForm.filters" type="textarea" :rows="row" :cols="col" style="font-size: small"/>
        <el-link @click="formatFilters">
          格式化
          <el-icon class="el-icon--right">
            <icon-view/>
          </el-icon>
        </el-link>
      </el-form-item>
      <el-form-item label="优先级" prop="order" title="数值越小优先级越大">
        <el-input v-model="ruleForm.order" placeholder="请输入路由标识..." type="number"/>
      </el-form-item>
      <el-form-item label="优先级" prop="order" style="display: none" title="数值越小优先级越大">
        <el-input v-model="ruleForm.enabled" type="number"/>
      </el-form-item>
      <el-form-item>
        <el-button @click="goBack(ruleFormRef)">返回</el-button>
        <el-button type="primary" @click="submitForm(ruleFormRef)">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts" setup>

import {onMounted, reactive, ref} from 'vue'
import axios from '@/network'
import type {FormInstance, FormRules} from 'element-plus'
import {View as IconView} from '@element-plus/icons-vue'
import {RouteInfo, RouteTemplate} from '@/type/Route'
import {msg, queryScopeValue} from '@/utils/Utils'
import router from '@/router/index'

// 表单校验规则
const ridDisabled = ref(false)
const tplDisabled = ref(false)
const ruleFormRef = ref<FormInstance>()
const ruleForm = reactive<RouteInfo>(new RouteInfo('', '', '', '', 100, '1', 1))
const rules = reactive<FormRules>({
  rid: [{
    validator: (rule: any, value: any, callback: any) => {
      if (value == '') {
        callback(new Error('路由标识不能为空'))
      } else if (isCopy() && value == queryScopeValue(router, 'id')) {
        callback(new Error('请给路由标识改个名字'))
      } else {
        callback()
      }
    }, trigger: 'blur'
  }],
  uri: [{
    validator: (rule: any, value: any, callback: any) => {
      if (value == '') {
        callback(new Error('路由不能为空'))
      } else if (!value.match(/(http):\/\/([\w.]+\/?)\S*/ig)) {
        callback(new Error('路由必须要以http://开头'))
      } else {
        callback()
      }
    }, trigger: 'blur'
  }],
  predicates: [{required: true, message: '路由谓语不能为空', trigger: 'blur'}],
  filters: [{required: true, message: '路由过滤不能为空', trigger: 'blur'}],
  order: [{required: true, message: '路由优先级不能为空', trigger: 'blur'}]
})


const row = 8;
const col = 20;

const templates = reactive<RouteTemplate[]>([])
const tempSelect = reactive<RouteTemplate>(new RouteTemplate(1, '', '', '', '', 0));

const tempChange = (tid: any) => {
  const selected: RouteTemplate = templates.filter((t: RouteTemplate) => t.tid + '' == tid)[0];
  tempSelect.copy(selected);
  ruleForm.setUseTemp(selected);
  console.log('选择中的模板: {}', tempSelect);
}

// 优先级根据输入的路由标识进行调整
const orderReset = () => {
  if (ruleForm.rid && ruleForm.rid.indexOf('gray') != -1) {
    ruleForm.order = 0
  } else {
    ruleForm.order = 100
  }
}

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      const data = ruleForm.toRestfulObj()
      const type = queryScopeValue(router, 'type')
      if ('add' == type || 'copy' == type) {
        data.enabled = 0
      }
      axios({
        url: isNew() ? '/route/saveRouteConfig' : '/route/updateRouteConfig',
        method: 'post',
        data: data
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          msg(res.data.body, 'success');
          router.push('/')
        } else {
          msg(res.data.errorMessage, 'error')
        }
      }).catch((e: Error) => {
        msg('保存失败', 'error')
      })
    } else {
      console.log('error submit!', fields)
    }
  })
}

const goBack = (formEl: FormInstance | undefined) => {
  router.back();
}

const isCopy = () => {
  return 'copy' == queryScopeValue(router, 'type');
}

const isNew = () => {
  return isCopy() || 'add' == queryScopeValue(router, 'type')
}

const isEdit = () => {
  return isCopy() || 'edit' == queryScopeValue(router, 'type')
}

const formatPredicates = () => {
  let json = JSON.parse(ruleForm.predicates);
  ruleForm.predicates = JSON.stringify(json, null, 2)
}

const formatFilters = () => {
  let json = JSON.parse(ruleForm.filters);
  ruleForm.filters = JSON.stringify(json, null, 2)
}

onMounted(() => {
  axios({
    url: '/route/queryRouteTemplates',
    method: 'post'
  }).then(res => {
    if (res.data.state == 'OK') {
      templates.length = 0
      res.data.body.forEach((t: RouteTemplate) => templates.push(t));
      console.log('查询到模板信息: ', templates);
      if (isEdit() || isCopy()) {
        // 先选中模版
        tempChange(router.currentRoute.value.query.tempId)
        // 回填数据
        ruleForm.copy(router.currentRoute.value.query)
        ridDisabled.value = !isCopy()
        tplDisabled.value = isEdit()
      } else {
        tempChange(tempSelect.tid);
      }
    } else {
      msg(res.data.errorMessage, 'error')
    }
  }).catch(e => {
    console.error(e)
    msg('查询路由模版失败', 'error')
  })
})
</script>

<style scoped>
.container {
  flex-grow: 1;
  padding: 20px 5%;
  overflow: auto;
  width: 90%;
}
</style>
