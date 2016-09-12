package com.yukai.monash.student_seek;


public class activejobs_model {


    private String employerid;
    private String jobid;
    private String jobdesc;
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

    public String getCompanyPicFile () {
        return companyPicFile;
    }

    public void setCompanyPicFile(String companyPicFile) {
        this.companyPicFile = companyPicFile;
    }
}
