package com.lhzw.searchlocmap.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "PersonalInfo")
// 数据库表的名字
public class PersonalInfo implements Serializable {
    /*
     * 离线：0 在线：1 sos：2
	 */

    private static final long serialVersionUID = 1L;
    @DatabaseField(generatedId = true)
    private int Id;
    @DatabaseField(columnName = "markerId")
    private int markerId;
    @DatabaseField(columnName = "num")
    private String num;
    @DatabaseField(columnName = "name")
    private String name;
    @DatabaseField(columnName = "age")
    private int age;
    @DatabaseField(columnName = "sex")
    private String sex;
    @DatabaseField(columnName = "phone")
    private String phone;
    @DatabaseField(columnName = "contact1")
    private String contact1;
    @DatabaseField(columnName = "contact2")
    private String contact2;
    @DatabaseField(columnName = "bloodtype")
    private String bloodtype;
    @DatabaseField(columnName = "allergy")
    private String allergy;
    @DatabaseField(columnName = "state")
    private String state;
    @DatabaseField(columnName = "state1")
    private String state1;
    @DatabaseField(columnName = "latitude")
    private String latitude;
    @DatabaseField(columnName = "longitude")
    private String longitude;
    @DatabaseField(columnName = "time")
    private long time;
    @DatabaseField(columnName = "locTime")
    private long locTime;
    @DatabaseField(columnName = "offset")
    private int offset;
    @DatabaseField(columnName = "feedback")
    private int feedback;

    /**
     *
     * @param num
     * @param name
     * @param age
     * @param sex
     * @param phone
     * @param contact1
     * @param contact2
     * @param allergy
     * @param bloodtype
     * @param state
     * @param state1
     * @param latitude
     * @param longitude
     * @param time
     * @param locTime
     */
    public PersonalInfo(String num, String name, int age, String sex, String phone, String contact1,
                        String contact2, String allergy, String bloodtype, String state, String state1,
                        String latitude, String longitude, long time, long locTime) {
        this.num = num;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.phone = phone;
        this.contact1 = contact1;
        this.contact2 = contact2;
        this.allergy = allergy;
        this.bloodtype = bloodtype;
        this.state = state;
        this.state1 = state1;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.locTime = locTime;
    }

    public PersonalInfo(int markerId, String num, String name, int age, String sex, String phone,
                        String contact1, String contact2, String bloodtype, String allergy, String state,
                        String state1, String latitude, String longitude, long time, long locTime, int offset, int feedback) {
        this.markerId = markerId;
        this.num = num;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.phone = phone;
        this.contact1 = contact1;
        this.contact2 = contact2;
        this.bloodtype = bloodtype;
        this.allergy = allergy;
        this.state = state;
        this.state1 = state1;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.locTime = locTime;
        this.offset = offset;
        this.feedback = feedback;
    }

    /**
     *
     */
    public PersonalInfo() {
        super();
        // TODO Auto-generated constructor stub
    }
    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContact1() {
        return contact1;
    }

    public void setContact1(String contact1) {
        this.contact1 = contact1;
    }

    public String getContact2() {
        return contact2;
    }

    public void setContact2(String contact2) {
        this.contact2 = contact2;
    }

    public String getBloodtype() {
        return bloodtype;
    }

    public void setBloodtype(String bloodtype) {
        this.bloodtype = bloodtype;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getState1() {
        return state1;
    }

    public void setState1(String state1) {
        this.state1 = state1;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getLocTime() {
        return locTime;
    }

    public void setLocTime(long locTime) {
        this.locTime = locTime;
    }

    public int getMarkerId() {
        return markerId;
    }
    public void setMarkerId(int markerId) {
        this.markerId = markerId;
    }
    public int getOffset() {
        return offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }
}