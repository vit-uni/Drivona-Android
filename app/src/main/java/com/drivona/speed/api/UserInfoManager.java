package com.drivona.speed.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lalifa.utils.ObjToSP;


/**
 * @author: BaiCQ
 * @ClassName: UserManager
 * @Description: 信息的缓存辅助类
 */
public class UserInfoManager extends ObjToSP<UserData> {
    private final static UserInfoManager _manager = new UserInfoManager();

    private UserInfoManager() {
        super("SP_USER");
    }

    private UserData current;

    @Nullable
    public static UserData get() {
        return _manager.getUser();
    }

    public static void save(@NonNull UserData user) {
        _manager.saveUser(user);
    }

    public static void logout() {
        _manager.clear();
    }

    /**
     * 保存当前用户信息
     *
     * @param user UserData
     */
    private void saveUser(UserData user) {
        if (null != user) {
            current = user;
            super.saveEntity(TAG, current);
        }
    }

    /**
     * 获取最新用户的信息
     */
    private UserData getUser() {
        if (null != current) {
            return current;
        }
        UserData user = super.getEntity(TAG);
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
