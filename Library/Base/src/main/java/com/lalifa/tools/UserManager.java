package com.lalifa.tools;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lalifa.api.UserBean;
import com.lalifa.utils.ObjToSP;


/**
 * @author: BaiCQ
 * @ClassName: UserManager
 * @Description: 信息的缓存辅助类
 */
public class UserManager extends ObjToSP<UserBean> {
    private final static UserManager _manager = new UserManager();

    private UserManager() {
        super("SP_USER");
    }

    private UserBean current;

    @Nullable
    public static UserBean get() {
        return _manager.getUser();
    }

    public static void save(@NonNull UserBean user) {
        _manager.saveUser(user);
    }

    public static void logout() {
        _manager.clear();
    }

    /**
     * 保存当前用户信息
     *
     * @param user UserBean
     */
    private void saveUser(UserBean user) {
        if (null != user) {
            current = user;
            super.saveEntity(TAG, current);
        }
    }

    /**
     * 获取最新用户的信息
     */
    private UserBean getUser() {
        if (null != current) {
            return current;
        }
        UserBean user = super.getEntity(TAG);
        if (null != user) {
            current = user;
        }
        return current;
    }

    private void clear() {
        current = null;
        super.deleteFast(TAG);
    }
}
