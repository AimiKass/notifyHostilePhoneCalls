package com.example.notifyhostilephonecalls.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class ExtractRating
{
    String rating;


    public void run(String number)
    {

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final StringBuilder stringBuilder = new StringBuilder();

                try
                {
                    String url = "https://www.white-pages.gr/arithmos/" + number + "/";//your website url
                    Document doc = Jsoup.connect(url).get();
                    rating = doc.getElementsByClass("td78").select("div#progress-bar-inner").text();
                } catch (Exception e)
                {
                    stringBuilder.append("Error : ").append(e.getMessage()).append("\n");
                    rating = "101";
                }


            }
        });

        thread.start();
        try
        {
            thread.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }


    }


    public String getRating(String number)
    {
        run(number);
        return rating;
    }

}


