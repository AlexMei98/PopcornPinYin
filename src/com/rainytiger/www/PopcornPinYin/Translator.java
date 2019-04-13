package com.rainytiger.www.PopcornPinYin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Translator {

    private PreProcessor processor;
    private Collector collector;

    Translator(PreProcessor processor, Collector collector) {
        this.processor = processor;
        this.collector = collector;
    }

    void translateFile(String inputPath, String outputPath) {
        System.out.println(inputPath + File.pathSeparator + outputPath);
    }

    String translateOneLine(String pinyin) {
        return "Test 0" + pinyin;
    }

    void translateTestSet(String testSetPath) throws IOException {
        File file = new File(testSetPath);
        if (!file.exists()) return;
        LineNumberReader lnr = new LineNumberReader(new FileReader(file));
        long skipBits = lnr.skip(Long.MAX_VALUE);
        if (skipBits >= Long.MAX_VALUE) throw new IOException("This File Is Too Large!");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        List<String> answer = new ArrayList<>();
        int lineNumber = 0;
        while ((line = br.readLine()) != null) {

        }
    }

}
