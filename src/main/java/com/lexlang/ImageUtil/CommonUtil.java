package com.lexlang.ImageUtil;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import com.jhlabs.image.ScaleFilter;
import com.lexlang.ImageUtil.gifUtil.GifDecoder;
import com.lexlang.ImageUtil.util.MD5Util;

/**
 * 常见图片处理方法
 * @author A
 *
 */
public class CommonUtil {
		
	 /**
	  * 把切分得图片再组装
	  * @param bufferedImage
	  * @param temp
	  * @param slice
	  * @return
	  * @throws IOException
	  */
	 public static BufferedImage filter(BufferedImage bufferedImage,int[] temp,int slice)throws IOException {

			  int width = bufferedImage.getWidth();
			  int height = bufferedImage.getHeight();
			  BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);     
			  
			  Graphics2D g2 = image.createGraphics();         
			  g2.setColor(Color.WHITE);     
			  g2.fillRect(0, 0, width, height); 
			  
			  int offWidth=width/slice;
			  int offheight=height/2;
			  
			  for(int i=0;i<slice*2;i++){
				  int order = temp[i];
				  int y=0;
				  if(order>=slice){
					  y=offheight;
					  order=order-10;
				  }
				  int x=order*offWidth;
				
				int tempi=i;
				int cutY=0;
				if(tempi>=slice){
					cutY=offheight;
					tempi=tempi-10;
				}
				int cutX=tempi*offWidth;
				
          		g2.translate(x,y);
          		g2.drawImage(bufferedImage.getSubimage(cutX, cutY, offWidth, offheight), null, null);
          		g2.translate(-x,-y);
			  }
			  
			  return image;
	}
	
	public static boolean isBlank(BufferedImage img){
		int width = img.getWidth()-3;
		int height = img.getHeight()-3;
		for (int x = 3; x < width; ++x) {
			for (int y = 3; y < height; ++y) {
				if (CommonUtil.isWhite(img.getRGB(x, y))) {
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 当前方法未测试
	 * 抽取gif 每一番转成 单个图片，便于识别
	 * @param is
	 * @return
	 */
	public static List<BufferedImage> extractGIF2PNG(InputStream is){
		GifDecoder decoder = new GifDecoder();
		decoder.read(is);
		List<BufferedImage> list=new ArrayList<BufferedImage>();
		for(int i=0;i<decoder.getFrameCount();i++){
			list.add(decoder.getFrame(i));
		}
		return list;
	}
	
	/**
	 * img 转 流
	 * @param img
	 * @return
	 * @throws IOException
	 */
	public static InputStream bufferedImage2InputStream(BufferedImage img) throws IOException{
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
		ImageIO.write(img, "png", imOut);
		InputStream is = new ByteArrayInputStream(bs.toByteArray());
		return is;
	}
	
	/**
	 * 缩放图片
	 * @param img
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage scaleImage(BufferedImage img,int width,int height){
		ScaleFilter sf = new ScaleFilter(width, height);
		BufferedImage imgdest = new BufferedImage(width, height, img.getType());
		imgdest = sf.filter(img, imgdest);
		return imgdest;
	}
	
	/**
	 * 合并图片
	 * @param img
	 * @param angle
	 * @return
	 */
	public static BufferedImage combinePic(BufferedImage img,BufferedImage img1){
		BufferedImage newImg=new BufferedImage(img.getWidth()+img1.getWidth(),img.getHeight(),img.getType());
		Graphics2D g2=newImg.createGraphics();
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, img.getWidth()+img1.getWidth(),img.getHeight());
		g2.translate(0,0);
		g2.drawImage(img, null, null);
		g2.translate(img.getWidth(),0);
		g2.drawImage(img1, null, null);
		return newImg;
	}
	
	/**
	 * 复制图片
	 * @param bimage
	 * @return
	 */
	public static BufferedImage copyBufferedImage(BufferedImage bimage){
        BufferedImage bimage2 = new BufferedImage(bimage.getWidth(), bimage.getHeight(), bimage.getType());
        bimage2.setData(bimage.getData());
        return bimage2;
	}
	


	public static boolean isWhite(int colorInt) {
		int whiteThreshold=125;
		final Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() > whiteThreshold) {
			return true;
		}
		return false;
	}
	
	public static int isWhite(int colorInt, int whiteThreshold) {
		final Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() > whiteThreshold) {
			return 1;
		}
		return 0;
	}
	
	public static boolean isBlack(int colorInt) {
		int whiteThreshold=300;
		final Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() <= whiteThreshold) {
			return true;
		}
		return false;
	}

	public static int isBlack(int colorInt, int whiteThreshold) {
		final Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() <= whiteThreshold) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * 移除图片空白
	 * @param img
	 * @param whiteThreshold
	 * @param white
	 * @return
	 * @throws Exception
	 */
	public static BufferedImage removeBlank(BufferedImage img, int whiteThreshold, int white) throws Exception {
		final int width = img.getWidth();
		final int height = img.getHeight();
		int start = 0;
		int end = 0;
		int lstart=0;
		int lend=0;
		//垂直方向
		Label1: for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				if (isWhite(img.getRGB(x, y), whiteThreshold) == white) {
					start = y;
					break Label1;
				}
			}
		}
		Label2: for (int y = height - 1; y >= 0; --y) {
			for (int x = 0; x < width; ++x) {
				if (isWhite(img.getRGB(x, y), whiteThreshold) == white) {
					end = y;
					break Label2;
				}
			}
		}
		//直方图水平方向
		Label3: for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (isWhite(img.getRGB(x, y), whiteThreshold) == white) {
					lstart = x;
					break Label3;
				}
			}
		}
		Label4: for (int x = width - 1; x >= 0; --x) {
			for (int y = 0; y < height; ++y) {
				if (isWhite(img.getRGB(x, y), whiteThreshold) == white) {
					lend = x;
					break Label4;
				}
			}
		}

		return img.getSubimage(lstart, start, lend - lstart + 1, end - start + 1);
		
	}
	
	/**
	 * 阈值设置二值化
	 * @param picFile
	 * @param whiteThreshold
	 * @return
	 * @throws Exception
	 */
	public static BufferedImage removeBackgroud(String picFile, int whiteThreshold) throws Exception {
		final BufferedImage img = ImageIO.read(new File(picFile));
		final int width = img.getWidth();
		final int height = img.getHeight();
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (isWhite(img.getRGB(x, y), whiteThreshold) == 1) {
					img.setRGB(x, y, Color.WHITE.getRGB());
				} else {
					img.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
		}
		return img;
	}


	public static void imgToSvmInput(BufferedImage img, String dataFile, int threshold) throws Exception {
		final FileOutputStream fs = new FileOutputStream(dataFile);
		final ScaleFilter sf = new ScaleFilter(16, 16);
		BufferedImage imgdest = new BufferedImage(16, 16, img.getType());
		imgdest = sf.filter(img, imgdest);
		fs.write(("0 ").getBytes());
		int index = 1;
		for (int x = 0; x < imgdest.getWidth(); ++x) {
			for (int y = 0; y < imgdest.getHeight(); ++y) {
				fs.write((index++ + ":" + isBlack(imgdest.getRGB(x, y), threshold) + " ").getBytes());
			}
		}
		fs.write("\r\n".getBytes());
		fs.close();
	}
	
	/**
	 * 图片转换为支持向量机的输入
	 * @param img
	 * @param threshold
	 * @return
	 * @throws Exception
	 */
	public static String imgToSvmInput(BufferedImage img, int threshold) throws Exception {
		StringBuilder sb=new StringBuilder();
		final ScaleFilter sf = new ScaleFilter(16, 16);
		BufferedImage imgdest = new BufferedImage(16, 16, img.getType());
		imgdest = sf.filter(img, imgdest);
		sb.append("0 ");
		int index = 1;
		for (int x = 0; x < imgdest.getWidth(); ++x) {
			for (int y = 0; y < imgdest.getHeight(); ++y) {
				sb.append(index++ + ":" + isBlack(imgdest.getRGB(x, y), threshold) + " ");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 判断两个颜色是否相识，比对色差小于threshold,则认为相识
	 * @param aColor
	 * @param bColor
	 * @param threshold
	 * @return
	 */
	public static boolean ColorLookLike(int aColor,int bColor,int threshold){
		 float h1=RGB2HSV(aColor);
		 float h2=RGB2HSV(bColor);
	     if(Math.abs(h1-h2)<threshold){return true;}
		 return false;
	}
	
	/**
	 * rgb转hsv
	 * @param aColor
	 * @return
	 */
	public static float RGB2HSV(int aColor){
		  int r1 = (aColor & 0xff0000) >> 16;
	      int g1 = (aColor & 0xff00) >> 8;
	      int b1 = (aColor & 0xff) ;
		  float r = (float)r1/255;
		  float g = (float)g1/255;
		  float b = (float)b1/255;
		  float max = Math.max(Math.max(r, g), b);
		  float min = Math.min(Math.min(r, g), b);
		  float h = 0;
		  if(r==max)
		   h = (g-b)/(max-min);
		  if(g==max)
		   h = 2+(b-r)/(max-min);
		  if(b==max) 
		   h= 4+(r-g)/(max-min);
		  h *=60;
		  if(h<0) h +=360;
		  return h;
    }
	
	/**
	 * png图片转成jpg
	 * @param bufferedImage
	 * @return
	 */
	public static BufferedImage PNG2JPG(BufferedImage bufferedImage){
        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
        bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
        return newBufferedImage;
	}
	
	/**
	 * 删除某路径下面相同的图片
	 * @param path
	 * @throws IOException
	 */
	public static void deleteSamePic(String path) throws IOException{
		  HashSet<String> hs=new HashSet<String>();
		  //String path="C:\\train\\Gdcourts";
		  File file=new File(path);
		  File[] tempList = file.listFiles();
		  System.out.println("该目录下对象个数："+tempList.length);
		  for (int i = 0; i < tempList.length; i++) {
		     if (tempList[i].isFile()) {
		     //读取某个文件夹下的所有文件
		     System.out.println("文件："+tempList[i]);
		     //BufferedImage bufferedImage = ImageIO.read(tempList[i].getAbsoluteFile()); 
		     String temp=MD5Util.getFileMD5String(tempList[i].getAbsoluteFile());
			 if(! hs.contains(temp)){
			    	hs.add(temp);
			 }else
			 {
			    	tempList[i].delete();
			 }
		   }
		 }
	}
	
	/**
	 * 打上时间戳    BufferedImage markImg=pressText(sdf.format(new Date()),img,"宋体",10,10,20,50,50);
	 * @param pressText
	 * @param src
	 * @param fontName
	 * @param fontStyle
	 * @param color
	 * @param fontSize
	 * @param x
	 * @param y
	 * @return
	 * @throws IOException
	 */
    public static BufferedImage pressText(String pressText,BufferedImage src,String fontName, int fontStyle, int color, int fontSize, int x,int y) throws IOException { 
	     int wideth = src.getWidth();      
	     int height = src.getHeight();      
	     BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_ARGB);      
	     Graphics g = image.createGraphics();      
	     g.drawImage(src, 0, 0, wideth, height, null);      
	     g.setColor(Color.RED);      
	     g.setFont(new Font(fontName, fontStyle, fontSize));      
	     g.drawString(pressText,fontSize+x,fontSize+y);      
	     g.dispose();      
	     return image;
    } 
	
}
