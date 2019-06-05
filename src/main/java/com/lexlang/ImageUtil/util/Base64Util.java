package com.lexlang.ImageUtil.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

/**
* @author lexlang
* @version 2018年7月17日 上午9:48:05
* 
*/
public class Base64Util {
    public static byte[] decode(String bytes) {  
        return Base64.decodeBase64(bytes);  
    }  
    
    public static String encode(final byte[] bytes) {  
        return new String(Base64.encodeBase64(bytes));  
    }
    
    public static String img2Base64(BufferedImage img) throws IOException{
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
		ImageIO.write(img, "png", os);  
		String imgString=encode(os.toByteArray());
		return imgString;
    }
    
    public static BufferedImage base64Toimg(String bytes) throws IOException{
    	byte[] buf=decode(bytes);
    	InputStream sbs = new ByteArrayInputStream(buf); 
    	return ImageIO.read(sbs);
    }
    
}
