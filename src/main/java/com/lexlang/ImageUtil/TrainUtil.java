package com.lexlang.ImageUtil;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.jhlabs.image.ScaleFilter;
import com.lexlang.ImageUtil.svm.svm_train;

/**
 * 训练样本
 * @author A
 *
 */
public class TrainUtil {
	public static void train(String clazz) throws Exception{
		   scaleTraindata(clazz, 100);
		   svm_train.main(new String[] {new File( clazz + "/data.txt").getAbsolutePath(), new File( clazz + "/data.txt.model").getAbsolutePath()});
	}
	
	public static int isBlack(int colorInt, int whiteThreshold) {
		final Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() <= whiteThreshold) {
			return 1;
		}
		return 0;
	}
	
	public static void scaleTraindata(String category, int threshold) throws Exception {
		final File dir = new File(category);
		final File dataFile = new File(category + "/data.txt");
		final FileOutputStream fs = new FileOutputStream(dataFile);
		final File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png");
			}
		});
		int Coun=10;
		HashMap<String,Integer> hs=new HashMap<String,Integer>();
		for (final File file : files) {
			final BufferedImage img = ImageIO.read(file);
			final ScaleFilter sf = new ScaleFilter(16, 16);
			BufferedImage imgdest = new BufferedImage(16, 16, img.getType());
			imgdest = sf.filter(img, imgdest);
			//new File("train/svm/" + category).mkdirs();
			//ImageIO.write(imgdest, "JPG", new File("train/svm/" + category + "/" + file.getName()));
			//fs.write((file.getName().charAt(0) + " ").getBytes());
			System.out.println(file.getName());
			String value=file.getName().substring(0, file.getName().indexOf("_"));
			if(isNumeric(value))
			{fs.write((value + " ").getBytes());}
			else
			{   if(hs.containsKey(value)){
					fs.write((hs.get(value) + " ").getBytes());
				}else{
					hs.put(value, Coun++);
					fs.write((hs.get(value) + " ").getBytes());
				}
			}
			
			int index = 1;
			for (int x = 0; x < imgdest.getWidth(); ++x) {
				for (int y = 0; y < imgdest.getHeight(); ++y) {
					fs.write((index++ + ":" + isBlack(imgdest.getRGB(x, y), threshold) + " ").getBytes());
				}
			}
			fs.write("\r\n".getBytes());
		}
		fs.close();
		//---------
		Properties pps = new Properties();        
		//调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。  
		//强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
		//new File("C:/train/" + category + "/valueKey.properties").mkdirs();
		OutputStream out = new FileOutputStream( category + "/valueKey.properties");
		for(String key:hs.keySet()){
		pps.setProperty(hs.get(key)+"", key);
	    //以适合使用 load 方法加载到 Properties 表中的格式，  
		//将此 Properties 表中的属性列表（键和元素对）写入输出流  
		}
		pps.store(out, "Update_name");
		
		
	}
	
	public static boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
	}
	
	public static void main(String[] args) throws Exception{
		train("I:\\myPy\\CrawlUtils\\crawl");
	}
}
