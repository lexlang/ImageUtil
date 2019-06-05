package com.lexlang.ImageUtil.cut;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.lexlang.ImageUtil.CommonUtil;

public class WeightCut {
	
	/**
	 * 按照比重最大切割图片，竖向的
	 * @param BufferedImage	切割的宽度
	 * @param xWidth	切割的宽度
	 * @param yHeight	切割的高度，暂缓无用
	 * @param coun 取几个字
	 * @param minPoint 最少几个的数量
	 * @return
	 */
	public static List<BufferedImage> getWeightCut(BufferedImage img,int xWidth,int yHeight,int coun ,int minPoint){
		List<BufferedImage> list=new ArrayList<BufferedImage>();
		//统计直方图
		final int width = img.getWidth();
		final int height = img.getHeight();
		final List<Integer> weightlist = new ArrayList<Integer>();
		for (int x = 0; x < width; ++x) {
			int count = 0;
			for (int y = 0; y < height; ++y) {
				if (CommonUtil.isWhite(img.getRGB(x, y), 50) == 0) {
					count++;
				}
			}
			weightlist.add(count);
		}
		//依次获得最开始的点
		LinkedHashMap<Integer,Integer> hs=new LinkedHashMap<Integer,Integer>();
		for(int i=0;i<weightlist.size()-xWidth;i++){
			int count=0;
			for( int j=i;j<i+xWidth;j++){
				count+=weightlist.get(j);
			}
			hs.put(i, count);
		}
		//局部最优解释
		int kuan=xWidth/4;
		List<Integer> weiZhi=new ArrayList<Integer>();
		int startW=0;
		for(int i=0;i<coun;i++){
			startW=getMaxPoint(hs,startW,minPoint,kuan)+xWidth;
			weiZhi.add(startW-xWidth);
		}
		for(int i=0;i<weiZhi.size();i++){list.add(getHeightCut(img.getSubimage(weiZhi.get(i), 0, xWidth, height),yHeight,minPoint));}
		return list;
	}
	
	public static BufferedImage getHeightCut(BufferedImage img,int yHeight,int minPoint){
		//统计直方图
		final int width = img.getWidth();
		final int height = img.getHeight();
		final List<Integer> weightlist = new ArrayList<Integer>();
		for (int y = 0; y < height; ++y) {
			int count = 0;
			for (int x = 0; x < width; ++x) {
				if (CommonUtil.isWhite(img.getRGB(x, y), 50) == 0) {
					count++;
				}
			}
			weightlist.add(count);
		}
		//依次获得最开始的点
		LinkedHashMap<Integer,Integer> hs=new LinkedHashMap<Integer,Integer>();
		for(int i=0;i<weightlist.size()-yHeight;i++){
			int count=0;
			for( int j=i;j<i+yHeight;j++){
				count+=weightlist.get(j);
			}
			hs.put(i, count);
		}
		int startW=getMaxPoint(hs,0,minPoint,yHeight/4);
		return img.getSubimage(0, startW, width, yHeight);
	}
	
	
	/**
	 * 
	 * @param hs 对应后面的集合
	 * @param startW 开始选择的位置
	 * @param weiZhi 如果比这个值大，时间对应的宽度
	 * @return
	 */
	private static Integer getMaxPoint(HashMap<Integer, Integer> hs,int startW,int minPoint,int kuan) {
		// TODO Auto-generated method stub
		for(int i=startW;i<hs.size();i++){
			if(hs.get(i)>minPoint){
				int moveOff=0;
				int moveOff1=0;
				if(i-kuan<startW){moveOff=startW;}else{moveOff=i-kuan;}
				if(hs.size()-i-1-kuan<0){moveOff1=hs.size();}else{moveOff1=i+kuan;}
				boolean flag=true;
				for(int j=moveOff;j<moveOff1;j++){
					if(hs.get(i)<hs.get(j)){flag=false;}
				}
				if(flag){return i;}
			}
		}
		return 0;
	}

}
