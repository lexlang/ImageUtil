package com.lexlang.ImageUtil.cut;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lexlang.ImageUtil.CommonUtil;



public class SplitPic {
	private static int whiteThreshold = 600;
	
	/**
	 * 水平方向切割
	 * @param img
	 * @return
	 * @throws Exception
	 */
	public static List<BufferedImage> vSplitImage(BufferedImage img) throws Exception {
		final List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		final int width = img.getWidth();
		final int height = img.getHeight();
		final List<Integer> weightlist = new ArrayList<Integer>();
		for (int x = 0; x <  height; ++x) {
			int count = 0;
			for (int y = 0; y < width; ++y) {
				if (CommonUtil.isWhite(img.getRGB(y,x), whiteThreshold) == 0) {
					count++;
				}
			}
			weightlist.add(count);
		}
		for (int i = 0; i < weightlist.size(); i++) {
			int length = 0;
			while (i < weightlist.size() && weightlist.get(i) > 0) {
				i++;
				length++;
			}
			if (length > 2) {
				subImgs.add(img.getSubimage(0, i - length-1, width, length+1));
			}
		}
		return subImgs;
	}
	
	/**
	 * 垂直方向切割
	 * @param img
	 * @return
	 * @throws Exception
	 */
	public static List<BufferedImage> hSplitImage(BufferedImage img) throws Exception {
		final List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		final int width = img.getWidth();
		final int height = img.getHeight();
		final List<Integer> weightlist = new ArrayList<Integer>();
		for (int x = 0; x < width; ++x) {
			int count = 0;
			for (int y = 0; y < height; ++y) {
				if (CommonUtil.isWhite(img.getRGB(x, y), whiteThreshold) == 0) {
					count++;
				}
			}
			weightlist.add(count);
		}
		for (int i = 0; i < weightlist.size(); i++) {
			int length = 0;
			while (i < weightlist.size() && weightlist.get(i) > 0) {
				i++;
				length++;
			}
			if (length > 1) {
				subImgs.add(CommonUtil.removeBlank(img.getSubimage(i - length, 0, length, height), whiteThreshold, 0));
			}
		}
		return subImgs;
	}
	
	public static JSONArray splitHanZi(BufferedImage img){
		JSONArray result=new JSONArray();
		List<String> vList=getVlocal(img);
		for(int index=0;index<vList.size();index++){
			String[] vArr=vList.get(index).split("_");
			BufferedImage currentImg = img.getSubimage(0, Integer.parseInt(vArr[0]), img.getWidth(),  Integer.parseInt(vArr[1]));
			result.add(checkBoxOrHsplit(currentImg));
		}
		return result;
	}
	
	private static JSONArray checkBoxOrHsplit(BufferedImage img){
		JSONArray result=new JSONArray();
		if(checkBox(img)){
			JSONArray store=splitBox(img);
			for(int index=0;index<store.size();index++){
				JSONArray items = store.getJSONArray(index);
				JSONArray tempResult=new JSONArray();
				for(int ind=0;ind<items.size();ind++){
					tempResult.add(splitHanZi((BufferedImage) items.get(ind)));
				}
				result.add(tempResult);
			}
		}else{
			result.add(img);
/*			List<String> hList=getHlocal(img);
			for(int index=0;index<hList.size();index++){
				String[] hArr=hList.get(index).split("_");
				BufferedImage currentImg = img.getSubimage(Integer.parseInt(hArr[0]), 0
						, Integer.parseInt(hArr[1])+1, img.getHeight());
				result.add(currentImg);
			}*/
		}
		return result;
	}
	
	public static JSONArray splitBox(BufferedImage img){
		//img 移除白框
		BulkStatistics bulk=new BulkStatistics();
		//最小矩阵法
		//找到起止点
		int width = img.getWidth();
		int height = img.getHeight();
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				if(! bulk.checkInBox(i, j) && CommonUtil.isBlack(img.getRGB(i, j))){
					if(startEndPoint(img,i,j)){
						String[] arr=getEndStartPoint(img,i,j).split("_");
						bulk.addImage(i, j, Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), img);
					}
				}
			}
		}

		return bulk.getStore();
	}
	
	/**
	 * 
	 * @param img
	 * @param x 上顶点坐标
	 * @param y
	 * @return
	 */
	private static String getEndStartPoint(BufferedImage img,int x,int y){
		int offY=y+1;
		//找到 向下的坐标
		while(! (CommonUtil.isBlack(img.getRGB(x+1, offY)) && CommonUtil.isBlack(img.getRGB(x+2, offY)))){
			offY+=1;
		}
		int offX=x+1;
		//找到向上的坐标
		while(! (CommonUtil.isBlack(img.getRGB(offX, offY-1)) && CommonUtil.isBlack(img.getRGB(offX, offY-2)))){
			offX+=1;
		}
		return offX+"_"+offY;
	}
	
	/**
	 * 上面顶角
	 * @param img
	 * @param x
	 * @param y
	 * @return
	 */
	private static boolean startEndPoint(BufferedImage img,int x,int y){
		try{
			if(CommonUtil.isBlack(img.getRGB(x, y)) && CommonUtil.isBlack(img.getRGB(x+1, y)) && CommonUtil.isBlack(img.getRGB(x+2, y))
					&& CommonUtil.isBlack(img.getRGB(x, y+1)) && CommonUtil.isBlack(img.getRGB(x, y+2))
					&& CommonUtil.isBlack(img.getRGB(x, y+1)) && ! CommonUtil.isBlack(img.getRGB(x+1, y+1))
					&& ! CommonUtil.isBlack(img.getRGB(x+2, y+1)) && ! CommonUtil.isBlack(img.getRGB(x+1, y+2))){
				return true;
			}else{
				return false;
			}
		}catch(Exception ex){}
		return false;
	}
	
	
	/**
	 * 水平线,占据屏幕二分之一
	 * @param img
	 * @return
	 */
	private static boolean checkBox(BufferedImage img){
		int width = img.getWidth();
		int total=0;//黑点数量
		for(int index=0;index<img.getWidth();index++){
			if(CommonUtil.isBlack(img.getRGB(index, 0))){
				total++;
			}
		}
		if((total*1.0)/width>0.5)
			return true;
		return false;
	}
	

	public static Map<String,BufferedImage> getVHsplit(BufferedImage img,int startX,int startY,int hold){
		List<String> vList=getVlocal(img);
		List<String> hList=getHlocal(img);
		Map<String,BufferedImage> result=new HashMap<String,BufferedImage>();
		if(vList.size()>1 || hList.size()>1){
			for(int i=0;i<vList.size();i++){
				for(int j=0;j<hList.size();j++){
					String[] vArr=vList.get(i).split("_");
					String[] hArr=hList.get(j).split("_");
					if( Integer.parseInt(hArr[1])<hold || Integer.parseInt(vArr[1])<hold){
						continue;
					}
					result.putAll(getVHsplit(img.getSubimage(Integer.parseInt(hArr[0]), Integer.parseInt(vArr[0])
							, Integer.parseInt(hArr[1]), Integer.parseInt(vArr[1])),Integer.parseInt(hArr[0])+startX,Integer.parseInt(vArr[0])+startY,hold));
				}
			}
		}else{
			for(int i=0;i<vList.size();i++){
				for(int j=0;j<hList.size();j++){
					String[] vArr=vList.get(i).split("_");
					String[] hArr=hList.get(j).split("_");
					if( Integer.parseInt(hArr[1])<hold || Integer.parseInt(vArr[1])<hold){
						continue;
					}
					result.put((Integer.parseInt(hArr[0])+startX+Integer.parseInt(hArr[1])/2)
							+"_"+(Integer.parseInt(vArr[0])+startY+Integer.parseInt(vArr[1])/2)
							, img.getSubimage(Integer.parseInt(hArr[0]), Integer.parseInt(vArr[0])
									, Integer.parseInt(hArr[1]), Integer.parseInt(vArr[1])));
				}
			}
			
		}
		return result;
	}
	
	private static List<String> getVlocal(BufferedImage img){
		final List<String> subImgs = new ArrayList<String>();
		final int width = img.getWidth();
		final int height = img.getHeight();
		final List<Integer> weightlist = new ArrayList<Integer>();
		for (int x = 0; x <  height; ++x) {
			int count = 0;
			for (int y = 0; y < width; ++y) {
				if (CommonUtil.isWhite(img.getRGB(y,x), whiteThreshold) == 0) {
					count++;
				}
			}
			weightlist.add(count);
		}
		for (int i = 0; i < weightlist.size(); i++) {
			int length = 0;
			while (i < weightlist.size() && weightlist.get(i) > 0) {
				i++;
				length++;
			}
			if (length > 2) {
				subImgs.add((i-length) +"_"+(length-1));
			}
		}
		return subImgs;
	}
	
	private static List<String> getHlocal(BufferedImage img){
		final List<String> subImgs = new ArrayList<String>();
		final int width = img.getWidth();
		final int height = img.getHeight();
		final List<Integer> weightlist = new ArrayList<Integer>();
		for (int x = 0; x < width; ++x) {
			int count = 0;
			for (int y = 0; y < height; ++y) {
				if (CommonUtil.isWhite(img.getRGB(x, y), whiteThreshold) == 0) {
					count++;
				}
			}
			weightlist.add(count);
		}
		for (int i = 0; i < weightlist.size(); i++) {
			int length = 0;
			while (i < weightlist.size() && weightlist.get(i) > 0) {
				i++;
				length++;
			}
			if (length > 1) {
				subImgs.add((i - length)+"_"+(length-1));
			}
		}
		return subImgs;
	}
	
}
