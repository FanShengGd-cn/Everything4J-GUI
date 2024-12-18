package com.fansheng.everything_gui;

import com.sun.jna.WString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Helixcs
 * Email : <p>zhangjian12424#gmail.com</p>
 */
public final class Everything4j {

    private static EverythingNative everythingNative;

    private static final int BUFFER_LEN = 260;

    public static String loadDllFromResources(String resourcePath) throws Exception {
        // 获取 .dll 文件的 InputStream
        InputStream inputStream = Everything4j.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new RuntimeException("Unable to find resource: " + resourcePath);
        }

        // 创建临时文件
        File tempFile = File.createTempFile("temp-dll-", ".dll");
        tempFile.deleteOnExit(); // JVM 退出时删除临时文件

        // 将 InputStream 写入临时文件
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return tempFile.getAbsolutePath();
    }

    static {
        try {
            if (EverythingUtils.JDK64BIT == EverythingUtils.currentJDKBit()) {
                // 从 resources 加载 DLL
                String dllPath = loadDllFromResources("Everything64.dll");
                everythingNative = EverythingUtils.makeDllInstance(dllPath, EverythingNative.class);
            } else if (EverythingUtils.JDK32BIT == EverythingUtils.currentJDKBit()) {
                String dllPath = loadDllFromResources("Everything32.dll");
                everythingNative = EverythingUtils.makeDllInstance(dllPath, EverythingNative.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Everything4j() {
    }

    public static Everything4j getInstance() {
        if (!EverythingUtils.isWinArch()) {
            try {
                throw new EverythingException("OS Arch Only Windows Supported!");
            } catch (EverythingException e) {
                e.printStackTrace();
            }
        }
        return InnerHolder.everything4jInstance;
    }

    private static class InnerHolder {
        private static final Everything4j everything4jInstance = new Everything4j();
    }

    public List<String> searchPathList(String searchKey) {
        List<String> searchPathList = new ArrayList<>();
        everythingNative.Everything_SetSearchW(new WString(searchKey));
        everythingNative.Everything_QueryW(true);
        int resultNum = everythingNative.Everything_GetNumResults();
        for (int i = 0; i < resultNum; i++) {
            searchPathList.add(everythingNative.Everything_GetResultPathA(i).toString());
        }
        return searchPathList;
    }

    public List<String> searchNameList(String searchKey) {
        List<String> searchNameList = new ArrayList<>();
        everythingNative.Everything_SetSearchW(new WString(searchKey));
        everythingNative.Everything_QueryW(true);
        int fileNum = everythingNative.Everything_GetNumResults();
        for (int i = 0; i < fileNum; i++) {
            String fileName = everythingNative.Everything_GetResultFileNameW(i).toString();
            System.out.println(fileName);
            searchNameList.add(fileName);
        }
        return searchNameList;
    }

    public synchronized List<String> searchResult(String searchKey) {
        List<String> searchResult = new ArrayList<>();
        everythingNative.Everything_SetSearchW(new WString(searchKey));
        everythingNative.Everything_QueryW(true);

        Buffer buffer = CharBuffer.allocate(BUFFER_LEN);

        int resultNum = everythingNative.Everything_GetNumResults();
        for (int i = 0; i < resultNum; i++) {
            everythingNative.Everything_GetResultFullPathNameW(i, buffer, BUFFER_LEN);
            char[] bf = (char[]) buffer.array();
            // cut off when zero byte  appear
            for (int j = 0; j < bf.length - 1; j++) {
                if (bf[j] == 0x00) {
                    char[] dstArray = new char[j];
                    System.arraycopy(bf, 0, dstArray, 0, j);
                    searchResult.add(new String(dstArray).trim().replaceAll("\u0000", ""));
                    break;
                }
            }
        }
        return searchResult;
    }


    private static class EverythingException extends Exception {
        public EverythingException() {
        }

        public EverythingException(String message) {
            super(message);
        }

        public EverythingException(String message, Throwable cause) {
            super(message, cause);
        }

        public EverythingException(Throwable cause) {
            super(cause);
        }

        public EverythingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

}
