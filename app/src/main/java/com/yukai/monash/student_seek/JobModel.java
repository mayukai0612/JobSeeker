package com.yukai.monash.student_seek;


public class JobModel {


    private String employerid;
    private String jobid;
    private String jobdesc;
    private String company;
    private String companyPicFile;

    public String getEmployerid() {
        return employerid;
    }

    public void setEmployerid(String employerid) {
        this.employerid = employerid;
    }

    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    public String getJobdesc() {
        return jobdesc;
    }

    public void setJobdesc(String jobdesc) {
        this.jobdesc = jobdesc;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyPicFile () {
        return companyPicFile;
    }

    public void setCompanyPicFile(String companyPicFile) {
        this.companyPicFile = companyPicFile;
    }
}
