package com.rainytiger.www.PopcornPinYin;

import com.google.gson.Gson;
import com.hankcs.hanlp.seg.Dijkstra.DijkstraSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;

class JsonParser {

    private Segment segment;

    JsonParser() {
        nShortSegment();
        segment = new DijkstraSegment().enableCustomDictionary(false).enablePlaceRecognize(true).enableOrganizationRecognize(true);
    }

    private void nShortSegment() {
    }

    List<Term> parseJson(String string) {
        Gson gson = new Gson();
        NewsJsonBean bean = gson.fromJson(string, NewsJsonBean.class);
        return segment.seg(bean.html + "." + bean.title);
    }

}
