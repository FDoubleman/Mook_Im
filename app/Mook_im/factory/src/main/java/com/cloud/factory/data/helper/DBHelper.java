package com.cloud.factory.data.helper;

import com.cloud.factory.model.db.AppDatabase;
import com.cloud.factory.model.db.User;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.Arrays;
import java.util.List;

/**
 * Created by fmm on 2017/12/20.
 * 数据库操作类
 */

public class DBHelper {

    public static DBHelper instance;

    public static DBHelper getInstance() {
        if (instance == null) {
            instance = new DBHelper();
        }
        return instance;
    }

    /**
     * 保存
     */

    public void save(final List<User> users) {
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<User> adapter = FlowManager.getModelAdapter(User.class);
                adapter.saveAll(users);

            }
        }).build().execute();
    }


    /**
     * 新增或者修改的统一方法
     *
     * @param tClass  传递一个Class信息
     * @param models  这个Class对应的实例的数组
     * @param <Model> 这个实例的范型，限定条件是BaseModel
     */
    public static <Model extends BaseModel> void save(final Class<Model> tClass, final Model... models) {
        if (models == null || models.length == 0) {
            return;
        }
        // 当前数据库的一个管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        // 提交一个事物
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //执行
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                //保存
                adapter.saveAll(Arrays.asList(models));
                //通知保存
                instance.notifySave(tClass,models);
            }
        }).build().execute();
    }

    /**
     * 进行删除数据库的统一封装方法
     *
     * @param tClass  传递一个Class信息
     * @param models  这个Class对应的实例的数组
     * @param <Model> 这个实例的范型，限定条件是BaseModel
     */
    public static <Model extends BaseModel> void delete(final Class<Model> tClass, final Model... models) {
        if (models == null || models.length == 0) {
            return;
        }
        //获得数据库管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        //提交一个事务
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //执行
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                //删除
                adapter.deleteAll(Arrays.asList(models));
                // 唤起通知
                instance.notifyDelete(tClass,models);
            }
        }).build().execute();
    }

    /**
     * 进行通知调用
     *
     * @param tClass  通知的类型
     * @param models  通知的Model数组
     * @param <Model> 这个实例的范型，限定条件是BaseModel
     */
    private static <Model extends BaseModel> void notifySave(final Class<Model> tClass, final Model... models) {

    }

    /**
     * 进行通知调用
     *
     * @param tClass  通知的类型
     * @param models  通知的Model数组
     * @param <Model> 这个实例的范型，限定条件是BaseModel
     */
    private static<Model extends BaseModel> void notifyDelete(final Class<Model> tClass,final Model... models){

    }

}
