package com.dscnitp.freshersportal.Model;

public class ModelGroupChatList {
    String grpId;

    public ModelGroupChatList() {
    }

    String grptitle;

    public ModelGroupChatList(String grpId, String grptitle, String grpdesc, String grpicon, String timestamp, String createdBy) {
        this.grpId = grpId;
        this.grptitle = grptitle;
        this.grpdesc = grpdesc;
        this.grpicon = grpicon;
        this.timestamp = timestamp;
        this.createdBy = createdBy;
    }

    String grpdesc;
    String grpicon;

    public String getGrpId() {
        return grpId;
    }

    public void setGrpId(String grpId) {
        this.grpId = grpId;
    }

    public String getGrptitle() {
        return grptitle;
    }

    public void setGrptitle(String grptitle) {
        this.grptitle = grptitle;
    }

    public String getGrpdesc() {
        return grpdesc;
    }

    public void setGrpdesc(String grpdesc) {
        this.grpdesc = grpdesc;
    }

    public String getGrpicon() {
        return grpicon;
    }

    public void setGrpicon(String grpicon) {
        this.grpicon = grpicon;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    String timestamp;
    String createdBy;
}
