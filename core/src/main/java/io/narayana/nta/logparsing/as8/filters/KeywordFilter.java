/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package io.narayana.nta.logparsing.as8.filters;

import java.io.IOException;
import java.io.InputStream;
import io.narayana.nta.logparsing.common.Filter;
import java.util.Set;

/**
 * @author huyuan
 *
 */
public class KeywordFilter implements Filter{
	
	GetFilterKeywords filterKeywords = new GetFilterKeywords();	

    @Override
    public boolean matches(String line) throws IndexOutOfBoundsException{
    	
    	String keywordList = filterKeywords.getPropValues();
    	String[] keywords = keywordList.split(",");

        try {
        	for (int i=0; i<keywords.length;i++){
            	System.out.println(keywords[i]);
    			if(line.indexOf(keywords[i]) != -1)
    					return true;
    		}
        	return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
}