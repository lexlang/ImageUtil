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
import java.util.Random;
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
	 * @throws Exception 
	 */
	public static BufferedImage moveOnePoint(BufferedImage img,int up,int right) throws Exception{
		BufferedImage newImg=new BufferedImage(img.getWidth()*4,img.getHeight()*4,img.getType());
		Graphics2D g2=newImg.createGraphics();
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, img.getWidth()*4,img.getHeight()*4);
		g2.translate(img.getWidth()*2+up,img.getHeight()*2+right);
		g2.drawImage(img, null, null);
		return CommonUtil.removeBlank(newImg ,230*3, 0); //newImg.getSubimage(img.getWidth()*2, img.getHeight()*2, img.getWidth(), img.getHeight());
	}
		
	/**
	 * 
	 * @param img
	 * @param angle
	 * @return
	 * @throws Exception 
	 */
	public static BufferedImage getAnglePic(BufferedImage img,double angle) throws Exception{
		BufferedImage newImg=new BufferedImage(img.getWidth()*4,img.getHeight()*4,img.getType());
		Graphics2D g2=newImg.createGraphics();
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, img.getWidth()*4,img.getHeight()*4);
		g2.translate(img.getWidth()*2,img.getHeight()*2);
		g2.rotate(angle*2*Math.PI/360);
		g2.drawImage(img, null, null);
		return CommonUtil.removeBlank(newImg ,230*3, 0); //newImg.getSubimage(img.getWidth()*2, img.getHeight()*2, img.getWidth(), img.getHeight());
	}
	
	public static BufferedImage getSheer(BufferedImage img) throws Exception{
		BufferedImage newImg=new BufferedImage(img.getWidth()*4,img.getHeight()*4,img.getType());
		Graphics2D g2=newImg.createGraphics();
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, img.getWidth()*4,img.getHeight()*4);
		g2.translate(img.getWidth()*2,img.getHeight()*2);
		switch(rand.nextInt(9)){
			case 0:
				g2.shear(0.1, 0);
				break;
			case 1:
				g2.shear(0.2, 0);
				break;
			case 3:
				g2.shear(-0.1, 0);
				break;
			case 4:
				g2.shear(-0.2, 0);
				break;
			case 5:
				g2.shear(0,0.1);
				break;
			case 6:
				g2.shear(0,0.2);
				break;
			case 7:
				g2.shear(0,-0.1);
				break;
			default:
				g2.shear(0,-0.2);
		}
		g2.drawImage(img, null, null);
		return CommonUtil.removeBlank(newImg ,230*3, 0);
	}
	
	
	private static Random rand=new Random();
	
	public static BufferedImage switchColorImage(BufferedImage img){
		int width = img.getWidth();
		int height = img.getHeight();
		
		int mark=rand.nextInt(4);
		
		for(int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				 int r = (img.getRGB(x, y) & 0xff0000) >> 16;
		         int g = (img.getRGB(x, y) & 0xff00) >> 8;
		         int b = (img.getRGB(x, y) & 0xff) ;
		         if(mark==0){
		        	 img.setRGB(x, y, (255 << 24) | (g << 16) | (b <<8 )| r);
		         }else if(mark==1){
		        	 img.setRGB(x, y, (255 << 24) | (b << 16) | (r <<8 )| g);
		         }else if(mark==2){
		        	 img.setRGB(x, y, (255 << 24) | (b << 16) | (g <<8 )| r);
		         }else if(mark==3){
		        	 img.setRGB(x, y, (255 << 24) | (r << 16) | (b <<8 )| g);
		         }
			}
		}
		return img;
	}
	
	
	public static BufferedImage diffColorImage(BufferedImage img){
		int width = img.getWidth();
		int height = img.getHeight();
		
		for(int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				 int r = (img.getRGB(x, y) & 0xff0000) >> 16;
		         int g = (img.getRGB(x, y) & 0xff00) >> 8;
		         int b = (img.getRGB(x, y) & 0xff) ;
		         if(r>100 && r<200){
		        	 r=rand.nextBoolean()?(5+rand.nextInt(5)+r):(-5-rand.nextInt(5)+r);
		         }
		         if(g>100 && g<200){
		        	 g=rand.nextBoolean()?(5+rand.nextInt(5)+g):(-5-rand.nextInt(5)+g);
		         }
		         if(b>100 && b<200){
		        	 b=rand.nextBoolean()?(5+rand.nextInt(5)+b):(-5-rand.nextInt(5)+b);
		         }
		         img.setRGB(x, y, (255 << 24) | (r << 16) | (g <<8 )| b);
			}
		}
		return img;
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
			hs.put(name+"up1"+"right0", moveOnePoint(img,1,0));
			hs.put(name+"up-1"+"right0", moveOnePoint(img,-1,0));
			hs.put(name+"up0"+"right1", moveOnePoint(img,0,1));
			hs.put(name+"up0"+"right-1", moveOnePoint(img,0,-1));
			hs.put(name+"angle1", getAnglePic(img,5));
			hs.put(name+"angle-1", getAnglePic(img,-5));
			hs.put(name+"angle2", getAnglePic(img,10));
			hs.put(name+"angle-2", getAnglePic(img,-10));
		}
		for(String key:hs.keySet()){
			ImageIO.write(hs.get(key), "png",new File(path+key+".png"));
		}
	}
	
	public static void main(String[] args) throws Exception{
		//BufferedImage img = ImageIO.read(new File("I:\\cncanew\\2m7j_1002036.png"));
		//ImageIO.write(getSheer(img), "png", new File("I:\\cncanew\\2m7j_color_"+System.currentTimeMillis()+".png"));
	}
	
	
}
