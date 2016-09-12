package com.yukai.monash.student_seek;


public class Applicants_model {

    /**
     * userid : 0
     * email : test@mail.com
     * password : test
     * firstname : test
     * lastname : test
     */

    private String userid;
    private String email;
    private String password;
    private String firstname;
    private String lastname;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
