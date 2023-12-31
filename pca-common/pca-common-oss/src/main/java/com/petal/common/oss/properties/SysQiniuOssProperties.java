package com.petal.common.oss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 七牛云oss配置类
 *
 * @author youzhengjie
 * @date 2023-04-08 16:27:36
 */
@Component
@Data
@ConfigurationProperties(prefix = "qiniu")
public class SysQiniuOssProperties {

    /**
     * 下面的AK、SK都要从七牛云的密钥管理中获取
     */
    private String accessKey;
    private String secretKey;
    /**
     * 指定存储空间名称
     */
    private String bucket;
    /**
     * 七牛云图片服务器域名（有效期30天，过期可以重新创建新的存储空间）
     */
    private String ossUrl;

}
