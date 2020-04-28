package com.lhzw.searchlocmap.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "LocPersonalInfo")
// 本地录入信息  已绑定的表数据库
public class LocPersonalInfo implements Serializable {
    /*
     * 离线：0 在线：1 sos：2
     */

    private static final long serialVersionUID = 1L;
    @DatabaseField(generatedId = true)
    private int _Id;
    @DatabaseField(columnName = "num")
    private String num;//固话注册码
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
    @DatabaseField(columnName = "offset")
    private int offset;

    /**
     * @param num
     * @param name
     * @param age
     * @param sex
     * @param phone
     * @param contact1
     * @param contact2
     * @param bloodtype
     * @param allergy
     * @param state
     */
    /**
     *
     */

    public LocPersonalInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public LocPersonalInfo(String num, String name, int age, String sex,
                           String phone, String contact1, String contact2, String bloodtype,
                           String allergy, String state) {
        super();
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

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}