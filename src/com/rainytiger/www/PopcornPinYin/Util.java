package com.rainytiger.www.PopcornPinYin;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Util {

    static Object read(String path) throws IOException, ClassNotFoundException {
        FileInputStream i = new FileInputStream(path);
        ObjectInputStream in = new ObjectInputStream(i);
        Object map = in.readObject();
        in.close();
        return map;
    }

    static void write(String path, Object obj) throws IOException {
        File file = new File(path);
        FileOutputStream o = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(o);
        out.writeObject(obj);
        out.flush();
        out.close();
    }

    static boolean cacheExist(String cachePath, String[] cacheNames) {
        File file = new File(cachePath);
        if (!file.exists()) return false;
        for (String fileName : cacheNames) {
            file = new File(cachePath + File.separator + fileName + ".data");
            if (!file.exists()) return false;
        }
        return true;
    }

    static void deleteCache(String cachePath, String[] cacheNames) throws IOException {
        File dir = new File(cachePath);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IOException("Create New Directory Filed!");
            }
        } else {
            for (String cacheName : cacheNames) {
                File file = new File(cacheName);
                if (file.exists()) {
                    if (!file.delete()) {
                        throw new IOException("Delete Cache File \"" + file.getName() + "\" Filed!");
                    }
                }
            }
        }
    }

    static boolean isAllChinese(String string) {
        String regex = "([\u4e00-\u9fa5]+)";
        StringBuilder sb = new StringBuilder();
        Matcher matcher = Pattern.compile(regex).matcher(string);
        while (matcher.find()) {
            sb.append(matcher.group(0));
        }
        return sb.toString().equals(string);
    }

    @SuppressWarnings("unchecked")
    static <T> T cast(Object obj) {
        return (T) obj;
    }

}
