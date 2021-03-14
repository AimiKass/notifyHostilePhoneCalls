package com.example.notifyhostilephonecalls.retrieveData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ExtractFromSite
{
    public String getPhoneNumberRating(String number)
    {
        final StringBuilder stringBuilder = new StringBuilder();
        String rating = null;

        try {

            String url = "https://www.white-pages.gr/arithmos/" + number + "/";//your website url
            Document doc = Jsoup.connect(url).get();
            rating = doc.getElementsByClass("td78").select("div#progress-bar-inner").text();

        }catch (Exception e)
        {
            stringBuilder.append("Error : ").append(e.getMessage()).append("\n");
        }

        return rating;
    }
}
