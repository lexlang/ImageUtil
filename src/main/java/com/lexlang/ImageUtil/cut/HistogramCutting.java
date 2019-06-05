package com.lexlang.ImageUtil.cut;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.lexlang.ImageUtil.CommonUtil;

/**
 * 切割图片
 * @author A
 *
 */
public class HistogramCutting {
	/**
	 * 水平方向切割成数组
	 * @param img         二值化的的图片
	 * @param hold        少于这个点时,不记录统计
	 * @param fontSize    字体的宽度，超过时，则切断
	 * @return
	 * @throws Exception
	 */
	public static List<BufferedImage> splitImage(BufferedImage img,int hold,int fontSize) throws Exception {
		final List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		int whiteThreshold = 50;
		final int width = img.getWidth();
		final int height = img.getHeight();
		//获得左右宽度权重
	    List<Integer> weightlist = new ArrayList<Integer>();
		for (int x = 0; x < width; ++x) {
			int count = 0;
			for (int y = 0; y < height; ++y) {
				if (CommonUtil.isWhite(img.getRGB(x, y), whiteThreshold) == 0) {
					count++;
				}
			}
			weightlist.add(count);
		}
		//权重超过hold时，记录位置
		ArrayList<Integer> local=new ArrayList<Integer>();
		for(int i=0;i<weightlist.size();i++){
			if(weightlist.get(i)>hold){
				local.add(i);
			}
		}
		//记录切割坐标的起止位置
		Map<Integer,Integer> hs=new LinkedHashMap<Integer,Integer>();
		int firstLocal=-1;
		for(int i=0;i<local.size();i++){
			if(firstLocal==-1){
				firstLocal=local.get(i);
			}
			else{
				if(local.get(i)-firstLocal>fontSize){
					int lastLocal=local.get(i-1);
					if(lastLocal-firstLocal==fontSize){
						hs.put(firstLocal, lastLocal);
					}
					else{
						int kuoSize=fontSize-(lastLocal-firstLocal);
						for(int j=0;j<kuoSize;j++){
							if(firstLocal==0){
								lastLocal=lastLocal+1;
								continue;
							}
							if(lastLocal==width-1){
								firstLocal=firstLocal+1;
								continue;
							}
							if(j%2==0){
								firstLocal=firstLocal+1;
							}else
							{
								lastLocal=lastLocal+1;
							}
						}
						hs.put(firstLocal, lastLocal);
					}
					firstLocal=-1;
				}
			}
		}
		for(Integer key:hs.keySet()){
			subImgs.add(img.getSubimage(key-1, 0, fontSize, height));
		}
		
		return subImgs;
	}
	
	/**
	 * 水平方向切割成数组
	 * @param img         二值化的的图片
	 * @param hold        少于这个点时,不记录统计
	 * @param fontSize    字体的宽度，超过时，则切断
	 * @return
	 * @throws Exception
	 */
	public static BufferedImage removeBackground(BufferedImage img,int hold,int fontSize) throws Exception {
		int whiteThreshold = 50;
		final int width = img.getWidth();
		final int height = img.getHeight();
		//获得上下宽度权重
	    List<Integer> weightlist = new ArrayList<Integer>();
		for (int y = 0; y < height; ++y) {
			int count = 0;
			for (int x = 0; x < width; ++x) {
				if (CommonUtil.isWhite(img.getRGB(x, y), whiteThreshold) == 0) {
					count++;
				}
			}
			weightlist.add(count);
		}
		//权重超过hold时，记录位置
		HashMap<Integer,Integer> local=new HashMap<Integer,Integer>();
		for(int i=0;i<weightlist.size();i++){
			if(weightlist.get(i)>hold){
				local.put(i,hold);
			}
		}
		//设置起止位置 记录
		int start=0;
		int weightMax=0;
		for(int i=0;i<height-fontSize;i++){
			int Coun=0;
			for(int j=0;j<=fontSize;j++){
				if(local.containsKey(i+j)){
					Coun++;
				}
			}
			if(weightMax<=Coun){
				start=i;
				weightMax=Coun;
			}
		}
		
		return img.getSubimage(0, start, width, fontSize);
	}
	
	
	public static void getHistogramDetail(BufferedImage img){
		int whiteThreshold = 50;
		final int width = img.getWidth();
		final int height = img.getHeight();
		//获得左右宽度权重
	    List<Integer> weightlist = new ArrayList<Integer>();
		for (int x = 0; x < width; ++x) {
			int count = 0;
			for (int y = 0; y < height; ++y) {
				if (CommonUtil.isWhite(img.getRGB(x, y), whiteThreshold) == 0) {
					count++;
				}
			}
			weightlist.add(count);
		}
		for(int i=0;i<weightlist.size();i++){
			System.out.println(weightlist.get(i));
		}
	}
	
}
