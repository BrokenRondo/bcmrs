package com.tzb.bcmrs.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MsgLogin extends MsgHead {
    private String userID;
    private String docID;

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getUserID() {
        return userID;
    }

    public String getDocID() {
        return docID;
    }

    @Override
    public byte[] packMessage() throws IOException {
        ByteArrayOutputStream bous=new ByteArrayOutputStream();
        DataOutputStream dous=new DataOutputStream(bous);
        packMessageHead(dous);
        writeString(dous,10,getUserID());
        writeString(dous,10,getDocID());
        dous.flush();
        return bous.toByteArray();
    }
}
