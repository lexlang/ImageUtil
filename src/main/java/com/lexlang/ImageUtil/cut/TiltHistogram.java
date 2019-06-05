package com.lexlang.ImageUtil.cut;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.lexlang.ImageUtil.CommonUtil;


public class TiltHistogram {
	private static int whiteThreshold = 100;
	/**
	 * 倾斜直方图，切割倾斜的验证码
	 * 原点为左上角
	 * @param img 导入图片
	 * @param diff 倾斜斜线上下距离差 正值为上面比下面短，负值为上面比上面短
	 * @return
	 * @throws Exception 
	 */
	public static List<BufferedImage> tiltHistogram(BufferedImage img,int diff) throws Exception{
		final List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		
		final int width = img.getWidth();
		final int height = img.getHeight();
		//多长进一格
		int step=height/diff;
		int[] widthY=new int[width];
		for(int i=0;i<height;i++){
			int tempY=i/step;
			widthY[i]=tempY;
		}
		//构造直方图
		final List<Integer> weightlist = new ArrayList<Integer>();
		for (int x = 0; x < width; ++x) {
			int count = 0;
			for (int y = 0; y < height; ++y) {
				try{
				if (CommonUtil.isWhite(img.getRGB(x+widthY[y], y), whiteThreshold) == 0) {
					count++;
				}}catch(Exception e){}//忽略点溢出的情况
			}
			weightlist.add(count);
		}
		//倾斜切割直方图
		for (int i = 0; i < weightlist.size(); i++) {
			int length = 0;
			while (i < weightlist.size() && weightlist.get(i) > 0) {
				i++;
				length++;
			}
			if (length > 2) {
				subImgs.add(CommonUtil.removeBlank(getTiltImage(img,i - length,i, widthY), whiteThreshold, 0));
			}
		}
		return subImgs;
	}
	
	/**
	 * 测试倾斜的格数 测试范围从-45度到正45度
	 * @param img
	 * @param hold 检测间隙的格数
	 * @return
	 */
	public static int testDiff(BufferedImage img,int hold){
		final int width = img.getWidth();
		final int height = img.getHeight();
		int tilt=-height;
		
		for(tilt=-height;tilt<=height;tilt++){
			int jianXi=0;
			int diff=tilt;
			boolean flag=false;//遇到从有到无，则为真
			//多长进一格
			int step=height/diff;
			int[] widthY=new int[width];
			for(int i=0;i<height;i++){
				int tempY=i/step;
				widthY[i]=tempY;
			}
			//构造直方图
			final List<Integer> weightlist = new ArrayList<Integer>();
			for (int x = 0; x < width; ++x) {
				int count = 0;
				for (int y = 0; y < height; ++y) {
					try{
					if (CommonUtil.isWhite(img.getRGB(x+widthY[y], y), whiteThreshold) == 0) {
						count++;
					}}catch(Exception e){}//忽略点溢出的情况
				}
				weightlist.add(count);
			}
			//检测有几个间隙
			for(int i=1;i<weightlist.size()-1;i++){
				if(weightlist.get(i)>0 && weightlist.get(i+1)==0){
					flag=true;
				}
				if(weightlist.get(i)==0 && weightlist.get(i+1)>0){
					jianXi++;
					flag=false;
				}
			}
			if(jianXi>=hold){
				return tilt;
			}
		}
		
		return tilt;
	}
	
	/**
	 * 
	 * @param img 
	 * @param startW 切割起始位置
 	 * @param finishW 结尾位置
	 * @param widthY X的位移的数组
	 * @return
	 */
	public static BufferedImage getTiltImage(BufferedImage img,int startW,int finishW,int[] widthY){
		BufferedImage buffer=new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
		buffer.getGraphics().drawImage(img, 0, 0, img.getWidth(),img.getHeight(), null);
		final int width = img.getWidth();
		final int height = img.getHeight();
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				try{
					if(x<startW+widthY[y] || x>finishW+widthY[y]){
						buffer.setRGB(x, y, Color.WHITE.getRGB());
					}
				}catch(Exception e){}//忽略点溢出的情况
			}
		}
		return buffer;
	}
	
}
