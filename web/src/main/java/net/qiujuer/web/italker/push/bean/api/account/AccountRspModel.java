package net.qiujuer.web.italker.push.bean.api.account;

import com.google.gson.annotations.Expose;
import net.qiujuer.web.italker.push.bean.card.UserCard;
import net.qiujuer.web.italker.push.bean.db.User;


/**
 * Created by fmm on 2017/8/31.
 *
 */
public class AccountRspModel {
    //用户信息相关
    @Expose
    private UserCard user;
    //账户
    @Expose
    private String account;
    //token
    @Expose
    private String token;
    //是否绑定设备
    @Expose
    private boolean isBind;

    public AccountRspModel(User user) {
        this(user,false);
    }

    public AccountRspModel(User user, boolean isBind) {
        this.user = new UserCard(user);
        this.account =user.getPhone();
        this.token =user.getToken();
        this.isBind = isBind;
    }

    public UserCard getUser() {
        return user;
    }

    public void setUser(UserCard user) {
        this.user = user;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }
}
