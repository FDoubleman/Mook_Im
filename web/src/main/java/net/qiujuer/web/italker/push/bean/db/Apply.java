package net.qiujuer.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by fmm on 2017/8/29.
 */
@Entity
@Table(name = "TB_APPLY")
public class Apply {

    public static final int TYPE_ADD_USER = 1; // 添加好友
    public static final int TYPE_ADD_GROUP = 2; // 加入群

    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(nullable = false,updatable = false)
    private String id;//申请id

    // 描述部分，对我们的申请信息做描述
    // eg: 我想加你为好友！！
    @Column(nullable = false)
    private String description;//描述

    // 附件 可为空
    // 可以附带图片地址，或者其他
    @Column(columnDefinition = "TEXT")
    private String attach;//附件

    // 当前申请的类型
    @Column(nullable = false)
    private int type;//申请类型

    // 目标Id 不进行强关联，不建立主外键关系
    // type->TYPE_ADD_USER：User.id
    // type->TYPE_ADD_GROUP：Group.id
    @Column(nullable = false)
    private String targetId;

    // 定义为创建时间戳，在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime creatAt =LocalDateTime.now();
    // 定义为更新时间戳，在创建时就已经写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updataAt = LocalDateTime.now();

    // 申请人 可为空 为系统信息
    // 一个人可以有很多个申请
    @ManyToOne
    @JoinColumn(name = "applicantId")
    private User applicant;//申请人
    @Column(insertable = false,updatable = false)
    private String applicantId;//申请人id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public LocalDateTime getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(LocalDateTime creatAt) {
        this.creatAt = creatAt;
    }

    public LocalDateTime getUpdataAt() {
        return updataAt;
    }

    public void setUpdataAt(LocalDateTime updataAt) {
        this.updataAt = updataAt;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }
}
