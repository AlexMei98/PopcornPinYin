package com.rainytiger.www.PopcornPinYin;

import java.io.File;
import java.io.IOException;

public class PopcornPinYin {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String preCorpusPath = "corpus";
        String cachePath = "cache";
        String corpusPath = "sina_news";

        PreProcessor processor = new PreProcessor(preCorpusPath, cachePath);
        processor.init();
        Collector collector = new Collector(preCorpusPath + File.separator + corpusPath, cachePath, processor);
        collector.init();
        System.out.println(args.length);
    }

}
