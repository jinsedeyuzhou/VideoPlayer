package com.ebrightmoon.aplayer.bean;

/**
 * Created by Berkeley on 10/28/16.
 */
public class LiveBean {
    /**
     * 城市
     */
    private String city;
    /**
     * ID
     */
    private String id;
    /**
     * 用户名称
     */
    private String name;
    /**
     * 分享url
     */
    private String share_addr;
    /**
     * StreamUrl
     */
    private String stream_addr;

    /**
     * room_id
     * @return
     */
    private long room_id;


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShare_addr() {
        return share_addr;
    }

    public void setShare_addr(String share_addr) {
        this.share_addr = share_addr;
    }

    public String getStream_addr() {
        return stream_addr;
    }

    public void setStream_addr(String stream_addr) {
        this.stream_addr = stream_addr;
    }
}
