package com.rainytiger.www.PopcornPinYin;

import java.io.*;

class Translator {

    private PreProcessor processor;
    private Collector collector;

    Translator(PreProcessor processor, Collector collector) {
        this.processor = processor;
        this.collector = collector;
    }

    void translateFile(String inputPath, String outputPath, boolean print) {
        System.out.println(inputPath + File.pathSeparator + outputPath);
    }

    String translateOneLine(String pinyin) {
        return "";
    }

    void translateTestSet(String testSetPath, boolean print) throws IOException {
        File file = new File(testSetPath);
        if (!file.exists()) return;
        LineNumberReader lnr = new LineNumberReader(new FileReader(file));
        long skipBits = lnr.skip(Long.MAX_VALUE);
        if (skipBits >= Long.MAX_VALUE) throw new IOException("This File Is Too Large!");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line, temp;
        int lineTotal = 0;
        int lineRight = 0;
        int hanziTotal = 0;
        int hanziRight = 0;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            temp = translateOneLine(line);
            line = br.readLine().trim();

            if (line.equals(temp)) {
                lineRight++;
                hanziRight += line.length();
            } else {
                hanziRight += Util.stringSameNumber(line, temp);
            }
            hanziTotal += line.length();
            lineTotal++;
        }
        if (!print) return;
        System.out.println("Translate Result:");
        System.out.println();
        System.out.println("Total Line Number: " + lineTotal + " | Right Line Number: " + lineRight);
        System.out.println("***Line rate: " + ((double) lineRight) / ((double) lineTotal) * 100 + "%");
        System.out.println();
        System.out.println("Total Hanzi Number: " + hanziTotal + " | Right Hanzi Number: " + hanziRight);
        System.out.println("***Hanzi rate: " + ((double) hanziRight) / ((double) hanziTotal) * 100 + "%");
        System.out.println();
    }

}
