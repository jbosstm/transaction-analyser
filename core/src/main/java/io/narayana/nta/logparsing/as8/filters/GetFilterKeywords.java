package io.narayana.nta.logparsing.as8.filters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class GetFilterKeywords {
	
	InputStream inputStream;
	String filterKeywords;
	 
	public String getPropValues() {
 
		try {
			Properties prop = new Properties();
<<<<<<< HEAD
			String propFileName = "filter.properties";			
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

=======
			String propFileName = "filter.properties";
			
			inputStream = getClass().getClassLoader().getResourceAsStream("src/main/resources/"+propFileName);
 
>>>>>>> 286cbc1a35401e2cebb1f7b1b07bea9afd98cd4b
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}	
			
			filterKeywords = prop.getProperty("keywords");
			
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e){	
			}
		}
		return filterKeywords;
	}
}
