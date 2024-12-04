package top.sacz.timtool.net.entity;

import androidx.annotation.NonNull;

import com.alibaba.fastjson2.JSON;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private String uin;

    private String nickname;

    private Integer identity;

    private String identityName;

    private String label;

    private Date createTime;

    private Date updateTime;

    /**
     * QQ
     */
    public String getUin() {
        return uin;
    }

    /**
     * QQ
     */
    public void setUin(String uin) {
        this.uin = uin;
    }

    /**
     * 昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 身份
     */
    public Integer getIdentity() {
        return identity;
    }

    /**
     * 身份
     */
    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    /**
     * 身份名
     */
    public String getIdentityName() {
        return identityName;
    }

    /**
     * 身份名
     */
    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    /**
     * 标签
     */
    public String getLabel() {
        return label;
    }

    /**
     * 标签
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @NonNull
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}