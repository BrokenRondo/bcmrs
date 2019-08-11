package com.tzb.bcmrs.message;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ParseTool {
    private static String ReadString(DataInputStream dis, int len) throws IOException
    {
        byte[] data = new byte[len];
        dis.readFully(data);
        return new String(data).trim();
    }

    public static MsgHead parseMsg(byte[] data) throws IOException
    {
        int totalLength = data.length + 4;

        ByteArrayInputStream bins = new ByteArrayInputStream(data);
        DataInputStream dins = new DataInputStream(bins);
        byte msgType = dins.readByte();
        int dest = dins.readInt();
        int src = dins.readInt();

        if (msgType == 0x02)
        {
            String userID = ReadString(dins, 10);
            String docID = ReadString(dins, 10);
            MsgLogin mli = new MsgLogin();
            mli.setTotalLen(totalLength);
            mli.setType(msgType);
            mli.setDest(dest);
            mli.setSrc(src);
            mli.setUserID(userID);
            mli.setDocID(docID);
            return mli;
        }
        else if (msgType == 0x04)
        {
            MsgKey mk = new MsgKey();
            String Key = ReadString(dins, totalLength - 13);
            mk.setTotalLen(totalLength);
            mk.setType(msgType);
            mk.setDest(dest);
            mk.setSrc(src);
            mk.setKey(Key);
            return mk;
        }


        return null;
    }
}
