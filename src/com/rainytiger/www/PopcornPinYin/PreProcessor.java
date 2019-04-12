package com.rainytiger.www.PopcornPinYin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@SuppressWarnings({"unchecked", "unused"})
class PreProcessor {

    private String cachePath;
    private String corpusPath;
    private Map<Character, Integer> hanzi2index;
    private Map<Integer, Character> index2hanzi;
    private Map<String, Integer> pinyin2index;
    private Map<Integer, String> index2pinyin;
    private List<Integer>[] hanzi2pinyin;
    private List<Integer>[] pinyin2hanzi;

    private String[] cacheNames = {
            "index2hanzi",
            "hanzi2index",
            "pinyin2hanzi",
            "index2pinyin",
            "hanzi2pinyin",
            "pinyin2hanzi"
    };

    PreProcessor(String corpusPath, String cachePath) throws IOException, ClassNotFoundException {
        this.corpusPath = corpusPath;
        this.cachePath = cachePath;

        if (cacheExist()) {
            readAll();
        } else {
            deleteCache();
            initMap();
            writeAll();
        }
    }

    private void writeAll() throws IOException {
        Util.write(cachePath + File.separator + "index2hanzi.data", index2hanzi);
        Util.write(cachePath + File.separator + "hanzi2index.data", hanzi2index);
        Util.write(cachePath + File.separator + "pinyin2index.data", pinyin2index);
        Util.write(cachePath + File.separator + "index2pinyin.data", index2pinyin);
        Util.write(cachePath + File.separator + "hanzi2pinyin.data", hanzi2pinyin);
        Util.write(cachePath + File.separator + "pinyin2hanzi.data", pinyin2hanzi);
    }

    private void readAll() throws IOException, ClassNotFoundException {
        index2hanzi = (Map<Integer, Character>) Util.read(cachePath + File.separator + "index2hanzi.data");
        hanzi2index = (Map<Character, Integer>) Util.read(cachePath + File.separator + "hanzi2index.data");
        pinyin2index = (Map<String, Integer>) Util.read(cachePath + File.separator + "pinyin2index.data");
        index2pinyin = (Map<Integer, String>) Util.read(cachePath + File.separator + "index2pinyin.data");
        hanzi2pinyin = (List<Integer>[]) Util.read(cachePath + File.separator + "hanzi2pinyin.data");
        pinyin2hanzi = (List<Integer>[]) Util.read(cachePath + File.separator + "pinyin2hanzi.data");
    }

    private void initMap() throws IOException {
        hanzi2index = new HashMap<>();
        index2hanzi = new HashMap<>();
        pinyin2index = new HashMap<>();
        index2pinyin = new HashMap<>();
        hanzi2pinyin = new List[6763];
        pinyin2hanzi = new List[406];

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

    private boolean cacheExist() {
        File file = new File(cachePath);
        if (!file.exists()) return false;
        for (String fileName : cacheNames) {
            file = new File(cachePath + File.separator + fileName + ".data");
            if (!file.exists()) return false;
        }
        return true;
    }

    private void deleteCache() throws IOException {
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

    int parseHanzi(char hanzi) {
        return hanzi2index.getOrDefault(hanzi, -1);
    }

    public char parseHanzi(int index) {
        return index2hanzi.getOrDefault(index, null);
    }

    private int parsePinyin(String pinyin) {
        return pinyin2index.getOrDefault(pinyin, -1);
    }

    public String parsePinyin(int index) {
        return index2pinyin.getOrDefault(index, null);
    }

    public List<Integer> hanziList(String pinyin) {
        int index = parsePinyin(pinyin);
        if (index == -1) return null;
        return pinyin2hanzi[index];
    }

    public List<Integer> pinyinList(char hanzi) {
        int index = parseHanzi(hanzi);
        if (index == -1) return null;
        return hanzi2pinyin[index];
    }

}
