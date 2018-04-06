package cn.com.yyft.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * @author
 *
 */
public class PropertiesHelp {
	/**
	 *
	 * @param key
	 * @return
	 */
	public static String getRelativePathValue(String key){
		Properties properties = new Properties();
		InputStream in = PropertiesHelp.class.getResourceAsStream("/resources/config1.properties");
		try {
			properties.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (String)properties.get(key);
	}
	
	public static void main(String[] args) {
		System.out.println(PropertiesHelp.getRelativePathValue("zookeeper.connect"));
	}
}
