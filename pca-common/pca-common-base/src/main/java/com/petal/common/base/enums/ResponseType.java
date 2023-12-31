package com.petal.common.base.enums;


/**
 * 响应类型枚举类
 *
 * @author youzhengjie
 * @date 2023-04-19 12:17:30
 */
public enum ResponseType {

    /**
     * 通用状态
     */
    SUCCESS(200,"接口请求成功"),
    ERROR(500,"接口请求失败"),

    UNAUTHORIZED(401,"认证失败"),
    FORBIDDEN(403,"权限不足,拒绝访问"),
    /**
     * 用户状态
     */
    NOT_LOGIN(401,"用户未登录，请重新登录"),
    NOT_PERMISSION(403,"用户没有权限"),
    /**
     * 登录状态
     */
    LOGIN_SUCCESS(600,"用户登录成功！"),
    USERNAME_PASSWORD_ERROR(601,"用户名或者密码不正确"),
    CODE_ERROR(602,"验证码错误"),
    ACCOUNT_NOT_EXIST(603,"账号不存在"),
    ACCOUNT_LOCKED(604,"账号被锁定"),
    ACCOUNT_CREDENTIAL_EXPIRED(605,"用户凭证已失效"),
    ACCOUNT_DISABLE(606,"账号已被禁用"),
    /**
     * 用户退出状态
     */
    LOGOUT_SUCCESS(800,"退出登录成功"),
    LOGOUT_ERROR(801,"退出登录失败"),

    /**
     * 文件操作状态
     */
    IMAGE_UPLOAD_SUCCESS(901,"图片上传成功"),
    IMAGE_UPLOAD_ERROR(902,"图片上传失败"),
    FILE_FORMAT_UNSUPPORT(903,"不支持该文件格式，上传失败"),
    FILE_DELETE_SUCCESS(904,"文件删除成功"),
    FILE_DELETE_ERROR(905,"文件删除失败"),

    /**
     * 导出excel状态
     */
    EXPORT_EXCEL_ERROR(1001,"导出excel失败"),

    /**
     * 注册状态
     */
    REGISTER_SUCCESS(1100,"注册用户成功"),
    REGISTER_ERROR(1101,"注册用户失败"),
    SEND_PHONE_CODE_SUCCESS(1102,"发送短信验证码成功,验证码将在5分钟后失效"),
    SEND_PHONE_CODE_ERROR(1103,"发送短信验证码失败"),
    PHONE_CODE_ERROR(1104,"短信验证码不正确"),
    PHONE_ERROR(1105,"手机号格式不正确"),
    CONFIRM_PASSWORD_ERROR(1106,"两次输入的密码不一致"),
    USERNAME_EXISTED(1107,"该用户名已被占用,请使用不同的用户名")
    ;


    private int code;
    private String message;

    ResponseType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
