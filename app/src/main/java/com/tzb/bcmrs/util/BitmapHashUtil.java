package com.tzb.bcmrs.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapHashUtil {
    public String getHash(Bitmap bmp){
        /**
         * 第一步，缩小尺寸。
         * 最快速的去除高频和细节，只保留结构明暗的方法就是缩小尺寸。
         * 将图片缩小到8x8的尺寸，总共64个像素。摒弃不同尺寸、比例带来的图片差异
         */
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int newWidth = 8;
        int newHeight = 8;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix,true);
        /**
         * 第二步，简化色彩。
         * 将缩小后的图片，转为64级灰度。也就是说，所有像素点总共只有64种颜色。
         */
        int width1 = newbmp.getWidth();   //获取位图的宽
        int height1 = newbmp.getHeight();  //获取位图的高

        int []pixels = new int[width1 * height1]; //通过位图的大小创建像素点数组

        newbmp.getPixels(pixels, 0, width1, 0, 0, width1, height1);
        int alpha = 0xFF << 24;
        for(int i = 0; i < height1; i++) {
            for(int j = 0; j < width1; j++) {
                int grey = pixels[width1 * i + j];

                int red = ((grey  & 0x00FF0000 ) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int)((float) red * 0.3 + (float)green * 0.59 + (float)blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width1 * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width1, height1, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, width1, 0, 0, width1, height1);

        /**
         * 第三步，计算平均值。
         * 计算所有64个像素的灰度平均值。
         */
        int sum = 0;
        for(int i=0;i<result.getWidth();i++){
            for(int j=0;j<result.getHeight();j++){
                int x = result.getPixel(i, j);
                sum+=x;
            }
        }
        int ave = sum/(result.getWidth()*result.getHeight());


        /**
         * 第四步，比较像素的灰度。
         * 算法的精髓，简单、有趣，又充满深意。
         * 将每个像素的灰度，与平均值进行比较。大于或等于平均值，记为1；小于平均值，记为0。
         */
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<result.getWidth();i++){
            for(int j=0;j<result.getHeight();j++){
                int x = result.getPixel(i, j);
                if(x>ave){
                    sb.append("1");
                }else{
                    sb.append("0");
                }
            }
        }
        /**
         * 第五步，计算哈希值。
         * 将上一步的比较结果，组合在一起成为一个字符串，这就是这张图片的指纹。组合的次序并不重要，
         * 只要保证所有图片都采用同样次序就行了（例如，自左到右、自顶向下、big-endian）。
         */



        return sb.toString();

    }
}
