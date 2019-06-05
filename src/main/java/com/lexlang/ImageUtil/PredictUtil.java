package com.lexlang.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.lexlang.ImageUtil.svm.svm_my_predict;


/**
* @author lexlang
* @version 2018年7月16日 上午10:39:53
* 
*/
public class PredictUtil {
	private svm_my_predict predict;
	private Properties pps = new Properties();
	private static final int whiteThreshold = 50;
	
	public PredictUtil(String modelPath,String keyValuePath){
		this.predict=new svm_my_predict(modelPath);
		try {
			InputStream in = new BufferedInputStream (new FileInputStream(keyValuePath));
			this.pps.load(in);
		} catch (IOException e) {
		}
	}
	
	public String getSingleCharOcr(BufferedImage img) throws Exception {
		int key=(int) Double.parseDouble(predict.predict(CommonUtil.imgToSvmInput(img, whiteThreshold))+"");
		if(key>9){ 
		  String value = pps.getProperty((key)+"");
		  return value;
		}else{
		 return key+"" ;
		}	
	}
	
}
