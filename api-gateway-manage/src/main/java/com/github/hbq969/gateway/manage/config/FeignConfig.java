package com.github.hbq969.gateway.manage.config;

import com.github.hbq969.gateway.manage.feign.MasterEndpoint;
import com.github.hbq969.code.common.spring.cloud.feign.FeignFactoryBean;
import feign.Client;
import feign.httpclient.ApacheHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : hbq969@gmail.com
 * @description : Feign自动装配
 * @createTime : 2023/8/28 13:59
 */
@Configuration
@Slf4j
public class FeignConfig {

    @Value("${gateway.endpoint.refresh.host:http://docker.for.mac.host.internal:30131}")
    private String url;

    @Bean("bc-api-gateway-manage-masterEndpoint")
    MasterEndpoint masterEndpoint() throws Exception {
        FeignFactoryBean fac = new FeignFactoryBean(){
            @Override
            protected Client client() {
                return new ApacheHttpClient();
            }
        };
        fac.setInter(MasterEndpoint.class);
        fac.setUrl(url);
        return (MasterEndpoint) fac.getObject();
    }
}
