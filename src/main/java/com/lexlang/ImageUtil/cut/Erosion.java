package com.lexlang.ImageUtil.cut;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.lexlang.ImageUtil.CommonUtil;

public class Erosion {
	
	/**
	 * 腐蚀算法
	 * @param img
	 * @return
	 */
	public static BufferedImage erosion(BufferedImage img){
		BufferedImage copy = CommonUtil.copyBufferedImage(img);
		for(int i=1;i<img.getWidth()-1;i++){
			for(int j=1;j<img.getHeight()-1;j++){
				if(checkBlack(copy.getRGB(i, j))){
					int coun=0;
					if(checkBlack(copy.getRGB(i, j+1))){
						coun++;
					}
					if(checkBlack(copy.getRGB(i, j-1))){
						coun++;
					}
					if(coun<=0){
						img.setRGB(i, j, Color.WHITE.getRGB());
					}
				}
			}
		}
		return img;
	}
	
	public static boolean checkBlack(int rgb){
		 int tr = (rgb & 0xff0000) >> 16;
         int tg = (rgb & 0xff00) >> 8;
         int tb = (rgb & 0xff) ;
         if(tr<150 || tg<150 || tb<150){
        	 return true;
         }else{
        	 return false;
         }
	}
	
	/**
	 * 缩放图片大小
	 * @param img
	 * @return
	 */
	public static BufferedImage zoomImg(BufferedImage img){
		
		int width = img.getWidth();
		int height = img.getHeight();
		List<Integer> weightlist = new ArrayList<Integer>();
		for (int y = 0; y < width; ++y) {
			int count = 0;
			for (int x = 0; x <  height; ++x) {
				if (CommonUtil.isWhite(img.getRGB(y,x), 400) == 0) {
					count++;
				}
			}
			weightlist.add(count);
		}
		
		int zoomWeight=0;
		for (int i = 0; i < weightlist.size(); i++) {
			if(weightlist.get(i)>3){
				zoomWeight++;
			}
		}
		
		System.out.println(zoomWeight);
		
		BufferedImage bimage = new BufferedImage(zoomWeight, img.getHeight(), img.getType());
		zoomWeight=0;
	
		for (int y = 0; y < width; ++y) {
			if (weightlist.get(y)>3) {
				for (int x = 0; x <  height; ++x) {
						bimage.setRGB(zoomWeight, x, img.getRGB(y, x));	
				}
				zoomWeight++;
			}
		}
		
		return bimage;
	}
}
