package io.narayana.nta.logparsing.as8.filters;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GetFilterKeywords {
    private List<String> filterKeywords;
    private static final Logger logger = Logger.getLogger(GetFilterKeywords.class.getName());

    private static class GetFilterKeywordsHolder {
        private static final GetFilterKeywords INSTANCE = new GetFilterKeywords();
    }

    public static final GetFilterKeywords getInstance() {
        return GetFilterKeywordsHolder.INSTANCE;
    }

    private GetFilterKeywords() {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            filterKeywords = new ArrayList<>();

            String propFileName = "filter.properties";
            logger.warn(getClass().getClassLoader().getResource(""));
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            for (String name : prop.stringPropertyNames()) {
                if (name.contains("keywords"))
                    filterKeywords.add(prop.getProperty(name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }
    }

    public List<String> getFilterKeywords() {
        return filterKeywords;
    }
}

