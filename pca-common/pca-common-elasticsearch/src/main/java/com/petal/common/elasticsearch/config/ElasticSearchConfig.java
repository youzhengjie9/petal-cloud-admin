package com.petal.common.elasticsearch.config;

import com.petal.common.elasticsearch.properties.ElasticSearchProperties;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ElasticSearch配置类
 *
 * @author youzhengjie
 * @date 2022/10/28 23:23:43
 */
@Configuration
public class ElasticSearchConfig {

    private ElasticSearchProperties elasticSearchProperties;

    @Autowired
    public void setElasticSearchProperties(ElasticSearchProperties elasticSearchProperties) {
        this.elasticSearchProperties = elasticSearchProperties;
    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        HttpHost httpHost = new HttpHost(elasticSearchProperties.getHostname(),
                elasticSearchProperties.getPort(), elasticSearchProperties.getScheme());

        RestClientBuilder restClientBuilder = RestClient.builder(httpHost);
        return new RestHighLevelClient(restClientBuilder);
    }


}
