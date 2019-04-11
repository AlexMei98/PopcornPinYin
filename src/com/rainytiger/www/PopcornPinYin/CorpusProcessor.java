package com.rainytiger.www.PopcornPinYin;

import java.io.*;
import java.util.*;

@SuppressWarnings("unchecked")
class CorpusProcessor {

    private List<String> corpus;
    private String cachePath;
    private String corpusPath;
    private Map<Character, Integer> hanzi2index = new HashMap<>();
    private Map<Integer, Character> index2hanzi = new HashMap<>();
    private Map<String, Integer> pinyin2index = new HashMap<>();
    private Map<Integer, String> index2pinyin = new HashMap<>();
    private List<Integer>[] hanzi2pinyin = new List[6763];
    private List<Integer>[] pinyin2hanzi = new List[406];

    CorpusProcessor(String corpusPath, String cachePath) {
        this.corpusPath = corpusPath;
        corpus = findCorpus(this.corpusPath);
        this.cachePath = cachePath;
    }

    private List<String> findCorpus(String path) {
        File filePath = new File(path);
        File[] files = filePath.listFiles();
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }

    private Object read(String path) throws IOException, ClassNotFoundException {
        FileInputStream i = new FileInputStream(path);
        ObjectInputStream in = new ObjectInputStream(i);
        Object map = in.readObject();
        in.close();
        return map;
    }

    private void write(String path, Object obj) throws IOException {
        File file = new File(path);
        FileOutputStream o = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(o);
        out.writeObject(obj);
        out.flush();
        out.close();
    }

    void writeAll() throws IOException {
        write(cachePath + File.separator + "index2hanzi.data", index2hanzi);
        write(cachePath + File.separator + "hanzi2index.data", hanzi2index);
        write(cachePath + File.separator + "pinyin2index.data", pinyin2index);
        write(cachePath + File.separator + "index2pinyin.data", index2pinyin);
        write(cachePath + File.separator + "hanzi2pinyin.data", hanzi2pinyin);
        write(cachePath + File.separator + "pinyin2hanzi.data", pinyin2hanzi);
    }

    void readAll() throws IOException, ClassNotFoundException {
        index2hanzi = (Map<Integer, Character>) read(cachePath + File.separator + "index2hanzi.data");
        hanzi2index = (Map<Character, Integer>) read(cachePath + File.separator + "hanzi2index.data");
        pinyin2index = (Map<String, Integer>) read(cachePath + File.separator + "pinyin2index.data");
        index2pinyin = (Map<Integer, String>) read(cachePath + File.separator + "index2pinyin.data");
        hanzi2pinyin = (List<Integer>[]) read(cachePath + File.separator + "hanzi2pinyin.data");
        pinyin2hanzi = (List<Integer>[]) read(cachePath + File.separator + "pinyin2hanzi.data");
    }

    void initMap() throws IOException {
        // 汉字
        BufferedReader hanziBufferReader =
                new BufferedReader(new FileReader(corpusPath + File.separator + "hanzi.txt"));
        String line = hanziBufferReader.readLine();
        char[] hs = line.toCharArray();
        int hanziIndex = 0;
        for (char c : hs) {
            index2hanzi.put(hanziIndex, c);
            hanzi2index.put(c, hanziIndex);
            hanzi2pinyin[hanziIndex++] = new ArrayList<>();
        }

        // 拼音
        BufferedReader pinyinBufferReader =
                new BufferedReader(new FileReader(corpusPath + File.separator + "pinyin.txt"));
        int pinyinIndex = 0;
        while ((line = pinyinBufferReader.readLine()) != null) {
            String[] chars = line.split(" ");
            index2pinyin.put(pinyinIndex, chars[0]);
            pinyin2index.put(chars[0], pinyinIndex);
            pinyin2hanzi[pinyinIndex] = new ArrayList<>();
            for (int i = 1, size = chars.length; i < size; i++) {
                hanziIndex = hanzi2index.get(chars[i].charAt(0));
                pinyin2hanzi[pinyinIndex].add(hanziIndex);
                hanzi2pinyin[hanziIndex].add(pinyinIndex);
            }
            pinyinIndex++;
        }
    }

    boolean cacheExist() {
        File file = new File(cachePath);
        if (!file.exists()) return false;
        String[] cacheFiles = {
                "index2hanzi",
                "hanzi2index",
                "pinyin2hanzi",
                "index2pinyin",
                "hanzi2pinyin",
                "pinyin2hanzi"
        };
        for (String fileName : cacheFiles) {
            file = new File(cachePath + File.separator + fileName + ".data");
            if (!file.exists()) return false;
        }
        return true;
    }

    void deleteCache() throws IOException {
        File dir = new File(cachePath);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IOException("Create New Directory Filed!");
            }
        } else {
            File[] cacheFiles = dir.listFiles();
            if (cacheFiles != null) {
                for (File file : cacheFiles) {
                    if (file.exists()) {
                        if (!file.delete()) {
                            throw new IOException("Delete Cache File \"" + file.getName() + "\" Filed!");
                        }
                    }
                }
            }
        }
    }

    public String toString() {
        StringBuilder ret = new StringBuilder("Return sample pairs:\n");
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            int randInt = rand.nextInt(406);
            ret.append(randInt).append(" -> ").append(pinyin2hanzi[randInt]).append("\n\t");
            ret.append(index2pinyin.get(randInt)).append(" -> [");
            for (int j = 0; j < pinyin2hanzi[randInt].size(); j++) {
                ret.append(index2hanzi.get(pinyin2hanzi[randInt].get(j))).append(", ");
            }
            ret.append("]\n\n");
        }
        ret.append("\n\n");
        for (int i = 0; i < 5; i++) {
            int randInt = rand.nextInt(6763);
            ret.append(randInt).append(" -> ").append(hanzi2pinyin[randInt]).append("\n");
            ret.append("\t").append(index2hanzi.get(randInt)).append(" -> [");
            for (int j = 0; j < hanzi2pinyin[randInt].size(); j++) {
                ret.append(index2pinyin.get(hanzi2pinyin[randInt].get(j))).append(", ");
            }
            ret.append("]\n\n");
        }
        return ret.toString();
    }
}
