package io.narayana.nta.logparsing.as8.filters;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetFilterKeywords {
    private String filterKeywords;
    private static final Logger logger = Logger.getLogger(GetFilterKeywords.class.getName());

    private static class GetFilterKeywordsHolder {
        private static final GetFilterKeywords INSTANCE = new GetFilterKeywords();
    }

    public static final GetFilterKeywords getInstance() {
        return GetFilterKeywordsHolder.INSTANCE;
    }

    private GetFilterKeywords() {
        try {
            InputStream in;
            Properties prop = new Properties();

            String propFileName = "filter.properties";
            in = getClass().getClassLoader().getResourceAsStream(File.separator + propFileName);
            if (in != null) {
                prop.load(in);
                filterKeywords = prop.getProperty("keywords");
                in.close();
            } else {
                logger.error("Oops: the " + propFileName + " file does not exist!", new FileNotFoundException(propFileName + "not found!"));
            }
        } catch (IOException ex) {
            logger.error("the filter keyword list init error!", ex);
        }
    }

    public String getFilterKeywords() {
        return filterKeywords;
    }
}