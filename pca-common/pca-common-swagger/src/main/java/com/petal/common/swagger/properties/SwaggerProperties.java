package com.petal.common.swagger.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * swagger属性
 *
 * @author youzhengjie
 * @date 2023/05/17 09:18:19
 */
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {

    /**
     * 是否开启 (true=开启,false=隐藏。生产环境建议设置为false)
     */
    private boolean enable;

    /**
     * 接口文档描述
     */
    private String description;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人的网站url
     */
    private String contactUrl;

    /**
     * 联系人的电子邮件
     */
    private String contactEmail;

    public boolean getEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactUrl() {
        return contactUrl;
    }

    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}
