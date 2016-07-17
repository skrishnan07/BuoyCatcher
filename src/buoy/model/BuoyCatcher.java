/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buoy.model;

import buoy.common.BuoyException;
import buoy.data.BuoyParserErrorHandler;
import buoy.data.BuoyReader;
import buoy.data.BuoyWriter;
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

    // Reference to a buoyWriter object;
    private BuoyWriter buoyWriter;

    // Referenc to buoyReader object;
    private BuoyReader buoyReader;

    /**
     * Constructor
     *
     * @param reader BuoyReader to read data saved from a previous session
     * @param writer BuoyWriter to save session data
     */
    public BuoyCatcher(BuoyReader reader, BuoyWriter writer)
    {
        buoyReader = reader;
        buoyWriter = writer;

    }

    public void refreshBuoyList(String lat, String lng, int radius)
    {
        initSearchOptions(lat, lng, radius);
        findBuoys();
    }

    public void initSearchOptions(String lat, String lng, int radius)
    {
        latitude = lat;
        longitude = lng;
        searchRadius = radius;

        buoySearchOptions = "?lat=" + latitude + "&lon=" + longitude + "&radius=" + searchRadius;
    }

    public void findBuoys()
    {
        try
        {
            // Clear Old data if any

            allBuoys.clear();

            //Construct the URL of the RSS Feed
            URL url = new URL(NOAA_BUOY_RSS_URL + buoySearchOptions);
            Feed feed = FeedParser.parse(url);

            //testPrint(feed);
            int numItems = feed.getItemCount();
            Buoy buoy = null;
            for (int i = 0; i < numItems; i++)
            {
                buoy = processBuoyItem(feed.getItem(i));
                if ( myBuoys.containsKey(buoy.getStationID()))
                {
                    buoy.setFavorite(true);
                }
            }
            
            // Mark favorite buoys not occuring in the current search as stale
            List <Buoy> existingFavs = getFavoriteBuoys();
            for (Buoy buoy1 : existingFavs)
            {
                if ( !allBuoys.containsKey(buoy1.getStationID()))
                {
                    buoy1.setStale(true);
                }
                else
                {
                    buoy1.setStale(false);
                }
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
        Collections.sort(listBuoys, new Buoy.BuoyComparator());

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
        Collections.sort(listBuoys, new Buoy.BuoyComparator());

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
            buoy.setFavorite(false);
        } else if (bFav)
        {
            myBuoys.put(buoy.getStationID(), buoy);
            buoy.setFavorite(true);
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

    /**
     * Used as debug in early development
     */
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

    /**
     * Used for debugging in early development stage
     *
     * @param feed
     */
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

    /**
     * Handles the case where stations have same name. This happens for SHIPs
     *
     * @param buoy
     * @return
     */
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

    /**
     * Save Settings including user's favorite buoy stations Save Latest Search
     * Location and
     */
    public void saveData()
    {
        if (buoyWriter != null)
        {
            buoyWriter.saveBuoyCatcher(this);
        }
    }

    /**
     * Used to persist the search options from a BuoyCatcher session
     *
     * @return creates a CSV record of the search options to be saved across 
     * user sessions.
     */
    public String toCSVRecord()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(BuoyWriter.BUOY_CATCHER_OPTIONS_RECORD_ID);
        sb.append(BuoyWriter.FS_WRITE);

        sb.append(latitude);
        sb.append(BuoyWriter.FS_WRITE);

        sb.append(longitude);
        sb.append(BuoyWriter.FS_WRITE);

        sb.append(searchRadius);

        return sb.toString();
    }

    /**
     * This method is called from the persistence file reader A buoy marked as
     * favorite in a previous session is loaded back in as a favorite in a new
     * session
     *
     * @param fav This is a favorite read from the persistence storage
     */
    public void addFavorite(Buoy fav)
    {
        Buoy buoy = makeUniqueBuoy(fav);
        myBuoys.put(fav.getStationID(), buoy);
    }

    /**
     * Called when the BuoyCatcher object is initialized.
     * This method invokes methods on the BuoyReader object
     * to get persisted data. 
     * @param errorHandler An handler to handle input parsing errors
     */
    public void restoreSession(BuoyParserErrorHandler errorHandler)
    {
        if ( buoyReader != null )
        {
            boolean fileRead =  buoyReader.readBuoyCatcherCSV(this, errorHandler);
            if ( !fileRead )
            {
                // No previous session file found or read.
                //set the default values for the search paramaters
                
                initSearchOptions(DEFAULT_LATITUDE, DEFAULT_LONGITUDE, DEFAULT_SEARCH_RADIUS_NM);
            }
        }
    }

    /**
     * Getter for Latitude
     *
     * @return the search latitude
     */
    public String getSearchLatitude()
    {
        return latitude;
    }

    /**
     * Getter for Longitude
     *
     * @return the search Longitude
     */
    public String getSearchLongitude()
    {
        return longitude;
    }
    
    /**
     * 
     * @return returns the search radius in nautical miles
     */
      public int getSearchRadius()
    {
        return searchRadius;
    }
}
