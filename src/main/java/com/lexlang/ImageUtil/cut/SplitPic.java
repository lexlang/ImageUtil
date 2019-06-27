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

import com.lexlang.ImageUtil.CommonUtil;



public class SplitPic {
	private static int whiteThreshold = 200;
	
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
