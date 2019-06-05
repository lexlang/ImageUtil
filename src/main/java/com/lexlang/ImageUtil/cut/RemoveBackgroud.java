package com.lexlang.ImageUtil.cut;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
/**
 * 二值化
 * @author A
 *
 */
public class RemoveBackgroud {
	/**
	 * 处理相似颜色图形
	 * @param img
	 * @param hold 保留前几大色调，默认出掉第一大色，为背景色，参数不需要把背景色加入
	 * @param flag 是否相反
	 * @return
	 * @throws Exception
	 */
	public static BufferedImage SameColorRemoveBackgroud(BufferedImage img,int hold,boolean flag) throws Exception {
		int width = img.getWidth();
		int height = img.getHeight();
		//把颜色数量放入map
		for(int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				 int r = (img.getRGB(x, y) & 0xff0000) >> 16;
		         int g = (img.getRGB(x, y) & 0xff00) >> 8;
		         int b = (img.getRGB(x, y) & 0xff) ;
		         if(r>hold || g>hold || b>hold){
		        	 img.setRGB(x, y, flag?Color.WHITE.getRGB():Color.BLACK.getRGB());
		         }
		         else{
		        	 img.setRGB(x, y, flag?Color.BLACK.getRGB():Color.WHITE.getRGB());
		         }
			}
		}
		return img;
	}
	
	public static BufferedImage removeDisturb(BufferedImage img){
		int width = img.getWidth();
		int height = img.getHeight();
		int hold=Threshold.returnAllThresHold(img);
		//把颜色数量放入map
		for(int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				 int r = (img.getRGB(x, y) & 0xff0000) >> 16;
		         int g = (img.getRGB(x, y) & 0xff00) >> 8;
		         int b = (img.getRGB(x, y) & 0xff) ;
		         if((r+g+b)>hold*3){
		        	 img.setRGB(x, y, Color.WHITE.getRGB());
		         }
		         else{
		        	 img.setRGB(x, y, Color.BLACK.getRGB());
		         }
			}
		}
		return img;
	}
	
	/**
	 * 处理颜色相同的背景
	 * @param img
	 * @param hold 保留前几大色调，默认出掉第一大色，为背景色，参数不需要把背景色加入
	 * @return
	 * @throws Exception
	 */
	public static BufferedImage MainColorRemoveBackgroud(BufferedImage img,int hold) throws Exception {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int width = img.getWidth();
		int height = img.getHeight();
		//把颜色数量放入map
		for(int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (map.containsKey(img.getRGB(x, y))) {
					map.put(img.getRGB(x, y), map.get(img.getRGB(x, y)) + 1);
				} else {
					map.put(img.getRGB(x, y), 1);
				}
			}
		}
		//挑出前hold颜色
		Map<Integer, Integer> mapColor = new HashMap<Integer, Integer>();
		for(Integer key:map.keySet()){
			if(mapColor.size()<=hold){
				mapColor.put(key, map.get(key));
			}
		}
		for(Integer key:map.keySet()){
			int minKey=10000;
			int colorKey=0;
			for(Integer k:mapColor.keySet()){
				if(minKey>mapColor.get(k)){minKey=mapColor.get(k);
				colorKey=k;
				}
			}
			if(! mapColor.containsKey(key)){
				if(minKey<map.get(key)){
					mapColor.remove(colorKey);
					mapColor.put(key, map.get(key));
				}
			}
		}
		//挑出背景色
		int minKey=0;
		int colorKey=0;
		for(Integer k:mapColor.keySet()){
			if(minKey<mapColor.get(k)){minKey=mapColor.get(k);
			colorKey=k;
			}
		}
		mapColor.remove(colorKey);
		//如果color里面有的颜色则去掉
		for(int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (mapColor.containsKey(img.getRGB(x, y))) {
					img.setRGB(x, y, Color.BLACK.getRGB());
				} else {
					img.setRGB(x, y, Color.WHITE.getRGB());
				}
			}
		}
		return img;
	}
	
	
	public static BufferedImage removeBackgroud(BufferedImage bufferedImage){
		  int h = bufferedImage.getHeight();
          int w = bufferedImage.getWidth();
          // 灰度化
          int[][] gray = new int[w][h];
          for (int x = 0; x < w; x++) {
               for (int y = 0; y < h; y++) {
                   int argb = bufferedImage.getRGB(x, y);
                   int r = (argb >> 16) & 0xFF;
                   int g = (argb >> 8) & 0xFF;
                   int b = (argb >> 0) & 0xFF;
                   int grayPixel = (int) ((b * 29 + g * 150 + r * 77 + 128) >> 8);                
                   gray[x][y] = grayPixel;
	            }
          }
          // 二值化
          int threshold = ostu(gray, w, h);
          BufferedImage binaryBufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
          for (int x = 0; x < w; x++) {
               for (int y = 0; y < h; y++) {
                   if (gray[x][y] > threshold) {
                	      gray[x][y] &= 0xFF0000;
                   } else {
                          gray[x][y] |= 0x00FFFF;
                   }
                         binaryBufferedImage.setRGB(x, y, gray[x][y]);
	               }
	      }
          
		return binaryBufferedImage;
	}
	
	public static int ostu(int[][] gray, int w, int h) {
	         int[] histData = new int[w * h];
	         // Calculate histogram
	         for (int x = 0; x < w; x++) {
	             for (int y = 0; y < h; y++) {
	                 int red = 0xFF & gray[x][y];
	                 histData[red]++;
	             }
	         }
			 // Total number of pixels
			int total = w * h;
			float sum = 0;
			for (int t = 0; t < 256; t++)
			sum += t * histData[t];
			float sumB = 0;
			int wB = 0;
			int wF = 0;
			float varMax = 0;
			int threshold = 0;
			
			for (int t = 0; t < 256; t++) {
				wB += histData[t]; // Weight Background
				if (wB == 0)
				continue;
				wF = total - wB; // Weight Foreground
				if (wF == 0)
				break;
				sumB += (float) (t * histData[t]);
				float mB = sumB / wB; // Mean Background
				float mF = (sum - sumB) / wF; // Mean Foreground
				// Calculate Between Class Variance
				float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);
				// Check if new maximum found
				if (varBetween > varMax) {
				varMax = varBetween;
				threshold = t;
				}
			}
			return threshold;
	}
	
}
