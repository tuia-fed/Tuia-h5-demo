package com.tm.demo;

import android.text.TextUtils;

import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommonUtils {

    /**
     * 判断列表是否为空
     *
     * @param list
     * @return
     */
    public static <T> boolean isEmpty(List<T> list) {
        if (list != null && list.size() > 0) {
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否为空
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    /**
     * 获取缓存路径
     *
     * @return
     */
    public static String getDownLoadPath() {
        return FileDownloadUtils.getDefaultSaveRootPath() + File.separator+"download" + File.separator+ "tmpdir" + File.separator;
    }

    /**
     * 检测下载文件存在与否
     *
     * @return
     */
    public static File checkFileExit(String appName) {
        File file = null;
        try {
            final List<File> files = CommonUtils.listFileSortByModifyTime(CommonUtils.getDownLoadPath());
            if (files != null && files.size() != 0) {
                for (File f : files) {
                    if ((appName.equals(f.getName()))) {
                        file = f;
                    }
                }
            }
        } catch (Exception e) {
        }
        return file;
    }


    /**
     * 获取目录下所有文件(按时间排序)
     *
     * @param path
     * @return
     */
    public static List<File> listFileSortByModifyTime(String path) {
        List<File> list = getFiles(path, new ArrayList<File>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }
        return list;
    }

    /**
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

}
