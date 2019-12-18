package com.devil.library.media.bean;

import java.io.Serializable;

/**
 * 文件夹信息是实体
 */
public class FolderInfo implements Serializable {
    //文件夹名称
    public String folderName;
    //文件夹路径
    public String folderPath;
    //文件夹包含文件数量
    public int fileCount;

    public static FolderInfo createInstance(String folderName,String folderPath,int fileCount){
        FolderInfo info = new FolderInfo();
        info.folderName = folderName;
        info.folderPath = folderPath;
        info.fileCount = fileCount;
        return info;
    }
}
