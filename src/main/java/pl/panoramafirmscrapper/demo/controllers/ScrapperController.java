package pl.panoramafirmscrapper.demo.controllers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ScrapperController {
    @GetMapping("search_for_businesses/{search_term}/{pages_count}")
    public String getBusinessesList(@PathVariable String search_term, @PathVariable Integer pages_count) {
        if(pages_count<=0)
            return "Wrong pages count!";

        String siteUrl = "https://panoramafirm.pl/" + search_term;
        Map<String, String> businessesUrlMap = new HashMap<>();
        Document doc;
        Elements businessesList, business;
        int numOfBusinesses;
        try{
            mapBusinesses(siteUrl, businessesUrlMap);

            return businessesUrlMap.toString();
        }
        catch(Exception e){
            System.out.println(e);
        }
        return "error";
    }

    private void mapBusinesses(String siteUrl, Map<String, String> businessesUrlMap) throws IOException {
        Document doc;
        Elements businessesList;
        int numOfBusinesses;
        Elements business;
        doc = Jsoup.connect(siteUrl).get();
        businessesList = doc.select("#company-list > li");
        numOfBusinesses = businessesList.size();
        for(int i=0; i<numOfBusinesses; i++){
            business = businessesList.select("li:nth-child("+ (i + 1) +") > div.row.border-bottom.company-top-content.pb-1 > div.col-8.col-sm-10 > h2 > a:first-child");
            businessesUrlMap.put(business.html(),business.attr("href"));
        }
    }
}
