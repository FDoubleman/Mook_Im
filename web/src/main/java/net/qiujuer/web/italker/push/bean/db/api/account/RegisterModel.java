package net.qiujuer.web.italker.push.bean.db.api.account;

import com.google.gson.annotations.Expose;

/**
 * Created by fmm on 2017/8/30.
 */
public class RegisterModel {

    @Expose
    private String account;
    @Expose
    private String password;
    @Expose
    private String name;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
