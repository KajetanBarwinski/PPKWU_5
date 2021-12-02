package pl.panoramafirmscrapper.demo.controllers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;

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
        try{
            mapBusinesses(siteUrl, businessesUrlMap);

            if(pages_count>1){
                for(int i=2;i<=pages_count;i++){
                    siteUrl = "https://panoramafirm.pl/" + search_term + "/firmy,"+i+".html";
                    mapBusinesses(siteUrl, businessesUrlMap);
                }
            }
            System.out.println(businessesUrlMap.size());
            return businessesUrlMap.toString();
        }
        catch(Exception e){
            System.out.println(e);
        }
        return "Unknown error has occured.";
    }

    @GetMapping("fetch_business_info")
    @ResponseBody
    public String fetchBusinessInfo(@RequestParam("url") String business_url){
        Map<String, String> businessesInfoMap = new HashMap<>();
        Document doc;
        Elements addressInfo;
        try{
            doc = Jsoup.connect(business_url).get();
            addressInfo = doc.select("#to-pdf > div.container-fluid.px-0.pb-2.bg-white.border-bottom > div > " +
                    "div.row.pt-2 > div.col-lg-8.col-sm-12 > div > div > div.col-12.col-sm-6.company-info > div.address.pb-2 > " +
                    "div > div.col-10.pl-1 > strong");
            businessesInfoMap.put("address",addressInfo.html());
            return businessesInfoMap.toString();
        }
        catch(Exception e){
            System.out.println(e);
        }
        return "Unknown error has occured.";
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
            business = businessesList.select("li:nth-child("+ (i + 1) +") > div.row.border-bottom.company-top-content.pb-1 > div > h2 > a:first-child");
            businessesUrlMap.put(business.html(),business.attr("href"));
        }
    }
}
