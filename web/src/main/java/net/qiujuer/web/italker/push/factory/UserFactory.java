package net.qiujuer.web.italker.push.factory;

import com.google.common.base.Strings;
import net.qiujuer.web.italker.push.bean.db.User;
import net.qiujuer.web.italker.push.utils.Hib;
import net.qiujuer.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

import javax.ws.rs.PathParam;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Created by fmm on 2017/8/30.
 */
public class UserFactory {

    //通过phone 找到User
    public static User findUserByPhone(String phone){
       return Hib.query(new Hib.QueryInter<User>() {
            @Override
            public User query(Session session) {

                return (User) session.createQuery("from User where phone=:inPhone")
                        .setParameter("inPhone",phone)
                        .uniqueResult();

            }
        });
    }

    //通过name 找到User
    public static User findUserByName(String name){
        return Hib.query(new Hib.QueryInter<User>() {
            @Override
            public User query(Session session) {
                return (User) session.createQuery("from User where name =:name")
                        .setParameter("name",name)
                        .uniqueResult();
            }
        });
    }

    //通过token 找到User
    public static User findUserByToken(String token){
        return Hib.query(new Hib.QueryInter<User>() {
            @Override
            public User query(Session session) {
                return (User) session.createQuery("from User where token=:token")
                        .setParameter("token",token)
                        .uniqueResult();
            }
        });
    }


    public static User login(String account,String password){
        final String accountStr = account.trim();
        // 把原文进行同样的处理，然后才能匹配
        final String encodePassword = encodePassword(password);

        //查找匹配用户
         User user = Hib.query(new Hib.QueryInter<User>() {
            @Override
            public User query(Session session) {
                return (User) session.createQuery("from User where phone =:phone and password =:password")
                        .setParameter("phone",accountStr)
                        .setParameter("password",encodePassword)
                        .uniqueResult();
            }
        });
        if(user!=null){
            // 对User进行登录操作，更新Token
            user = login(user);
        }
        return user;
    }


    /**
     * 注册用户
     * 注册的操作需要写入数据库，并返回数据库中的User信息
     * @param account 账户
     * @param password 密码
     * @param name 名称
     * @return user
     */
    public static User register(String account,String password,String name){
        //1、对account
        account =account.trim();
        //2、密码加密
        password =encodePassword(password);
        //3、保存到数据库
        User user =  creatUser(account,password,name);
        if(user!=null){
            user=  login(user);
        }
        return user;
    }


    /**
     * 把一个User进行登录操作
     * 本质上是对Token进行操作
     * @param user
     * @return
     */
    public static User login(User user){
        //1、生成 token
        String token = UUID.randomUUID().toString();
        token = TextUtil.encodeBase64(token);
        //2、更新用户token
        user.setToken(token);
        //3、更新数据库 中的token
        return updataUser(user);

    }

    public static User bindPushId(User user, String pushId){
        //1、check 检查参数
        if(Strings.isNullOrEmpty(pushId)){
            return null;
        }
        Hib.queryOnly(new Hib.QueryOnlyInter() {
            @SuppressWarnings("unchecked")
            @Override
            public void querOnly(Session session) {
                //2、检查该设备是否 被其他用户(除自己外)绑定
                List<User> users = session.createQuery("from User where lower(pushId)=:pushId and id !=:userId")
                        .setParameter("pushId",pushId)
                        .setParameter("userId",user.getId())
                        .list();
                //2.1 绑定过 取消
                for (User u : users) {
                    u.setPushId(null);
                    session.saveOrUpdate(u);
                }
            }
        });
        //3、检查该用户绑定的 pushID 和传入的是否相同
        //3.1 、相同不更新数据库 pushid
        if(pushId.equalsIgnoreCase(user.getPushId())){
            // 如果当前需要绑定的设备Id，之前已经绑定过了
            // 那么不需要额外绑定
            return user;
        }else{
            //3.2 、不相同 设置pushId

            // 如果当前账户之前的设备Id，和需要绑定的不同
            // 那么需要单点登录，让之前的设备退出账户，
            // 给之前的设备推送一条退出消息
            if (Strings.isNullOrEmpty(user.getPushId())) {
                // TODO 推送一个退出消息
            }
            user.setPushId(pushId);
           // 更新数据库User
            return updataUser(user);
        }
    }



    /**
     * 注册账户时 创建并保存 至数据库
     * @param account 账户
     * @param password 密码
     * @param name 名称
     * @return 保存数据库成功的 用户
     */
    private static User creatUser(String account,String password,String name){
        User user =  new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhone(account);//账号就是用户的手机号

        return Hib.query(new Hib.QueryInter<User>() {
            @Override
            public User query(Session session) {
                session.save(user);

                return user;
            }
        });
    }

    /**
     * 对密码进行加密操作
     *
     * @param password 原文
     * @return 密文
     */
    private static String encodePassword(String password) {
        // 密码去除首位空格
        password = password.trim();
        // 进行MD5非对称加密，加盐会更安全，盐也需要存储
        password = TextUtil.getMD5(password);
        // 再进行一次对称的Base64加密，当然可以采取加盐的方案
        return TextUtil.encodeBase64(password);
    }

    /**
     * 更新用户
     * @param user user
     * @return user
     */
    public static User updataUser(User user){

      return   Hib.query(new Hib.QueryInter<User>() {
            @Override
            public User query(Session session) {
                 session.saveOrUpdate(user);
                return user;
            }
        });
    }
}
