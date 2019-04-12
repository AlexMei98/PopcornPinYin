package com.rainytiger.www.PopcornPinYin;

import java.io.*;

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

}
