package net.qiujuer.web.italker.push.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Created by qiujuer
 * on 2017/2/17.
 */
public class Hib {
    // 全局SessionFactory
    private static SessionFactory sessionFactory;

    static {
        // 静态初始化sessionFactory
        init();
    }

    private static void init() {
        // 从hibernate.cfg.xml文件初始化
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            // build 一个sessionFactory
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            // 错误则打印输出，并销毁
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    /**
     * 获取全局的SessionFactory
     *
     * @return SessionFactory
     */
    public static SessionFactory sessionFactory() {
        return sessionFactory;
    }

    /**
     * 从SessionFactory中得到一个Session会话
     *
     * @return Session
     */
    public static Session session() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 关闭sessionFactory
     */
    public static void closeFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public interface QueryOnlyInter{
        void querOnly(Session session);
    }

    public static void queryOnly(QueryOnlyInter queryOnlyInter){
        //开启事务
        Session session = sessionFactory.openSession();
        final Transaction transaction = session.beginTransaction();
        try {
            queryOnlyInter.querOnly(session);
            //提交事务
            transaction.commit();
        }catch (Exception e){
            e.printStackTrace();
            //提交失败 回滚
            try {
                //回滚失败
                transaction.rollback();
            }catch (RuntimeException e1){
                e1.printStackTrace();
            }

        }finally {
            //异常一定关闭 会话
            session.close();
        }
    }

    public interface QueryInter<T>{
        T query(Session session);
    }

    // 简化Session操作的工具方法，
    // 具有一个返回值
    public static <T> T query(QueryInter<T> queryInter){
        // 重开一个Session
       Session session = sessionFactory.openSession();
       //开启事务
        final Transaction transaction = session.beginTransaction();
        T t =null;
        try {
            // 调用传递进来的接口，
            // 并调用接口的方法把Session传递进去
            t = queryInter.query(session);
            // 提交
            transaction.commit();
        }catch (Exception e){
            e.printStackTrace();
            // 回滚
            try{
                transaction.rollback();
            }catch (RuntimeException e1){
                e1.printStackTrace();
            }
        }finally {
            // 无论成功失败，都需要关闭Session
            session.close();
        }
        return t;
    }

}
