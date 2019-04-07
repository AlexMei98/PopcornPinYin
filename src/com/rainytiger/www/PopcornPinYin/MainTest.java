package com.rainytiger.www.PopcornPinYin;

import java.io.IOException;

public class MainTest {

    public static void main(String[] args) throws IOException {
        CorpusProcessor processor = new CorpusProcessor("corpus/sina_news");
        processor.initMap();
        System.out.println(processor);
    }
}
