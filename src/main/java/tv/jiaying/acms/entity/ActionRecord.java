package tv.jiaying.acms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户操作记录
 */
@Entity
public class ActionRecord {

    @Id
    @GeneratedValue
    private long id;

    private String  username;


    public static enum ACTION{

        ITEM_UPDATE("更新节目"),
        ITEM_UPDATE_ONLINE("节目上下架"),

        COLUM_GET("获取栏目"),
        COLUM_ADD("新增栏目"),
        COLUM_UPDATE("更新栏目"),
        COLUM_UPDATE_ONLINE("更新栏目上下架"),
        COLUM_SORT("栏目排序"),
        COLUM_DELETE("删除栏目"),
        COLUM_RELEVANCE("新增关联操作"),
        COLUM_RELEVANCE_DELETE("删除关联操作"),


        PROVIDER_UPDATE("更新提供商"),

        SYSTEM_USER_DELETE("删除系统用户"),
        SYSTEM_USER_UPDATE("更改系统用户"),

        UPLOAD_ADI("上传ADI文件"),

        LOGIN("登陆");


        private String content;

        ACTION(String c){
            this.content = c;
        }

        public String getContent(){
            return this.content;
        }
    }

    private String action;

    public enum LEVEL{
        normalRecord,
        errorRecord
    }

    private String result;

    private LEVEL level;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date date;

    public ActionRecord() {
    }

    public ActionRecord(String username, String action, String result, LEVEL level, Date date) {
        this.username = username;
        this.action = action;
        this.result = result;
        this.level = level;
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LEVEL getLevel() {
        return level;
    }

    public void setLevel(LEVEL level) {
        this.level = level;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
