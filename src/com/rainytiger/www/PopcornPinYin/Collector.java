package com.rainytiger.www.PopcornPinYin;

import com.hankcs.hanlp.seg.common.Term;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Collector {

    private String corpusPath;
    private String cachePath;
    private List<String> corpusFileNames;
    private Map<Integer, Integer> map1;
    private Map<Integer, Map<Integer, Integer>> map2;
    private Map<Integer, Map<Integer, Map<Integer, Integer>>> map3;

    private PreProcessor processor;
    private JsonParser jsonParser = new JsonParser();

    private String[] cacheFileNames = {
            "map1",
            "map2",
            "map3"
    };

    Collector(String corpusPath, String cachePath, PreProcessor processor) {
        this.corpusPath = corpusPath;
        this.cachePath = cachePath;
        findCorpus(this.corpusPath);
        this.processor = processor;
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        map3 = new HashMap<>();
    }

    void init() throws IOException, ClassNotFoundException {
        if (Util.cacheExist(cachePath, cacheFileNames)) {
            readAll();
        } else {
            Util.deleteCache(cachePath, cacheFileNames);
            parseAll();
            writeAll();
        }
    }

    private void readAll() throws IOException, ClassNotFoundException {
        map1 = Util.cast(Util.read(cachePath + File.separator + "map1.data"));
        map2 = Util.cast(Util.read(cachePath + File.separator + "map2.data"));
        map3 = Util.cast(Util.read(cachePath + File.separator + "map3.data"));
    }

    private void writeAll() throws IOException {
        Util.write(cachePath + File.separator + "map1.data", map1);
        Util.write(cachePath + File.separator + "map2.data", map2);
        Util.write(cachePath + File.separator + "map3.data", map3);
    }

    private void findCorpus(String path) {
        File filePath = new File(path);
        File[] files = filePath.listFiles();
        corpusFileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                corpusFileNames.add(file.getName());
            }
        }
    }

    private void updateMap(String word) {
        List<Integer> list = new ArrayList<>();
        for (char c : word.toCharArray()) list.add(processor.parseHanzi(c));

        if (list.contains(-1)) return;

        int size = list.size();
        for (int c : list) update(c);
        if (size == 2) update(list.get(0), list.get(1));
        if (size == 3) update(list.get(0), list.get(1), list.get(2));
    }

    private void update(int han1) {
        map1.put(han1, map1.getOrDefault(han1, 0) + 1);
    }

    private void update(int han1, int han2) {
        Map<Integer, Integer> map = map2.getOrDefault(han1, new HashMap<>());
        map.put(han2, map.getOrDefault(han2, 0) + 1);
        map2.put(han1, map);
    }

    private void update(int han1, int han2, int han3) {
        Map<Integer, Map<Integer, Integer>> mMap = map3.getOrDefault(han1, new HashMap<>());
        Map<Integer, Integer> map = mMap.getOrDefault(han2, new HashMap<>());
        map.put(han3, map.getOrDefault(han3, 0) + 1);
        mMap.put(han2, map);
        map3.put(han1, mMap);
    }

    private void parseOneFile(String fileName, int index, int total) throws IOException {
        LineNumberReader lnr = new LineNumberReader(new FileReader(fileName));
        long ret = lnr.skip(Long.MAX_VALUE);
        if (ret >= Long.MAX_VALUE) throw new IOException("This File Is Too Large!");
        int lineNumber = lnr.getLineNumber() + 1;

        String line;
        int lineIndex = 1;
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        System.out.println("Parsing " + fileName + "...");
        while ((line = br.readLine()) != null) {
            if (lineIndex % 1000 == 0)System.out.println("Parsing File " + index + " / " + total + " | Line " + lineIndex + " / " + lineNumber);
            lineIndex++;
            List<Term> terms = jsonParser.parseJson(line);
            for (Term term : terms) {
                if (!Util.isAllChinese(term.word)) continue;
                updateMap(term.word);
            }
        }
    }

    private void parseAll() throws IOException {
        int size = corpusFileNames.size();
        int index = 1;
        long startTime = System.currentTimeMillis();
        for (String corpusName : corpusFileNames) {
            parseOneFile(corpusPath + File.separator + corpusName, index++, size);
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total Time: " + totalTime + " ms");
    }

    public String toString() {
        return map1.toString() + "\n" + map2.toString() + "\n" + map3.toString();
    }

}
