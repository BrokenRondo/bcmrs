package com.tzb.bcmrs.util;

import java.util.Vector;

class String2Integer{
    private Vector<String>Integer_Array;
    private Vector<String>Char_Array;
    private String newIntegerStr;//
    public String2Integer(){
        Integer_Array=new Vector<String>();
        Char_Array=new Vector<String>();
        int Int_src=10;
        char char_src='A';
        for (int i=0;i<26;i++){
            //对A~Z
            Integer_Array.add(String.valueOf(Int_src));
            Char_Array.add(String.valueOf(char_src));
            Int_src++;
            char_src++;
        }
        char_src='a';
        for (int i=0;i<26;i++){
            //对a~z
            Integer_Array.add(String.valueOf(Int_src));
            Char_Array.add(String.valueOf(char_src));
            Int_src++;
            char_src++;
        }
        char_src='0';
        for (int i=0;i<10;i++){
            //对a~z
            Integer_Array.add(String.valueOf(Int_src));
            Char_Array.add(String.valueOf(char_src));
            Int_src++;
            char_src++;
        }
        char_src='+';
        Integer_Array.add(String.valueOf( Int_src));
        Char_Array.add(String.valueOf( char_src));
        char_src='/';
        Int_src++;
        Integer_Array.add(String.valueOf(Int_src));
        Char_Array.add(String.valueOf(char_src));
        char_src='=';
        Int_src++;
        Integer_Array.add(String.valueOf(Int_src));
        Char_Array.add(String.valueOf(char_src));
    }

    /*给出一个Base64字符串，将字符串映射到大整数*/
    public  String String2Integer_str(String src){
        newIntegerStr="";//先置空，防止多次映射时不正确
        for (int i=0;i<src.length();i++){
            //对每个Base64字符，将其转成数字
            char character=src.charAt(i);
            //找到这个字符后，定位它在转换表的哪一个，然后给出index
            int charIndex=Char_Array.indexOf(String.valueOf( character));
            String char2int=Integer_Array.get(charIndex);
            newIntegerStr+=char2int;
        }
        return newIntegerStr;
    }
    public String Integer2Base64(String Integer) {
        int end = Integer.length();
        int start = 0;
        String result = "";
        try {
            while (start < end) {
                String t = Integer.substring(start, start + 2);
                int index = Integer_Array.indexOf(t);
                result += Char_Array.get(index);
                start += 2;
            }
            return result;
        }catch (ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
    }
}
public class Base64_2_IntegerUtil {
    public String StringtoInteger(String src){
        String2Integer string2Integer=new String2Integer();
        String result=string2Integer.String2Integer_str(src);
        return result;
    }
    public String Integer2Base64(String src){
        String2Integer string2Integer=new String2Integer();
        String result=string2Integer.Integer2Base64(src);
        return result;
    }

}
