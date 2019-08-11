package com.tzb.bcmrs.tools;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Random;

public class MyCrypto {

    public static BigInteger modValue = new BigInteger("12768377909919851828946146344720145724929016863406097300773382257744003936124739623026242138782964258619677459227494342472695861349901094925023388262899331");

    /**
     * 对字符串md5加密
     *
     * @param str 传入要加密的字符串
     * @return  MD5加密后的大整形
     */
    public static BigInteger MD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
           // MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            //md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1,str.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Vec2[] splitKey(int requestleast,int NumberDivided,BigInteger key) {

        //病人作为可信中心选取多项式并将秘密份额分发给六个用户，其中自己占三个，任意三个份额可以获得密钥
        //为了兼容增加用户的操作，要求可以选择安全联系人，例如可以选择自己的亲属，将秘密份额分给他们
        BigInteger[]ai=new BigInteger[10];
        BigInteger randomPrime=modValue;
        int indexCompare[]=new int[10];
        BigInteger resumeDataPieces[]=new BigInteger[10];
        BigInteger resumeKeyPieces[]=new BigInteger[10];
        //假设要分成t份秘密份额，则要选取t-1次多项式

        ai[0]=key;//共享的密钥
        for(int i=1;i<requestleast;i++) {
            ai[i]=new BigInteger(10,new Random()).mod(randomPrime);
        }
        for(int i=0;i<requestleast;i++) {
            System.out.println("ai["+i+"]="+ai[i]);
        }
        System.out.println("\n");
        //在此时相当于多项式已经选出来，接下来要计算n份秘密
        BigInteger[]secretX=new BigInteger[10];//as the x vector of the secret
        BigInteger[]secretY=new BigInteger[10];//the same

        //first secretY should be initialize or there'll be nullpointexception
        for(int i=0;i<NumberDivided;i++) {
            secretY[i]=new BigInteger(0,new Random());
        }
        //then should choose some Random points on the polynomial and calculate Y corresponding to X chosen
        for(int i=0;i<NumberDivided;i++) {
            BigInteger temp=new BigInteger(10,new Random());//choose random x coordinate
            //x can alse be the ID of users

            secretX[i]=temp.mod(randomPrime);
            for(int j=1;j<requestleast;j++) {
                secretY[i]=secretY[i].add(ai[j].multiply(temp.pow(j)));//calculate Y corresponding
            }
            //after calculating secretY above ,ai[0]should be added as the sharing key
            secretY[i]=secretY[i].add(ai[0]);
            //然后还要模
            secretY[i]=secretY[i].mod(randomPrime);
        }



        Vec2[] v= new Vec2[NumberDivided];
        for(int i=0;i<NumberDivided;i++) {
            v[i] = new Vec2(secretX[i],secretY[i]);
        }



        for(int i=0;i<requestleast;i++) {
            indexCompare[i]=0;
        }
        for(int i=0;i<requestleast;i++) {
            Random ran1=new Random();
            int index=ran1.nextInt(NumberDivided);
            indexCompare[i]=index;
            for(int j=0;j<i;j++) {
                if(index==indexCompare[j])
                    i--;//避免出现一样的X
            }
        }
        for(int i=0;i<requestleast;++i) {
            int index=indexCompare[i];
            resumeDataPieces[i]=secretX[index];
            resumeKeyPieces[i]=secretY[index];//t个能用的秘密份额
        }

        return v;
    }
    public static Vec2[] splitKey_static(int requestleast,int NumberDivided,BigInteger key) {

        //病人作为可信中心选取多项式并将秘密份额分发给六个用户，其中自己占三个，任意三个份额可以获得密钥
        //为了兼容增加用户的操作，要求可以选择安全联系人，例如可以选择自己的亲属，将秘密份额分给他们
        BigInteger[]ai=new BigInteger[10];
        BigInteger randomPrime=modValue;
        int indexCompare[]=new int[10];
        BigInteger resumeDataPieces[]=new BigInteger[10];
        BigInteger resumeKeyPieces[]=new BigInteger[10];
        //假设要分成t份秘密份额，则要选取t-1次多项式

        ai[0]=key;//共享的密钥
        for(int i=1;i<requestleast;i++) {
            ai[i]=new BigInteger(10,new Random()).mod(randomPrime);
        }
        for(int i=0;i<requestleast;i++) {
            System.out.println("ai["+i+"]="+ai[i]);
        }
        System.out.println("\n");
        //在此时相当于多项式已经选出来，接下来要计算n份秘密
        BigInteger[]secretX=new BigInteger[10];//as the x vector of the secret
        BigInteger[]secretY=new BigInteger[10];//the same

        //first secretY should be initialize or there'll be nullpointexception
        for(int i=0;i<NumberDivided;i++) {
            secretY[i]=new BigInteger(0,new Random());
        }
        //then should choose some Random points on the polynomial and calculate Y corresponding to X chosen
        for(int i=0;i<NumberDivided;i++) {
            BigInteger temp=new BigInteger(10,new Random());//choose random x coordinate
            //x can alse be the ID of users

            secretX[i]=temp.mod(randomPrime);
            for(int j=1;j<requestleast;j++) {
                secretY[i]=secretY[i].add(ai[j].multiply(temp.pow(j)));//calculate Y corresponding
            }
            //after calculating secretY above ,ai[0]should be added as the sharing key
            secretY[i]=secretY[i].add(ai[0]);
            //然后还要模
            secretY[i]=secretY[i].mod(randomPrime);
        }



        Vec2[] v= new Vec2[NumberDivided];
        for(int i=0;i<NumberDivided;i++) {
            v[i] = new Vec2(secretX[i],secretY[i]);
        }



        for(int i=0;i<requestleast;i++) {
            indexCompare[i]=0;
        }
        for(int i=0;i<requestleast;i++) {
            Random ran1=new Random();
            int index=ran1.nextInt(NumberDivided);
            indexCompare[i]=index;
            for(int j=0;j<i;j++) {
                if(index==indexCompare[j])
                    i--;//避免出现一样的X
            }
        }
        for(int i=0;i<requestleast;++i) {
            int index=indexCompare[i];
            resumeDataPieces[i]=secretX[index];
            resumeKeyPieces[i]=secretY[index];//t个能用的秘密份额
        }

        return v;
    }
    public static BigInteger recoverKey(ArrayList<Vec2> vec2s,int requestLeast) {

        BigInteger prime = modValue;
        BigInteger []resumeKeyPieces = new BigInteger[vec2s.size()];
        BigInteger []resumeDatapieces= new BigInteger[vec2s.size()];
        for(int i = 0;i<vec2s.size();i++)
        {
            resumeKeyPieces[i] = vec2s.get(i).x;
            resumeDatapieces[i] = vec2s.get(i).y;
        }

        //在调用之前需要把t份秘密放入x和y数组中
        //因为拉格朗日插值只需要算出幂次为0的项的系数，所以可以忽略掉其他项，只算0次，不需要重构多项式
        //需要知道足够的秘密份额。
        //if(secNum<3)return -1;
        BigInteger iResult=new BigInteger(0,new Random());
        BigInteger iTotalX=new BigInteger(0,new Random());
        BigInteger iMinusX=new BigInteger(0,new Random());
        iTotalX=BigInteger.valueOf(1);
        iResult=BigInteger.valueOf(0);
        for(int i=0;i<requestLeast;i++) {
            iTotalX=iTotalX.multiply(resumeKeyPieces[i]);
            iTotalX=iTotalX.mod(prime);
        }
        for(int i=0;i<requestLeast;i++) {
            BigInteger iTotalMinusX=new BigInteger(1,new Random());
            iTotalMinusX=BigInteger.valueOf(1);
            for(int j=0;j<requestLeast;j++) {
                if(i!=j) {
                    iMinusX=resumeKeyPieces[j].subtract(resumeKeyPieces[i]);
                    if(resumeKeyPieces[i].compareTo(resumeKeyPieces[j])<0) {
                        iMinusX=prime.add(iMinusX);//不能为负数，如果是负数就要加上模
                    }
                    iTotalMinusX=(iTotalMinusX.multiply(iMinusX)).mod(prime);
                }			}
            iTotalMinusX=(iTotalMinusX.multiply(resumeKeyPieces[i]));//在分母上再乘以resumeKeyPieces[i]，以抵消iTotalX多乘的一次
            iTotalMinusX=iTotalMinusX.modInverse(prime);
            iResult=iResult.add(((resumeDatapieces[i].multiply(iTotalX).mod(prime)).multiply(iTotalMinusX)).mod(prime));
            iResult=iResult.mod(prime);
        }
        return iResult;
    }

}
