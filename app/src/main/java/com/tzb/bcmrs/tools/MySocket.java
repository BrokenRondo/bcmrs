package com.tzb.bcmrs.tools;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static java.lang.Thread.sleep;

public class MySocket {

    public static final String HOST = "192.168.43.82";

    public static String request(String host,int port,String request)
    {
        String info;
        try
        {
            Socket socket = new Socket(host,port);
            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();
            PrintStream ps = new PrintStream(output);
            ps.println(request);
            BufferedReader bf = new BufferedReader(new InputStreamReader(input));
            InputStream ins = socket.getInputStream();
            DataInputStream dis = new DataInputStream(ins);

            int len=dis.readInt();
            byte[] buf=new byte[len];
            dis.readFully(buf);

            info = new String(buf,"GBK").trim();
            //gbk转utf-8
            //String gbk= URLEncoder.encode(info,"UTF-8");
           // String utf8= URLDecoder.decode(info,"UTF-8");
           // String clientStr = new String(info.getBytes("GB2312"), "GB2312");
            //info=utf8;
            output.close();
            input.close();
        }
        catch (IOException e)
        {
            info = null;
        }

        return info;
    }

    public static void sendBmp(String host, int port, Bitmap bmp)
    {
        try {
            Socket s=new Socket(host,port);

            File file = File.createTempFile("qrccode","jpg");
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                bos.flush();
                bos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }


            FileInputStream files=new FileInputStream(file);

            OutputStream outs=s.getOutputStream();

            byte[] buf=new byte[1024];

            int len=0;

            while((len=files.read(buf))!=-1)
            {
                outs.write(buf,0,len);
            }

            s.shutdownOutput();

            outs.close();
            files.close();
            s.close();

            file.delete();
        }catch(IOException e){

        }

    }

}
