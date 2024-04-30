package com.ione.commondata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogDTO {

    public static final String PAYMENT_UPDATE_API_NAME  = "PAYMENT_UPDATE";
    public static final String CREDIT_DEBIT_NOTE_CREATION_API_NAME  = "CREDIT_DEBIT_NOTE_CREATION";
    public static final String PDF_UPLOAD_API_NAME  = "PDF_UPLOAD_API";
    private String id;
    private String parentId;
    
    private String apiName;
    private String programName;
    private String content;
    private String description;

//    public void setDescription(String methodName) {
//        this.description = "method name:- " + methodName + " : " + new Date();
//    }
    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }

}
