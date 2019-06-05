package com.lexlang.ImageUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import javax.imageio.ImageIO;

import com.lexlang.ImageUtil.util.Base64Util;


public class TotalName {
	
	public void totalName(){
		File dir = new File("C:/Users/A/Pictures/app/chinaTaxRecog/");
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png");
			}
		});
		HashSet<String> hs=new HashSet<String>(); 
		int len=0;
		for (final File file : files) {
			String[] arr=file.getName().split("_");
			for(int i=0;i<arr[0].length();i++){
				hs.add((arr[0].charAt(i)+"").toLowerCase());
			}
			if(len<=arr[0].length()){
				len=arr[0].length();
			}
			if(arr[0].contains("-")|| arr[0].contains(".")){
				System.out.println(file.getName());
			}
			
		}
		System.out.println(len);
		
		StringBuilder sb=new StringBuilder();
		Iterator<String> it = hs.iterator();
		String bef="";
		while(it.hasNext()){
			String item = it.next();
			if(! bef.contains(item))
				sb.append(item);
		}
		System.out.println(sb.toString());
	}
	
	
	public void deleteSame() throws IOException{
		File dir = new File("I:\\myPy\\CrawlUtils\\crawl");
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png");
			}
		});
		HashSet<String> hs=new HashSet<String>(); 
		int len=0;
		for (final File file : files) {
			String img=Base64Util.img2Base64(ImageIO.read(file));
			if(hs.contains(img)){
				System.out.println(file.getName());
				file.delete();
			}else{
				hs.add(img);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		new TotalName().totalName();
	}

}
