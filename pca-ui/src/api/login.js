//引入二次封装axios
import request from '../utils/request'
import website from "@/config/website";

//获取图片验证码
export function getImageCaptcha(){
    return request({
        method: 'get',
        url: '/image/captcha'
    })
}

//密码登录
export function passwordLogin(formData){

    const basicAuth = 'Basic ' + website.passwordLoginClient ;

    return request({
        method:'post',
        headers: {
            Authorization: basicAuth
        },
        url:'/petal-auth/oauth2/token?grant_type=password&scope=server',
        data:formData
    })
}