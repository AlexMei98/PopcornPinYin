package com.rainytiger.www.PopcornPinYin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Translator {

    private PreProcessor processor;
    private Collector collector;
    private int hanziTotal;
    private double lambda;

    Translator(PreProcessor processor, Collector collector, double lambda) {
        this.processor = processor;
        this.collector = collector;
        this.lambda = lambda;
        this.hanziTotal = collector.hanziTotalNumber();
        System.out.println(hanziTotal);
    }

    void translateFile(String inputPath, String outputPath, boolean print) throws IOException {
        File input = new File(inputPath);
        File output = new File(outputPath);
        if (!input.exists()) {
            throw new FileNotFoundException("File \"" + inputPath + "\" Not Found!");
        } else if (!output.exists()) {
            if (!output.createNewFile()) throw new IOException("Create File \"" + outputPath + "\" Failed!");
        } else {
            if (!output.delete()) throw new IOException("Delete File \"" + outputPath + "\" Failed!");
            if (!output.createNewFile()) throw new IOException("Create File \"" + outputPath + "\" Failed!");
        }
        BufferedReader br = new BufferedReader(new FileReader(input));
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        String line, ret;
        while ((line = br.readLine()) != null) {
            ret = translateOneLine(line);
            bw.write(ret + "\n");
            if (print) System.out.println(ret);
        }
        bw.close();
        br.close();
    }

    String translateOneLine(String pinyin) {
        String lowerCase = pinyin.toLowerCase();
        String[] array = lowerCase.split(" ");
        List<String> pys = new ArrayList<>(array.length);
        for (String str : array) {
            if (str.equals("qv")) {
                pys.add("qu");
            } else if (str.equals("xv")) {
                pys.add("xu");
            } else {
                pys.add(str);
            }
        }
        Graph graph = new Graph(pys);
        return graph.parse();
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

    class Graph {

        private List<List<Node>> lists = new ArrayList<>();

        Graph(List<String> pys) {
            System.out.println(pys.size());
            for (String pinyin : pys) {
                List<Integer> hs = processor.hanziList(pinyin.trim());
                List<Node> list = new ArrayList<>(hs.size());
                for (int hanzi : hs) {
                    list.add(new Node(hanzi));
                }
                lists.add(list);
            }
        }

        String parse() {
            for (int i = 0, size = lists.size(); i < size; i++) {
                viterbi(i, lambda, 1 - lambda);
            }
            StringBuilder sb = new StringBuilder();
            double P = -Double.MAX_VALUE;
            Node max = null;
            List<Node> last = lists.get(lists.size() - 1);
            for (Node node : last) {
                if (P < node.prob) {
                    P = node.prob;
                    max = node;
                }
            }
            if (max == null) return "";
            while (max.prev != null) {
                sb.append(max.hanzi);
                max = max.prev;
            }
            return sb.reverse().toString();
        }

        void viterbi(int level, double lambda, double alpha) {
            if (level == 0) {
                for (Node node : lists.get(level)) {
                    if (node.isNull()) continue;
                    node.prob = Math.log(((double) node.map1) / ((double) hanziTotal));
                }
                return;
            }
            for (Node node : lists.get(level)) {
                if (node.isNull()) continue;
                for (Node prev : lists.get(level - 1)) {
                    if (prev.isNull()) continue;
                    double P;
                    if (prev.map1 == 0) {
                        P = -Double.MAX_VALUE;
                    } else {
                        P = Math.log(((double) prev.map2.getOrDefault(node.hanziIndex, 0)) * lambda + alpha * ((double) prev.map1));
                    }
                    if (P > node.prob) {
                        node.prob = P;
                        node.prev = prev;
                    }
                }
            }
        }

        class Node {

            char hanzi;
            int hanziIndex;
            int map1;
            Map<Integer, Integer> map2;
            Map<Integer, Map<Integer, Integer>> map3;

            double prob = -Double.MAX_VALUE;
            Node prev = null;

            Node(int hanziIndex) {
                this.hanziIndex = hanziIndex;
                init();
            }

            void init() {
                hanzi = processor.parseHanzi(hanziIndex);
                map1 = collector.getFromMap1(hanziIndex);
                map2 = collector.getFromMap2(hanziIndex);
                map3 = collector.getFromMap3(hanziIndex);
            }

            boolean isNull() {
                return hanziIndex < 0 || hanziIndex >= 6763 || hanzi == ' ' || map1 == -1 || map2 == null || map3 == null;
            }

        }

    }

}
