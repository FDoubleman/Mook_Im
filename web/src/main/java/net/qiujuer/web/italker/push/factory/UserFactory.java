package net.qiujuer.web.italker.push.factory;

import net.qiujuer.web.italker.push.bean.db.User;
import net.qiujuer.web.italker.push.utils.Hib;
import net.qiujuer.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

/**
 * Created by fmm on 2017/8/30.
 */
public class UserFactory {

    public static User register(String account,String password,String name){
        //1、对account
        account =account.trim();
        //2、密码加密
        password =encodePassword(password);
        //3、查询是否 使用account  或者 name 注册过
        Hib.query(new Hib.QueryInter<User>() {
            @Override
            public User query(Session session) {
                session.createQuery("from User where phone=:account");

                return null;
            }
        });

        User user =  new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhone(account);//账号就是用户的手机号

        //进行数据库操作
        //首先 创建获得 会话
        Session session =Hib.session();
        //开启一个事务
        session.beginTransaction();
        try {
            //保存一个操作
            session.save(user);
            //提交事务
            session.getTransaction().commit();
        }catch (Exception e){
            //保存事务失败 事务回滚
            session.getTransaction().rollback();
        }

        return user;
    }

    private static String encodePassword(String password){
        //去除头尾空格
        password =password.trim();
        //对密码进行MD5加密
        TextUtil.getMD5(password);
        //对密码进行 base64加密
        return TextUtil.encodeBase64(password);
    }

}
