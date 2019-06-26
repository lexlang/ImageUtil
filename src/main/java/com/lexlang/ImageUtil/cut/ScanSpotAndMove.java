package com.lexlang.ImageUtil.cut;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.imageio.ImageIO;

/**
* @author lexlang
* @version 2019年6月26日 下午5:03:53
* 
*/
public class ScanSpotAndMove {
	private HashSet<String> duplicateSpot=new HashSet<String>();
	
	/**
	 * 
	 * @param img
	 * @param hold 连接一起黑块小于此数量,则值白
	 * @return
	 */
	public  BufferedImage scanSpotAndMove(BufferedImage img,int hold){
		int width = img.getWidth();
		int height = img.getHeight();
		//把颜色数量放入map
		for(int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				 int r = (img.getRGB(x, y) & 0xff0000) >> 16;
		         int g = (img.getRGB(x, y) & 0xff00) >> 8;
		         int b = (img.getRGB(x, y) & 0xff) ;
		         if(r+g+b<50){
		        	 duplicateSpot.clear(); 
		        	 if(statisticsSpot(img,x,y)<hold){
		        		 img.setRGB(x, y, Color.WHITE.getRGB());
		        	 }
		         }
			}
		}
		return img;
	}
	
	private Integer statisticsSpot(BufferedImage img,int xOff,int yOff){
		int width = img.getWidth();
		int height = img.getHeight();
		int r = (img.getRGB(xOff, yOff) & 0xff0000) >> 16;
        int g = (img.getRGB(xOff, yOff) & 0xff00) >> 8;
        int b = (img.getRGB(xOff, yOff) & 0xff) ;
        int coun=0;
        if(r+g+b<50){
        	coun++;
        	if(yOff+1<height && ! duplicateSpot.contains(xOff+":"+(yOff+1))){
        		duplicateSpot.add(xOff+":"+(yOff+1));
        		coun=coun+statisticsSpot(img,xOff,yOff+1);
        	}
        	if(yOff-1>0 && ! duplicateSpot.contains(xOff+":"+(yOff-1))){
        		duplicateSpot.add(xOff+":"+(yOff-1));
        		coun=coun+statisticsSpot(img,xOff,yOff-1);
        	}
        	if(xOff+1<width && ! duplicateSpot.contains((xOff+1)+":"+yOff)){
        		duplicateSpot.add((xOff+1)+":"+yOff);
        		coun=coun+statisticsSpot(img,xOff+1,yOff);
        	}
        }
        return coun;
	}
	
}
