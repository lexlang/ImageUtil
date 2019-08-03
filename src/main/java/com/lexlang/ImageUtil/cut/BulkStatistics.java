package com.lexlang.ImageUtil.cut;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
* @author lexlang
* @version 2019年7月17日 上午9:45:08
* 
*/
public class BulkStatistics {
	
	private Map<String,BufferedImage> map=new HashMap<String,BufferedImage>();
	
	/**
	 * 添加数据到块状体内
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @param img
	 */
	public void addImage(int startX,int startY,int endX,int endY,BufferedImage img){
		map.put(startX+"_"+startY+"_"+endX+"_"+endY, img.getSubimage(startX+1, startY+1, endX-startX-2, endY-startY-2));
	}
	
	/**
	 * 判断坐标是否在块体内
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean checkInBox(int x,int y){
		Set<String> keys = map.keySet();
		for(String key:keys){
			String[] arr=key.split("_");
			if(x<Integer.parseInt(arr[2]) && y<Integer.parseInt(arr[3])){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getStore(){
		JSONArray item=new JSONArray();
		Set<String> keys = copySet(map.keySet());
		ArrayList<String> list=new ArrayList<String>();//排序结果

		while(keys.size()>0 && list.size()<keys.size()){
			int maxX=10000;
			int maxY=10000;
			String curTemp="";
			for(String key:keys){
				String[] arr=key.split("_");
				if(list.contains(key)){
					continue;
				}
				if(maxY>Integer.parseInt(arr[1])){
					maxX=Integer.parseInt(arr[0]);
					maxY=Integer.parseInt(arr[1]);
					curTemp=key;
				}else if(maxY==Integer.parseInt(arr[1]) && maxX>=Integer.parseInt(arr[0])){
					maxX=Integer.parseInt(arr[0]);
					maxY=Integer.parseInt(arr[1]);
					curTemp=key;
				}
			}
			list.add(curTemp);
		}

		for(int index=0;index<list.size();index++){
			int coun=1;
			JSONArray array=new JSONArray();
			if(! (index==list.size()-1)){
				while(checkYiHang(list.get(index),list.get(index+coun))){
					coun++;
					if(index+coun>=list.size()-1){
						break;
					}
				}
			}

			if(coun==1){
				array.add(map.get(list.get(index)));
			}else{
				for(int i=0;i<coun;i++){
					array.add(map.get(list.get(index+i)));
				}
				index+=coun-1;
			}
			item.add(array);
		}
		return item;
	}
	
	public boolean checkYiHang(String aKey,String bKey){
		String[] aArr=aKey.split("_");
		String[] bArr=bKey.split("_");
		int flag=Math.abs(Integer.parseInt(aArr[1])-Integer.parseInt(bArr[1]));
		if(flag<5){
			return true;
		}else{
			return false;
		}
	}
	
	public Set<String> copySet(Set<String> keys){
		Set<String> set=new HashSet<String>();
		for(String key:keys){
			set.add(key);
		}
		return set;
	}
	
}
