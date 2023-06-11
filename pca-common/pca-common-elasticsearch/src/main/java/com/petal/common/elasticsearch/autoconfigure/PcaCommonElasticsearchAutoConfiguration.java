package com.petal.common.elasticsearch.autoconfigure;

import com.petal.common.elasticsearch.config.ElasticSearchConfig;
import com.petal.common.elasticsearch.properties.ElasticSearchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ElasticSearchConfig.class)
@EnableConfigurationProperties(ElasticSearchProperties.class)
public class PcaCommonElasticsearchAutoConfiguration {


}
