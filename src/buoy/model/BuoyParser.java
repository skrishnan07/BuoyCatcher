/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buoy.model;

import buoy.common.BuoyException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Shankar Krishnan
 * Contains utilities to parse the buoy data obtained
 * from the RSS Feed
 */
public class BuoyParser
{

    // String constants used in parsing
    private static final String LOCATION_PREFIX = "Location:";

    // REGEXP for matching HTML tag expected in the buoy data
    private static final Pattern HTML_DESCR_TAG_REGEX = Pattern.compile("<strong>(.+?)</strong>");

    public boolean parseBuoyInfo(Buoy buoy, String html, String text)
    {
        return true;
    }

    /**
     * Adds a Buoy using data obtained from the RSS Feed for one feed item
     *
     * @param title the title attribute of the buoy item
     * @param link URL link of the buoy
     * @param textDescription weather data in text form
     * @param htmlDescription weather data in HTMl form
     * @return returns a populated buoy object to be added to the main list
     * @throws BuoyException if a station ID cannot be determined or if
     * there is incomplete information in constructing the weather conditions
     */
    public Buoy addBuoy(String title, String link, String textDescription, String htmlDescription) throws BuoyException
    {
        Buoy buoy = null;

        // If title is null then cannot process much. Since we have no
        // unioque way to identify the buoy
        if (title == null)
        {
            throw new BuoyException("Buoy record in feed with null title element");

        } else
        {

            // Extract station ID, Full Name and URL link
 
            buoy = createBuoy(title);
            buoy.setLinkURL(link);

            // Extract Data Fields by parsing the HTML Description
            List<String> strongTags = extractDataFields(htmlDescription);

            // Use the extracted placeholders to set the reporting time
            // and Weather
            if (strongTags != null && strongTags.size() > 1)
            {
                String reportTime = strongTags.get(0);
                buoy.setReportTime(reportTime);
                strongTags.remove(0);

                //System.out.println("Buoy : <" + buoy.getStationID() + "> Reporting @ <" + reportTime + ">");
                int i = 0;
                String leftTag = "";
                String rightTag = "";
                String value = "";
                for (String tag : strongTags)
                {

                    leftTag = tag;
                    if (i < strongTags.size() - 1)
                    {
                        rightTag = strongTags.get(i + 1);
                    } else
                    {
                        rightTag = "";
                    }
                    i++;
                    value = extractDataValue(textDescription, leftTag, rightTag);

                    if (tag.startsWith(LOCATION_PREFIX))
                    {
                        // Treat this differently
                        extractLatLongAndRelativeLocation(buoy, value);

                    } else
                    {
                        // Add the waether condition value to the list of data
                        buoy.addWeatherCondition(tag.replace(":", ""), value);
                    }
                }
            } else
            {
                throw new BuoyException("Buoy record in feed with incomplete Weather Conditions");
            }
        }

        return buoy;
    }

  
    
    
    public static final String STATION_PREFIX = "Station";
    
    public  Buoy createBuoy(String title)
    {
        String stationID = "";
        String name = "";
        
        String regexp = "^(.+?)-";
        Pattern pat = Pattern.compile(regexp);
        Matcher match = pat.matcher(title);

        if (match.find())
        {
            stationID =  match.group();
            
            name = title.substring(title.
                    indexOf(stationID) + stationID.length(), title.length()).trim();
            stationID =  stationID.replace("-", "").trim();
            
            // Take out the literal "Station" from the Station ID if present;
            
            int firstIndex = stationID.indexOf(STATION_PREFIX);
            if ( firstIndex >= 0)
            {
                stationID = stationID.substring(firstIndex+ STATION_PREFIX.length(), stationID.length()).trim();
            }
            
        } else
        {
            stationID = title;
            name = title;
        }
        
        Buoy buoy = new Buoy(stationID, name);
        return buoy;
    }

    /**
     *
     * @param htmlDescription the input text containing HTML description of buoy
     * The parsing is down in two steps. It is easier to parse out the text
     * within the 'strong' tags as buoy information field names. A separate step
     * parses the text information extract the values for the data fields
     * @return returns a list of place holder tags for weather conditions
     */
    private List<String> extractDataFields(String htmlDescription)
    {
        List<String> tagValues = new ArrayList<String>();
        Matcher matcher = HTML_DESCR_TAG_REGEX.matcher(htmlDescription);

        while (matcher.find())
        {
            tagValues.add(matcher.group(1));
        }
        return tagValues;
    }

    /**
     *
     * @param inputText
     * @param leftTag
     * @param rightTag
     * @return
     */
    private String extractDataValue(String inputText, String leftTag, String rightTag)
    {
        String value = "";

        Pattern pat = null;
        Matcher matcher = null;

        if (rightTag.isEmpty())
        {

            value = inputText.substring(inputText.
                    lastIndexOf(leftTag) + leftTag.length(), inputText.length()).trim();

        } else
        {
            pat = Pattern.compile(leftTag + "(.+?)" + rightTag);
            matcher = pat.matcher(inputText);
            if (matcher.find())
            {
                value = matcher.group(1).trim();
            }

        }
        return value;
    }

    /**
     * Uses a simple parsing rule to separate out the location information
     *
     * @param buoy the buoy to add location information to
     * @param value the string containing lat, long and relative location
     */
    private void extractLatLongAndRelativeLocation(Buoy buoy, String value)
    {
        String regexp = "^(.+?)or";
        Pattern pat = Pattern.compile(regexp);
        Matcher match = pat.matcher(value);

        String latLong = "";
        String relativePos = "";

        if (match.find())
        {
            latLong = match.group(0);
            relativePos = value.substring(value.
                    lastIndexOf(latLong) + latLong.length(), value.length()).trim(); 
            
            latLong = latLong.replace("or", "").trim();


        } else
        {
            latLong = "";
            relativePos = value;
        }

        buoy.setLatlong(latLong);
        buoy.setRelativeLocation(relativePos);
    }
}
