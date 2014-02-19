/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
package io.narayana.nta.webapp.models;

import io.narayana.nta.Configuration;
import io.narayana.nta.LogMonitorBean;
import io.narayana.nta.persistence.dao.GenericDAO;
import org.apache.commons.io.FilenameUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author <a href="mailto:zfeng@redhat.com">Amos Feng</a>
 */
@SessionScoped
@Named
public class SwitchLogBean implements Serializable {

    @EJB
    private LogMonitorBean logMonitor;

    @EJB
    private GenericDAO dao;

    private UploadedFile uploadedFile;

    private String currentLogFile;

    private Map<String,Object> logFiles = new LinkedHashMap<String, Object>();

    @PostConstruct
    public void init() {
        currentLogFile = logMonitor.getLogFile().getPath();
        logFiles.put(Configuration.LOGFILE_PATH, Configuration.LOGFILE_PATH);
        logFiles.put("upload", "upload");
    }

    public void changeLog() throws Exception {
        logMonitor.stop();

        if(currentLogFile.equals("upload")) {
            dao.deleteAll();

            String fileName = Configuration.UPLOAD_LOGFILE_PATH + File.separator + FilenameUtils.getName(uploadedFile.getName());
            String contentType = uploadedFile.getContentType();

            File file = new File(fileName);
            if(file.exists()) {
                file.delete();
            }
            file.getParentFile().mkdirs();
            file.createNewFile();

            logMonitor.setFile(file);
            logMonitor.start();
            while (!logMonitor.isRunning());

            OutputStream output = new FileOutputStream (file);
            output.write(uploadedFile.getBytes());
            output.close();

            currentLogFile = fileName;
        } else if(!logMonitor.getLogFile().getPath().equals(currentLogFile)){
            dao.deleteAll();

            logMonitor.setFile(new File(currentLogFile));
            logMonitor.reLoad();
            logMonitor.start();
        }
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getCurrentLogFile() {
        return currentLogFile;
    }

    public void setCurrentLogFile(String currentLogFile) {
        this.currentLogFile = currentLogFile;
    }

    public Map getLogFiles() {
        return logFiles;
    }

    public void setLogFiles(Map logFiles) {
        this.logFiles = logFiles;
    }
}
