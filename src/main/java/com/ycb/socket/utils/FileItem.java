package com.ycb.socket.utils;

import java.io.InputStream;

/**
 * 临时存储上传文件信息
 */
public class FileItem {
    private InputStream inputStream;
    private String name;
    private String contentType;

    public FileItem(String name, InputStream inputStream, String contentType) {
        this.inputStream = inputStream;
        this.name = name;
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }


}
