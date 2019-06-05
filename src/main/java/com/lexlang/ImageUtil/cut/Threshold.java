package com.lexlang.ImageUtil.cut;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Threshold {
	/**
	 * 返回全局阈值
	 * @param img
	 * @return
	 */
	public static int returnAllThresHold(BufferedImage img){
		final int width = img.getWidth();
		final int height = img.getHeight();
		int[] imgRgb=new int[width*height];
		for(int y=0;y<height;y++){
				for(int x=0;x<width;x++){
					imgRgb[y*width+x]=img.getRGB(x, y);
			 }
		}
		return returnThreshold(imgRgb);
	}
	
	/**
	 * 返回局部阈值
	 * @param imgRgb
	 * @return
	 */
	public static int returnThreshold(int[] imgRgb){
		return GetOSTUThreshold(getGrayScaleHistogram(imgRgb));
	}
	/**
	 * 返回局部阈值
	 * @param imgRgb
	 * @return
	 */
	public static int returnThreshold(ArrayList<Integer> imgRgb){
		int[] arr=new int[imgRgb.size()];
		for(int i=0;i<imgRgb.size();i++){arr[i]=imgRgb.get(i);}
		return GetOSTUThreshold(getGrayScaleHistogram(arr));
	}
	
	
	/**
	 * 提取256个灰度图
	 * @param img
	 * @return
	 * @throws IOException 
	 */
	public static int[] getGrayScaleHistogram(int[] imgRgb){
		int[] hs=new int[256];
		for(int i=0;i<256;i++){
			hs[i]=0;
		}
		for(int i=0;i<imgRgb.length;i++){
			int gray= returnGray(imgRgb[i]); 
			hs[gray]=hs[gray]+1;
		}
		return hs;
	}
	public static int returnGray(int rgb){
		 int tr = (rgb & 0xff0000) >> 16;
         int tg = (rgb & 0xff00) >> 8;
         int tb = (rgb & 0xff) ;
         int gray= (int)(0.299 *tr + 0.587*tg + 0.114*tb); 
         return gray;
	}
	
	/**
	 * 迭代最有阈值
	 * @param HistGram
	 * @return
	 */
    public static int GetOSTUThreshold(int[] HistGram)
    {	

        int X, Y, Amount = 0;
        int PixelBack = 0, PixelFore = 0, PixelIntegralBack = 0, PixelIntegralFore = 0, PixelIntegral = 0;
        double OmegaBack, OmegaFore, MicroBack, MicroFore, SigmaB, Sigma;              // 类间方差;
        int MinValue, MaxValue;
        int Threshold = 0;

        for (MinValue = 0; MinValue < 256 && HistGram[MinValue] == 0; MinValue++) ;
        for (MaxValue = 255; MaxValue > MinValue && HistGram[MinValue] == 0; MaxValue--) ;
        if (MaxValue == MinValue) return MaxValue;          // 图像中只有一个颜色             
        if (MinValue + 1 == MaxValue) return MinValue;      // 图像中只有二个颜色

        for (Y = MinValue; Y <= MaxValue; Y++) Amount += HistGram[Y];        //  像素总数

        PixelIntegral = 0;
        for (Y = MinValue; Y <= MaxValue; Y++) PixelIntegral += HistGram[Y] * Y;
        SigmaB = -1;
        for (Y = MinValue; Y < MaxValue; Y++)
        {
            PixelBack = PixelBack + HistGram[Y];
            PixelFore = Amount - PixelBack;
            OmegaBack = (double)PixelBack / Amount;
            OmegaFore = (double)PixelFore / Amount;
            PixelIntegralBack += HistGram[Y] * Y;
            PixelIntegralFore = PixelIntegral - PixelIntegralBack;
            MicroBack = (double)PixelIntegralBack / PixelBack;
            MicroFore = (double)PixelIntegralFore / PixelFore;
            Sigma = OmegaBack * OmegaFore * (MicroBack - MicroFore) * (MicroBack - MicroFore);
            if (Sigma > SigmaB)
            {
                SigmaB = Sigma;
                Threshold = Y;
            }
        }
        return Threshold;
    }
}
