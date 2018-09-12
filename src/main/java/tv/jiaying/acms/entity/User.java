package tv.jiaying.acms.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 应用用户信息
 */
@Entity
public class User {
    @Id
    @GeneratedValue
    private long id;

    private String smartCardId; //智能卡号

    private String deviceNumber; //机顶盒号

    public enum Status {
        normal,  //正常
        down //停机
    }

    private Status status; //用户状态

    private String region; //用户所在地区

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSmartCardId() {
        return smartCardId;
    }

    public void setSmartCardId(String smartCardId) {
        this.smartCardId = smartCardId;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
