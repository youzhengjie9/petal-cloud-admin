<template>
  <div class="main">
    <div class="backImg"></div>
    <div class="login">
      <password-login-form ref="pwdForm"
       :passwordLoginForm="passwordLoginForm"
       :passwordLoginRules="passwordLoginRules"
       :imageBase64="imageBase64"
       @refreshCaptcha="refreshCaptcha"
       @passwordLogin="passwordLogin"></password-login-form>

    </div>
  </div>
</template>

<script>
//引入api中的方法
import { getImageCaptcha,passwordLogin } from "@/api/login";
import{
  initDynamicRouter
}from '@/utils/permission'
import PasswordLoginForm from "@/components/login/PasswordLoginForm.vue";

export default {
  components:{
    PasswordLoginForm
  },
  data() {
    return {
      //form表单数据
      passwordLoginForm: {
        username:'',
        password:'',
        codeKey:'', //存储在redis中的正确的验证码的key，通过这个key能找到正确的验证码
        code:'' //前端输入的验证码
      },
      //配置前端表单校验规则
      passwordLoginRules: {
        //配置username校验规则
        username: [
          {
            required: true, //必填项
            message: "请输入帐号",
            trigger: "blur",
          },
          {
            min: 3, //长度不能小于3位
            max: 15, //长度不能大于15位
            message: "帐号长度要在3-15位之间",
            trigger: "blur",
          },
        ],
        //配置password校验规则
        password: [
          {
            required: true, //必填项
            message: "请输入密码",
            trigger: "blur",
          },
          {
            min: 5, //长度不能小于5位
            max: 20, //长度不能大于20位
            message: "密码长度要在5-20位之间",
            trigger: "blur",
          },
        ],
        //配置验证码规则
        code: [
          {
            required: true, //必填项
            message: "请输入验证码",
            trigger: "blur",
          },
          {
            min: 5,
            max: 5,
            message: "验证码长度只能为5位",
            trigger: "blur",
          },
        ],
      },
      //验证码图片的base64编码数据
      imageBase64: "",
    };
  },
  mounted() {
    //一进入login页面自动刷新验证码
    this.refreshCaptcha();
  },
  methods: {
    //密码登录逻辑
    passwordLogin(passwordLoginForm) {
      
      this.$refs['pwdForm'].$refs[passwordLoginForm].validate((valid) => {
        //如果前端校验通过，则进入这里
        if (valid) {
          const newFormData={
            username:this.passwordLoginForm.username,
            password:this.passwordLoginForm.password,
            // image_captcha_key:this.form.codeKey, //存储在redis中的正确的验证码的key，通过这个key能找到正确的验证码
            // image_captcha:this.form.code, //前端输入的验证码
          }

          //调用userLogin的api方法
          passwordLogin(newFormData).then((res) => {
            // 拿到oauth2登录返回的数据（比如accessToken和refreshToken等等）
            let data=res.data;
            console.log(data)
            //用户登录成功
            if(res.status===200)
            {
                this.$store.dispatch('loginSuccess',data);
                this.$message({
                    showClose: true,
                    message: '登录成功',
                    type: 'success',
                    duration:1000
                });
                //登陆成功后就可以为这个用户生成动态路由（调用permission.js的初始化动态路由方法）
                initDynamicRouter();

                //登录成功后跳转到首页
                this.$router.push({
                  path:'/'
                })
            }
            else{
              this.$message({
                    showClose: true,
                    message: '登录失败,请检查系统是否出现了错误',
                    type: 'error',
                    duration:1000
                });
            }
            
        }).catch((err)=>{
          console.log(err)
            this.$message({
              showClose: true,
              message: '登录失败,请检查输入的帐号或密码是否正确',
              type: 'error',
              duration:1000
            });
        })
     
        } else {
          return false;
        }
      });
    },
    //调用刷新验证码api接口
    refreshCaptcha() {
      getImageCaptcha()
        .then((res) => {
          console.log(res)
          //把验证码的key存储到表单对象中，请求登录接口时方便通过携带这个key从redis中找到正确的验证码
          this.passwordLoginForm.codeKey=res.data.data.imageCaptchaKey;
          //存储验证码图片base64
          this.imageBase64 = res.data.data.imageCaptchaBase64;
        })
        .catch((err) => {});
    },
  },
};
</script>

<style scoped>

.backImg {
  /*background: url("../../assets/login-page.jpg");*/
  background: url("@/assets/login-page.jpg");
  background-size: 100% 100%;
  position: fixed;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
}
.login {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 1px solid #ccc;
  background: #fff;
  width: 22%;
  padding: 20px 20px 25px 20px;
}
.login .title {
  text-align: center;
  padding-bottom: 5px;
}
.login .title span {
  font-size: 30px;
  color: #000;
}

.login .list {
  display: flex;
  align-items: center;
  padding: 2px 0;
}
.login .list input {
  border-radius: 3px;
  border: none;
  outline: none;
  color: #999;
  border: 1px solid #bdbdbd;
  font-size: 14px;
  line-height: 35px;
  padding: 0 10px;
  display: block;
  box-sizing: border-box;
  flex: 7;
}

.login .list .getCode span {
  font-size: 20px;
  background: #f5f7fa;
  color: #777;
  border-radius: 4px;
  line-height: 38px;
  border: 1px solid #ccc;
  display: inline-block;
  margin-left: 10px;
  width: 80px;
  text-align: center;
  user-select: none;
  cursor: pointer;
}

.btn {
  display: flex;
  justify-content: flex-end;
  padding-top: 5px;
}
.btn button {
  font-size: 13px;
  color: #fff;
  background: #46b5ff;
  outline: none;
  border: none;
  line-height: 35px;
  padding: 0 20px;
  display: inline-block;
  flex: 1;
  cursor: pointer;
}

</style>