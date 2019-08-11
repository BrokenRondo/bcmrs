package com.tzb.bcmrs.tools;

import java.math.BigInteger;

public class Vec2 {

    public BigInteger x;
    public BigInteger y;

    public Vec2(BigInteger x,BigInteger y)
    {
        this.x = x;
        this.y = y;
    }

    public Vec2(String string)
    {
        String[] strs = string.split(",");
        this.x = new BigInteger(strs[0]);
        this.y = new BigInteger(strs[1]);
    }

}
