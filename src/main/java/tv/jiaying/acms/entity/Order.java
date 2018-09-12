package tv.jiaying.acms.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "order_record")
public class Order {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "order_no")
    private String orderNo; //订单号，由系统产生

    @Column(name = "order_type")
    private int orderType; //订单类型，产品包0 或者 单片1

    @Column(name = "pay_type")
    private int payType; //支付方式

    @Column(name = "pay_amount")
    private String payAmount; //支付金额，缴费通知接口返回

    @Column(name = "pay_channel_type")
    private int payChannelType; //支付方式，缴费通知接口返回

    @Column(name = "tran_date")
    private Date tranDate; //支付交易时间，缴费通知接口返回

    @Column(name = "bill_key")
    private String billKey; //缴费号码，缴费通知接口返回

    private int status; //缴费状态 3表示支付成功

    @Column(name = "pay_order_no")
    private String payOrderNo; //支付订单号

    @Column(name = "payment_Item_name")
    private String paymentItemName; //缴费项目名称

    @Column(name = "assetid")
    private String assetID; //单片的assetId

    @Column(name = "providerid")
    private String providerID; //单片的providerId

    @Column(name = "ppvid")
    private String ppvID; //商品的ppvid

    @Column(name = "dataid")
    private long dataID; //内容ID itemId

    @Column(name = "cardid")
    private String cardID; //智能卡号

    private String title; //商品名称

    private Double pirce;//商品价格

    @Column(name = "order_date")
    private Date orderDate;  //订单时间

    @Column(name = "expire_date")
    private Date expireDate;//订单过期时间

    @Column(name = "modify_date")
    private Date modifyDate; //订单修改时间

    private int duration;//包月时长

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public int getPayChannelType() {
        return payChannelType;
    }

    public void setPayChannelType(int payChannelType) {
        this.payChannelType = payChannelType;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public String getBillKey() {
        return billKey;
    }

    public void setBillKey(String billKey) {
        this.billKey = billKey;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPayOrderNo() {
        return payOrderNo;
    }

    public void setPayOrderNo(String payOrderNo) {
        this.payOrderNo = payOrderNo;
    }

    public String getPaymentItemName() {
        return paymentItemName;
    }

    public void setPaymentItemName(String paymentItemName) {
        this.paymentItemName = paymentItemName;
    }

    public String getAssetID() {
        return assetID;
    }

    public void setAssetID(String assetID) {
        this.assetID = assetID;
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public String getPpvID() {
        return ppvID;
    }

    public void setPpvID(String ppvID) {
        this.ppvID = ppvID;
    }

    public long getDataID() {
        return dataID;
    }

    public void setDataID(long dataID) {
        this.dataID = dataID;
    }

    public String getCardID() {
        return cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPirce() {
        return pirce;
    }

    public void setPirce(Double pirce) {
        this.pirce = pirce;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }
}
