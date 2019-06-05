package com.lexlang.ImageUtil.cut;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.imageio.ImageIO;

import com.lexlang.ImageUtil.CommonUtil;

/**
 * 切割图片，权重法
 * @author Administrator lex
 * 主要用途是扫描图片里面的 黑点块
 */
public class ScanPicBlock {

	/**
	 * 
	 * @param img       加载的是整个图片，不是清洗后的图片
	 */
	public static HashSet<String> run(BufferedImage img){//获得对应的块的值
		HashSet<String> block=new HashSet<String>();//存储已经处理的点坐标
		Stack<String> handle=new Stack<String>();//存储需要处理的点，如果当此为空时结束
		int startX=0;int startY=0;//设置初始变量
		final int width = img.getWidth();
		final int height = img.getHeight();
		
		//获得初始位置，竖向位置扫描
		boolean flag=true;
		for(int x = 0; x < width && flag; ++x) {
			for (int y = 0; y < height && flag; ++y) {
				if(CommonUtil.isBlack(img.getRGB(x, y), 100)>0){
					startX=x;startY=y;
					flag=false;
					handle.add(x+"\t"+y);
				}
			}
		}
		
		while(! handle.isEmpty()){
			String[] arr=handle.pop().split("\t");
			scanUpDown(img,Integer.parseInt(arr[0]),Integer.parseInt(arr[1]),block,handle);
		}
		
		return block;
	}
	/**
	 * 
	 * @param img 原始图片
	 * @param x 当前检测点X坐标
	 * @param y 当前检测点y坐标
	 * @param block 当前代码块坐标
	 * @param Handle 要处理的集合
	 */
	public static void scanUpDown(BufferedImage img,int x,int y,HashSet<String> block,Stack<String> handle){
		block.add(x+"\t"+y);
		for(int i=-1;i<=1;i++){
			for(int j=-1;j<=1;j++){
				try{
					if(CommonUtil.isBlack(img.getRGB(x+i, y+j), 100)>0){
						int n=x+i;
						int m=y+j;
						if(! block.contains(n+"\t"+m)){
							handle.add(n+"\t"+m);
						}
					}
				}
				catch(Exception e){}
			}
		}
	}
	/**
	 * 
	 * @param img   背景清洗干净的图片
	 * @param hold  少于这个数，就认识干扰点
	 * @return      返回图片清洗的，块状单元图片
	 * @throws Exception
	 */
	public static List<BufferedImage> splitImage(BufferedImage img,int hold) throws Exception {
		final List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		//获得第一个hs
		final int width = img.getWidth();
		final int height = img.getHeight();
		//ImageIO.write(img, "jpg", new File("captcha/"+System.currentTimeMillis()+".jpg"));
		HashSet<String> hs=run(img);
		while(! hs.isEmpty())
		{	  //保存图片
			  if( hs.size()>=hold){	 //构造图像
			  subImgs.add(getImg(width,height,hs));
			  }
			  //清除hs里面块的坐标
			  Iterator<String> it = hs.iterator();
			  while(it.hasNext()){
				  String[] arr=it.next().split("\t");
				  img.setRGB(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]), Color.WHITE.getRGB());
			  }
			  //ImageIO.write(img, "jpg", new File("captcha/"+System.currentTimeMillis()+".jpg"));
			  //获取块状坐标
			  hs=run(img);
		}
		return subImgs;
	}
	
	/**
	 * 
	 * @param img 背景干净而之图片
	 * @param hold
	 * @return 除去黑块
	 * @throws Exception
	 */
	public static BufferedImage removeBlockImage(BufferedImage bimage,int hold) throws Exception {
		BufferedImage img=CommonUtil.copyBufferedImage(bimage);
		final List<BufferedImage> subImgs = new ArrayList<BufferedImage>();

		final int width = img.getWidth();
		final int height = img.getHeight();

		HashSet<String> hs=run(img);
		while(! hs.isEmpty())
		{	  //保存图片
			  if( hs.size()<=hold){	 //构造图像
				  {
					  Iterator<String> it = hs.iterator();
					  while(it.hasNext()){
						  String[] arr=it.next().split("\t");
						  bimage.setRGB(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]), Color.WHITE.getRGB());
					  }
				  }
			  }
			  
			  {
				  Iterator<String> it = hs.iterator();
				  while(it.hasNext()){
					  String[] arr=it.next().split("\t");
					  img.setRGB(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]), Color.WHITE.getRGB());
				  }
			  }
			  hs=run(img);
		}
		return bimage;
	}
	
	/**
	 * 
	 * @param img       背景清洗干净的图片
	 * @param minWidth  最小的宽度
	 * @param minHeight 最小的高度 小于这个范围就认为是干扰点
	 * @return
	 * @throws Exception
	 */
	public static List<BufferedImage> splitImage(BufferedImage img,int minWidth,int minHeight) throws Exception {
		final List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		//获得第一个hs
		final int width = img.getWidth();
		final int height = img.getHeight();

		HashSet<String> hs=run(img);
		while(! hs.isEmpty())
		{	  //保存图片
			  if(getBlock(hs,minWidth,minHeight)){	 //构造图像
			  subImgs.add(getImg(width,height,hs));
			  }
			  //清除hs里面块的坐标
			  Iterator<String> it = hs.iterator();
			  while(it.hasNext()){
				  String[] arr=it.next().split("\t");
				  img.setRGB(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]), Color.WHITE.getRGB());
			  }
			  //获取块状坐标
			  hs=run(img);
		}
		return subImgs;
	}
	/**
	 * 判断这个点块的大小
	 * @param hs 导入点坐标的
	 * @param minWidth
	 * @param minHeight
	 * @return
	 */
	public static boolean getBlock(HashSet<String> hs,int minWidth,int minHeight){
		int minW=10000;
		int minH=10000;
		int maxW=0;
		int maxH=0;
		Iterator<String> it = hs.iterator();
		  while(it.hasNext()){
			  String[] arr=it.next().split("\t");
			  if(minW<Integer.parseInt(arr[0])){
				  minW=Integer.parseInt(arr[0]);
			  }
			  if(maxW>Integer.parseInt(arr[0])){
				  maxW=Integer.parseInt(arr[0]);
			  }
			  if(minH<Integer.parseInt(arr[1])){
				  minH=Integer.parseInt(arr[1]);
			  }
			  if(maxH>Integer.parseInt(arr[1])){
				  maxH=Integer.parseInt(arr[1]);
			  }
		  }
		if((maxH-minH>minHeight) && (maxW-minW>minWidth)){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param img   背景清洗干净的图片
	 * @param hold  少于这个数，就认识干扰点
	 * @param maxWidth  组合一起的宽度比这个还小，就认识是一体的
	 * @return      返回图片清洗的，块状单元图片
	 * @throws Exception
	 */
	public static List<BufferedImage> splitJoinImage(BufferedImage img,int hold,int maxWidth) throws Exception {
		final List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		//获得第一个hs
		final int width = img.getWidth();
		final int height = img.getHeight();
		ArrayList<HashSet<String>> list=new ArrayList<HashSet<String>>();//保存到一起
		HashSet<String> hs=run(img);
		while(! hs.isEmpty())
		{	  //保存图片
			  if( hs.size()>=hold){	 //构造图像
				  list.add(hs);
              }
			  //清除hs里面块的坐标
			  Iterator<String> it = hs.iterator();
			  while(it.hasNext()){
				  String[] arr=it.next().split("\t");
				  img.setRGB(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]), Color.WHITE.getRGB());
			  }
			  //获取块状坐标
			  hs=run(img);
		}
		
		//东西揉合到一起
		if(list.size()>0){
		ArrayList<HashSet<String>> st=new ArrayList<HashSet<String>>();
		st.add((HashSet<String>) list.get(0).clone());
		for(int i=1;i<list.size();i++){
			for(int j=0;j<st.size();j++){
				if(two2one(list.get(i),st.get(j),maxWidth)){
					HashSet<String> li=new HashSet<String>();
					st.get(j).addAll(list.get(i));//加载所有的数据
				}
				else{
					st.add((HashSet<String>) list.get(i).clone());
				}
			}
		}
		
		//图片输出
		HashSet<String> filter=new HashSet<String>();
		for(int i=0;i<st.size();i++){
			if(! filter.containsAll(st.get(i))){
			     subImgs.add(getImg(width,height,st.get(i)));
			     filter.addAll(st.get(i));
			}
		}
		
		
		}

		
		return subImgs;
	}
	

	
	public static boolean two2one(HashSet<String> aHs,HashSet<String> bHs,int maxWidth){
		//boolean flag=false;
		//提取两组数据的宽度
		int aMin=1000;
		int aMax=0;
		int bMin=1000;
		int bMax=0;
		Iterator<String> it = aHs.iterator();
		while(it.hasNext()){
			String[] arr=it.next().split("\t");
			int temp=Integer.parseInt(arr[0]);
			if(aMin>temp){aMin=temp;}
			if(aMax<temp){aMax=temp;}
		}
		it = bHs.iterator();
		while(it.hasNext()){
			String[] arr=it.next().split("\t");
			int temp=Integer.parseInt(arr[0]);
			if(bMin>temp){bMin=temp;}
			if(bMax<temp){bMax=temp;}
		}
		//在内部，则返回为真
		if(aMin>=bMin && aMax<=bMax){return true;}
		if(bMin>=aMin && bMax<=aMax){return true;}
		//重合部分超过，百分之五十，则认为是整体
		if(aMin>=bMin && aMax>=bMax && aMin<=bMax){
			
			double CoincidenceLength=bMax-aMin;
			if(CoincidenceLength/(aMax-aMin)>0.5){
				return true;
			}
			if(CoincidenceLength/(bMax-bMin)>0.5){
				return true;
			}
		}
		
		if(bMin>=aMin && bMax>=aMax && bMin<=aMax){
			//double totalLength=aMax-bMin;
			double CoincidenceLength=aMax-bMin;
			if(CoincidenceLength/(aMax-aMin)>0.5){
				return true;
			}
			if(CoincidenceLength/(bMax-bMin)>0.5){
				return true;
			}
		}
		//不重合，但相离距离小于总体的百分之15%，且总长度跟一个字体比例80%到120%
		if(aMax<bMin){
			double totalLength=bMax-aMin;
			double jianXiLength=bMin-aMax;
			double jianXi=jianXiLength/totalLength;
			double biLie=totalLength/maxWidth;
			if(jianXi<0.15 && biLie>0.8 && biLie<1.2){
				return true;
			}
		}
		
		if(bMax<aMin){
			double totalLength=aMax-bMin;
			double jianXiLength=aMin-bMax;
			double jianXi=jianXiLength/totalLength;
			double biLie=totalLength/maxWidth;
			if(jianXi<0.15 && biLie>0.8 && biLie<1.2){
				return true;
			}
		}
		
		return false;
	}
	
	public static BufferedImage getImg(int width,int height,HashSet<String> hs) throws Exception{
		  int newImageData[] = new int[width*height];
		  for(int i=0;i<newImageData.length;i++){
			  newImageData[i]=Color.WHITE.getRGB();
		  }
		  BufferedImage imgChild = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
		  imgChild.setRGB(0, 0, width, height, newImageData, 0, width);
		  imgChild.flush();
		  Iterator<String> it = hs.iterator();
		  while(it.hasNext()){
			  String[] arr=it.next().split("\t");
			  imgChild.setRGB(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]), Color.BLACK.getRGB());
		  }
		  //ImageIO.write(imgChild, "jpg", new File("captcha/"+System.currentTimeMillis()+".jpg"));
		  //ImageIO.write(CommonUtil.removeBlank(imgChild, 100, 0), "jpg", new File("captcha/"+System.currentTimeMillis()+".jpg"));
		  return CommonUtil.removeBlank(imgChild, 100, 0);
		  //return imgChild;
	}
	
	public static BufferedImage MainColorgetImg(int width,int height,HashSet<String> hs) throws Exception{
		  int newImageData[] = new int[width*height];
		  for(int i=0;i<newImageData.length;i++){
			  newImageData[i]=Color.WHITE.getRGB();
		  }
		  BufferedImage imgChild = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
		  imgChild.setRGB(0, 0, width, height, newImageData, 0, width);
		  imgChild.flush();
		  Iterator<String> it = hs.iterator();
		  int Coun=0;
		  while(it.hasNext()){
			  String[] arr=it.next().split("\t");
			  Coun++;
			  imgChild.setRGB(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]), Color.BLACK.getRGB());
		  }
		  //System.out.println(Coun);
		  //return CommonUtil.removeBlank(imgChild, 1, 0);
		 
		  //return imgChild;
		  return splitJoinImage(imgChild,10,20).get(0);
	}
	
	/**
	 * 处理颜色相识的背景 颜色接近
	 * @param img
	 * @param hold 保留前几大色调，默认出掉第一大色，为背景色，参数不需要把背景色加入
	 * @return
	 * @throws Exception
	 */
	public static List<BufferedImage> splitImageSameColorRemoveBackgroud(BufferedImage img,int hold) throws Exception {
		final List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int width = img.getWidth();
		int height = img.getHeight();
		//把颜色数量放入map
		for(int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (map.containsKey(img.getRGB(x, y))) {
					map.put(img.getRGB(x, y), map.get(img.getRGB(x, y)) + 1);
				} else {
					map.put(img.getRGB(x, y), 1);
				}
			}
		}
		//排序打印出来
		ArrayList<Integer> sortList=new ArrayList<Integer>();
		for(Integer key:map.keySet()){
			sortList.add(key);
		}
		//排序输出
		Collections.sort(sortList);
		Iterator<Integer> it = sortList.iterator();
		while(it.hasNext()){
			int yanTemp=it.next();
			if(map.get(yanTemp)>1){
			System.out.println("颜色："+yanTemp+"--数量："+map.get(yanTemp));
			}
		}

		return subImgs;
	}

	
	/**
	 * 处理颜色相同的背景
	 * @param img
	 * @param hold 保留前几大色调，默认出掉第一大色，为背景色，参数不需要把背景色加入
	 * @return
	 * @throws Exception
	 */
	public static List<BufferedImage> splitImageMainColorRemoveBackgroud(BufferedImage img,int hold) throws Exception {
		final List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int width = img.getWidth();
		int height = img.getHeight();
		System.out.println(width);
		System.out.println(height);
		//把颜色数量放入map
		for(int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (map.containsKey(img.getRGB(x, y))) {
					map.put(img.getRGB(x, y), map.get(img.getRGB(x, y)) + 1);
				} else {
					map.put(img.getRGB(x, y), 1);
				}
			}
		}
		System.out.println(map.size());
		//挑出前hold颜色
		Map<Integer, Integer> mapColor = new HashMap<Integer, Integer>();
		for(Integer key:map.keySet()){
			if(mapColor.size()<=hold){
				mapColor.put(key, map.get(key));
			}
		}
		for(Integer key:map.keySet()){
			int minKey=10000;
			int colorKey=0;
			for(Integer k:mapColor.keySet()){
				if(minKey>mapColor.get(k)){minKey=mapColor.get(k);
				colorKey=k;
				}
			}
			if(! mapColor.containsKey(key)){
				if(minKey<map.get(key)){
					mapColor.remove(colorKey);
					mapColor.put(key, map.get(key));
				}
			}
		}
		
		//挑出背景色
		int minKey=0;//前景颜色数字最大
		int colorKey=0;
		for(Integer k:mapColor.keySet()){
			if(minKey<mapColor.get(k)){minKey=mapColor.get(k);
			  colorKey=k;
			}
		}
		mapColor.remove(colorKey);
		//如果color里面有的颜色则去掉
		HashMap<Integer,HashSet<String>> list=new HashMap<Integer,HashSet<String>>();//保存到一起
		for(int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (mapColor.containsKey(img.getRGB(x, y))) {//把已有的颜色去掉
					if(list.containsKey(img.getRGB(x, y))){
						list.get(img.getRGB(x, y)).add(x+"\t"+y);
					}
					else{
						HashSet<String> hsChild=new HashSet<String>();
						hsChild.add(x+"\t"+y);
						list.put(img.getRGB(x, y), hsChild);
					}
					//img.setRGB(x, y, Color.BLACK.getRGB());
				} 
			}
		}

		for(Integer key:list.keySet()){
			subImgs.add(MainColorgetImg(width,height,list.get(key)));
			//subImgs.add(getImg(width,height,list.get(key)));
		}
		return subImgs;
	}
	
}
