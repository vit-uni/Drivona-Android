package com.drivona.speed.api

/**
 * 接口地址统一管理
 */
class ApiUrl {
    companion object {

        // ============================== 通用 ==============================

        // 文件上传
        const val upload = "index/upload"

        // 获取公钥（用于接口加密）
        const val publicKey = "index/publicKey"

        // 协议内容（1用户协议 2隐私政策 3平台规则 4信息规范 ...）
        const val userRule = "user_rule"

        // ============================== 地址 ==============================

        // 获取默认地址列表
        const val addressDefaultList = "index/address_default_list"

        // 获取地址列表
        const val addressList = "index/address_list"

        // 添加地址
        const val addressAdd = "index/address_add"

        // 删除地址
        const val addressDel = "index/address_del"

        // ============================== 摄像头 / 事件 ==============================

        // 获取摄像头
        const val devices = "index/devices"

        // 获取事件
        const val events = "index/events"

        // 上报新增摄像头
        const val reportNewCamera = "api/reportNewCamera"

        // 更新摄像头上报
        const val updateCameraReport = "api/updateCameraReport"

        // ============================== 设备 ==============================

        // 添加设备
        const val equipmentAdd = "index/equipment_add"

        // 校验设备
        const val equipmentCheck = "index/equipment_check"

        // 删除设备
        const val equipmentDel = "index/equipment_del"

        // 设备初始化 / 配置设备参数
        const val equipmentInit = "index/equipment_init"

        // 获取设备列表
        const val equipmentList = "index/equipment_list"

        // ============================== 用户 ==============================

        // 获取用户信息
        const val userinfo = "index/userinfo"

        // 编辑用户资料
        const val userEdit = "index/useredit"

        // 修改密码（step=1 校验旧密码，step=2 设置新密码）
        const val changePwd = "index/change_pwd"

        // 意见反馈
        const val feedback = "index/feedback"

        // ============================== 登录 / 注册 ==============================

        // 邮箱登录
        const val login = "index/login"

        // 账号（手机号）密码登录
        const val loginMake = "login_make"

        // 退出登录
        const val loginOff = "index/login_off"

        // 邮箱注册
        const val sign = "index/sign"

        // 发送邮箱验证码（type：sign注册 forget忘记密码）
        const val sendMail = "index/sendMail"

        // 校验邮箱验证码
        const val checkMail = "index/checkMail"

        // 更换邮箱
        const val changeEmail = "index/change_email"

        // 邮箱找回密码
        const val forget = "index/forget"

        // 手机号找回密码
        const val forgetMake = "forget_make"

        // 图形验证码
        const val sendImgCode = "send_img_code"

        // 获取手机验证码（1登录验证 2绑定手机号 3更换手机号验证身份 4修改手机号 5忘记密码）
        const val sendCode = "send_code"

        // 三方登录
        const val checkAuthorizations = "check_authorizations"

        // 三方登录绑定手机号
        const val oauthUserBind = "oauth_user_bind"

        // ============================== 第三方完整地址 ==============================

        // Google 路线规划（Directions API）
        const val googleDirections = "https://maps.googleapis.com/maps/api/directions/json"

        // Google 逆地理编码（Geocoding API）
        const val googleGeocode = "https://maps.googleapis.com/maps/api/geocode/json"

        // Google Routes API 路线计算
        const val googleComputeRoutes = "https://routes.googleapis.com/directions/v2:computeRoutes"
    }
}
