package com.lexlang.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.List;

import com.lexlang.ImageUtil.cut.RemoveBackgroud;
import com.lexlang.ImageUtil.cut.SplitPic;
import com.lexlang.ImageUtil.cut.Threshold;


/**
* @author lexlang
* @version 2018年7月19日 上午9:13:18
* 简单验证码,标准模板
* 
*/
public  class SvmPredict {
	public PredictUtil predict;
	
	public SvmPredict(String modelPath,String keyValuePath){
		predict=new PredictUtil(modelPath,keyValuePath);
	}
	
	/**
	 * 只要覆写这段代码，做图片切割
	 * @param img
	 * @return
	 * @throws Exception
	 */
	public List<BufferedImage> split(BufferedImage img) throws Exception {
		// TODO Auto-generated method stub
			return cutSave(img);
	}
	
	public static List<BufferedImage> cutSave(BufferedImage img) throws Exception {
		// TODO Auto-generated method stub
		//二值化
		BufferedImage reverseImg = RemoveBackgroud.SameColorRemoveBackgroud(img, Threshold.returnAllThresHold(img), true);
		//切割图片
		List<BufferedImage> list=SplitPic.hSplitImage(reverseImg);
		return list;
	}
	
	public String getAllOcr(BufferedImage img) throws Exception{
		StringBuilder sb=new StringBuilder();
		List<BufferedImage> list=split(img);
		for(int i=0;i<list.size();i++){
			sb.append(predict.getSingleCharOcr(list.get(i)));
		}
		return sb.toString();
	}
}
