package com.rainytiger.www.PopcornPinYin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PopcornPinYin {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length == 0) {
            String usage =
                    "  Usage: java -jar PopcornPinYin.jar \"PINYIN\"\n" +
                    "         Use PINYIN as input (with \"\" surrounding), print the output to the terminal.\n\n" +
                    "     Or: java -jar PopcornPinYin.jar INPUT OUTPUT PRINT_FLAG\n" +
                    "         Use INPUT file which one line as a input pinyin, save the output to OUTPUT file.\n\n" +
                    "     Or: java -jar PopcornPinYin.jar TESTSET PRINT_FLAG\n" +
                    "         Use PINYIN as input, print the output to the terminal.\n\n" +
                    "         PRINT_FLAG: \"true\" or others, means print the results to the terminal or not.\n";
            System.out.println(usage);
            return;
        }

        String preCorpusPath = "corpus";
        String cachePath = "cache";
        String corpusPath = "sina_news";

        PreProcessor processor = new PreProcessor(preCorpusPath, cachePath);
        processor.init();
        Collector collector = new Collector(preCorpusPath + File.separator + corpusPath, cachePath, processor);
        collector.init();
        List<Translator> translators = new ArrayList<>(1001);
        Translator translator = new Translator(processor, collector, 0.01);

        if (args.length == 1) {
            System.out.println(translator.translateOneLine(args[0]));
        } else if (args.length == 2) {
            translator.translateTestSet(args[0], Boolean.valueOf(args[1]));
        } else if (args.length == 3) {
            translator.translateFile(args[0], args[1], Boolean.valueOf(args[2]));
        }
    }

}
