package com.rainytiger.www.PopcornPinYin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class CorpusProcessor {

    private List<String> corpus;
    private List<String> words;
    private Map<Character, Integer> hanzi2index = new HashMap<>();
    private Map<Integer, Character> index2hanzi = new HashMap<>();
    private Map<String, Integer> pinyin2index = new HashMap<>();
    private Map<Integer, String> index2pinyin = new HashMap<>();
    private List<Integer>[] hanzi2pinyin = new List[6763];
    private List<Integer>[] pinyin2hanzi = new List[406];

    CorpusProcessor(String corpusPath) {
        setCorpus(findCorpus(corpusPath));
    }

    void initMap() throws IOException {
        // 汉字
        BufferedReader hanziBufferReader = new BufferedReader(new FileReader("corpus/hanzi.txt"));
        String line = hanziBufferReader.readLine();
        char[] hs = line.toCharArray();
        int hanziIndex = 0;
        for (char c : hs) {
            index2hanzi.put(hanziIndex, c);
            hanzi2index.put(c, hanziIndex);
            hanzi2pinyin[hanziIndex++] = new ArrayList<>();
        }

        // 拼音
        BufferedReader pinyinBufferReader = new BufferedReader(new FileReader("corpus/pinyin.txt"));
        line = null;
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

    private void setCorpus(List<String> strings) {
        corpus = strings;
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
}
