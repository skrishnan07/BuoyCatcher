/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buoy;

import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.bean.Feed;
import it.sauronsoftware.feed4j.bean.FeedHeader;
import it.sauronsoftware.feed4j.bean.FeedItem;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shankar Krishnan
 */
public class BuoyCatcher
{

    public static final String NOAA_BUOY_RSS_URL = "http://www.ndbc.noaa.gov/rss/ndbc_obs_search.php";
    public static final String DEFAULT_LATITUDE = "40.000N";
    public static final String DEFAULT_LONGITUDE = "73.000W";
    public static final int DEFAULT_SEARCH_RADIUS_NM = 100;

    private String latitude = DEFAULT_LATITUDE;
    private String longitude = DEFAULT_LONGITUDE;
    private int searchRadius = DEFAULT_SEARCH_RADIUS_NM;

    private String buoySearchOptions;

    //Parser for parsing buoy data
    public BuoyParser buoyParser = new BuoyParser();

    // List of all buoys found
    private TreeMap<String, Buoy> allBuoys = new TreeMap<>();

    // List of favorite buoys
    private TreeMap<String, Buoy> myBuoys = new TreeMap<>();

    public BuoyCatcher(String lat, String lng, int radius)
    {
        latitude = lat;
        longitude = lng;
        searchRadius = radius;

        buoySearchOptions = "?lat=" + latitude + "&lon=" + longitude + "&radius=" + searchRadius;
    }

    public void resetSearchOptions(String lat, String lng, int radius)
    {
        latitude = lat;
        longitude = lng;
        searchRadius = radius;

        buoySearchOptions = "?lat=" + latitude + "&lon=" + longitude + "&radius=" + searchRadius;
        findBuoys();
    }

    public void findBuoys()
    {
        try
        {
            // Clear Old data if any
            
            allBuoys.clear();
            myBuoys.clear();
            
            //Construct the URL of the RSS Feed

            URL url = new URL(NOAA_BUOY_RSS_URL + buoySearchOptions);
            Feed feed = FeedParser.parse(url);

            //testPrint(feed);
            int numItems = feed.getItemCount();
            Buoy buoy = null;
            for (int i = 0; i < numItems; i++)
            {
                buoy = processBuoyItem(feed.getItem(i));
            }
        } catch (Exception ex)
        {
            Logger.getLogger(BuoyCatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return returns a list of all Buoys sorted alphabetically by Station ID
     */
    public List<Buoy> getAllBuoys()
    {
        Collection<Buoy> col = null;
        col = allBuoys.values();

        ArrayList<Buoy> listBuoys = new ArrayList<>(col);
        Collections.sort(listBuoys, new Buoy.DistanceComparator());

        return listBuoys;
    }

    /**
     *
     * @return returns a list of favorite Buoys sorted alphabetically by Station
     * ID
     */
    public List<Buoy> getFavoriteBuoys()
    {
        Collection<Buoy> col = null;
        col = myBuoys.values();

        ArrayList<Buoy> listBuoys = new ArrayList<>(col);
        Collections.sort(listBuoys, new Buoy.DistanceComparator());

        return listBuoys;
    }

    /**
     * Effectively toggles the favorite state of the buoy The bFav flag makes
     * the state setting explicit
     *
     * @param buoy the buoy that needs to be affected
     * @param bFav TRUE: Make it a Favorite. FALSE: Remove from Favorite
     */
    public void setFavoriteStatus(Buoy buoy, boolean bFav)
    {
        if (!bFav && myBuoys.containsValue(buoy))
        {
            myBuoys.remove(buoy.getStationID());
        } else if (bFav)
        {
            myBuoys.put(buoy.getStationID(), buoy);
        }

    }

    /**
     * @param item Item found in the RSS Feed while parsing Creates a new Buoy
     * and adds it to the list of buoys
     */
    private Buoy processBuoyItem(FeedItem item)
    {
        Buoy buoy = null;
        try
        {
            String title = item.getTitle();
            URL itemUrl = item.getLink();
            String textDescription = item.getDescriptionAsText();
            String htmlDescription = item.getDescriptionAsHTML();

            String link = "";

            if (itemUrl != null)
            {
                link = itemUrl.toString();
            }

            buoy = buoyParser.addBuoy(title, link, textDescription, htmlDescription);
            buoy = makeUniqueBuoy(buoy);
            if (buoy != null)
            {

                allBuoys.put(buoy.getStationID(), buoy);
            }
        } catch (BuoyException ex)
        {
            Logger.getLogger(BuoyCatcher.class.getName()).log(Level.WARNING, ex.getMessage(), buoyParser);
        } catch (Exception ex)
        {
            Logger.getLogger(BuoyCatcher.class.getName()).log(Level.WARNING, ex.getMessage(), buoyParser);
        }

        return buoy;
    }

    private void printBuoys()
    {
        List<Buoy> listBuoys = getAllBuoys();
        for (Buoy buoy : listBuoys)
        {
            System.out.println("****************************");
            System.out.println(buoy.toReportString());
            System.out.println("****************************");
        }

    }

    private void testPrint(Feed feed)
    {

        //Based on example from the feed4j library distribution
        try
        {
            System.out.println("** HEADER **");
            FeedHeader header = feed.getHeader();
            System.out.println("Title: " + header.getTitle());
            System.out.println("Link: " + header.getLink());
            System.out.println("Description: " + header.getDescription());
            System.out.println("Language: " + header.getLanguage());
            System.out.println("PubDate: " + header.getPubDate());

            System.out.println("** ITEMS **");
            int items = feed.getItemCount();
            for (int i = 0; i < items; i++)
            {
                FeedItem item = feed.getItem(i);
                System.out.println("Title: " + item.getTitle());
                System.out.println("Link: " + item.getLink());
                System.out.println("Plain text description: " + item.getDescriptionAsText());
                System.out.println("HTML description: " + item.getDescriptionAsHTML());
                System.out.println("PubDate: " + item.getPubDate());

            }
        } catch (Exception ex)
        {
            Logger.getLogger(BuoyCatcher.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Buoy makeUniqueBuoy(Buoy buoy)
    {
        if (buoy == null || buoy.getStationID() == null)
        {
            return buoy;
        }
        String id = buoy.getStationID();
        if (!allBuoys.containsKey(id))
        {
            return buoy;
        } else
        {
            for (int i = 2; i < 100; i++)
            {
                id = buoy.getStationID() + "(" + i + ")";
                if (!allBuoys.containsKey(id))
                {
                    buoy.setStationID(id);
                    return buoy;
                }
            }
        }
        return null;
    }

    public void saveData()
    {
        
    }
}
