package com.dscnitp.freshersportal.Model;

public class ModelQuestionPaper {
    private String FileName;
    private String FileUrl;

    public ModelQuestionPaper(String fileName, String fileUrl) {
        FileName = fileName;
        FileUrl = fileUrl;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }
}
