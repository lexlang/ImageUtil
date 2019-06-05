package com.lexlang.ImageUtil;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.jhlabs.image.ScaleFilter;
/**
 * 扩展训练集
 * @author A
 *
 */
public class ExtendUtil {
	/**
	 * 
	 * @param img 原始图片
	 * @param up  竖向
	 * @param right	横向方向
	 * @return
	 */
	private static BufferedImage moveOnePoint(BufferedImage img,int up,int right){
		BufferedImage newImg=new BufferedImage(img.getWidth()*4,img.getHeight()*4,img.getType());
		Graphics2D g2=newImg.createGraphics();
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, img.getWidth()*4,img.getHeight()*4);
		g2.translate(img.getWidth()*2+up,img.getHeight()*2+right);
		g2.drawImage(img, null, null);
		return newImg.getSubimage(img.getWidth()*2, img.getHeight()*2, img.getWidth(), img.getHeight());
	}
	
	/**
	 * 
	 * @param img
	 * @param angle
	 * @return
	 */
	private static BufferedImage getAnglePic(BufferedImage img,double angle){
		BufferedImage newImg=new BufferedImage(img.getWidth()*4,img.getHeight()*4,img.getType());
		Graphics2D g2=newImg.createGraphics();
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, img.getWidth()*4,img.getHeight()*4);
		g2.translate(img.getWidth()*2,img.getHeight()*2);
		g2.rotate(angle*2*Math.PI/360);
		g2.drawImage(img, null, null);
		return newImg.getSubimage(img.getWidth()*2, img.getHeight()*2, img.getWidth(), img.getHeight());
	}
	
	/**
	 * 拓展训练集
	 * @param path
	 * @throws Exception
	 */
	public static void makeTrainSample(String path) throws Exception {
		if(! path.endsWith("\\")){
			path=path+"\\";
		}
		final File dir = new File(path);
		final File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith("png");
			}
		});
		HashMap<String,BufferedImage> hs=new HashMap<String,BufferedImage>();
		for (final File file : files) {
			final BufferedImage img = ImageIO.read(file);
			String name=file.getName().toLowerCase().replace(path, "").replace(".jpg", "").replace(".png", "");
			System.out.println(name);
			//hs.put(name+"up1"+"right0", moveOnePoint(img,4,0));
			//hs.put(name+"up-1"+"right0", moveOnePoint(img,-4,0));
			//hs.put(name+"up0"+"right1", moveOnePoint(img,0,4));
			//hs.put(name+"up0"+"right-1", moveOnePoint(img,0,-4));
			hs.put(name+"angle1", getAnglePic(img,5));
			//hs.put(name+"angle-1", getAnglePic(img,-5));
		}
		for(String key:hs.keySet()){
			ImageIO.write(hs.get(key), "png",new File(path+key+".png"));
		}
	}
	
	public static void main(String[] args) throws Exception{
		makeTrainSample("C:\\Users\\A\\Pictures\\app\\chinaTaxRecog");
	}
	
	
}
