package com.rainytiger.www.PopcornPinYin;

import java.io.IOException;

public class MainTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String corpusPath = "corpus";
        String cachePath = "cache";

        CorpusProcessor processor = new CorpusProcessor(corpusPath, cachePath);

        if (processor.cacheExist()) {
            processor.readAll();
        } else {
            processor.deleteCache();
            processor.initMap();
            processor.writeAll();
        }

        processor.readAll();
        System.out.println(processor);

    }
}
