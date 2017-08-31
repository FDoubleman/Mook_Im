package net.qiujuer.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by fmm on 2017/8/25.
 *
 */
@Entity
@Table(name ="TB_USER_FOLLOW")
public class UserFollow {


    // 这是一个主键
    @Id
    @PrimaryKeyJoinColumn
    // 主键生成存储的类型为UUID，自动生成UUID
    @GeneratedValue(generator = "uuid")
    // 把uuid的生成器定义为uuid2，uuid2是常规的UUID toString
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    // 不允许更改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id; //关系id


    // 定义一个发起人，你关注某人，这里就是你
    // 多对1 -> 你可以关注很多人，你的每一次关注都是一条记录
    // 你可以创建很多个关注的信息，所有是多对1；
    // 这里的多对一是：User 对应 多个UserFollow
    // optional 不可选，必须存储，一条关注记录一定要有一个"你"
    @ManyToOne(optional = false)
    // 定义关联的表字段名为originId，对应的是User.id
    // 定义的是数据库中的存储字段
    @JoinColumn(name = "originId")
    private User origin;//关注者

    // 把这个列提取到我们的Model中，不允许为null，不允许更新，插入
    @Column(updatable = false,nullable = false,insertable = false)
    private String originId;//关注者ID

    // 定义关注的目标，你关注的人
    // 也是多对1，你可以被很多人关注，每次一关注都是一条记录
    // 所有就是 多个UserFollow 对应 一个 User 的情况
    @ManyToOne(optional = false)
    // 定义关联的表字段名为targetId，对应的是User.id
    // 定义的是数据库中的存储字段
    @JoinColumn(name = "targetId")
    private User target;//被关注者

    @Column(updatable = false,nullable = false,insertable = false)
    private String targetId;//被关注者 ID


    // 别名，也就是对target的备注名, 可以为null
    @Column
    private String alias;//别名

    // 定义为创建时间戳，在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime creatAt= LocalDateTime.now();// 创建时间

    // 定义为更新时间戳，在创建时就已经写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt =LocalDateTime.now();//更新时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOrigin() {
        return origin;
    }

    public void setOrigin(User origin) {
        this.origin = origin;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public LocalDateTime getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(LocalDateTime creatAt) {
        this.creatAt = creatAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}
