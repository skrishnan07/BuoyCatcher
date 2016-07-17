/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buoy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Shankar Krishnan
 */
public class Buoy implements Serializable
{

    // General Properties describing the Buoy
    // Setters and Getters were generated automatically
    private String stationID;
    public static final String PROP_STATIONID = "stationID";

    private String name;
    public static final String PROP_NAME = "name";

    private String latlong;
    public static final String PROP_LATLONG = "latlong";

    private String relativeLocation;
    public static final String PROP_RELATIVELOCATION = "relativeLocation";

    private String linkURL;
    public static final String PROP_LINKURL = "linkURL";

    private String reportTime;
    public static final String PROP_REPORTTIME = "reportTime";

    // Properties describing weather conditions at buoy at time of report
    // Maintained as a dynamic collection because different buoys report different data
    private TreeMap<String, WeatherCondition> conditions = new TreeMap<>();

    // Other class data
    //relative distance from point of search
    private int distance = 0;

    Buoy(String id, String name)
    {
        this.name = name;
        stationID = id;

    }

    /**
     * Get the value of reportTime
     *
     * @return the value of reportTime
     */
    public String getReportTime()
    {
        return reportTime;
    }

    /**
     * Set the value of reportTime
     *
     * @param reportTime new value of reportTime
     */
    public void setReportTime(String reportTime)
    {
        String oldReportTime = this.reportTime;
        this.reportTime = reportTime;
        propertyChangeSupport.firePropertyChange(PROP_REPORTTIME, oldReportTime, reportTime);
    }

    /**
     * Get the value of linkURL
     *
     * @return the value of linkURL
     */
    public String getLinkURL()
    {
        return linkURL;
    }

    /**
     * Set the value of linkURL
     *
     * @param linkURL new value of linkURL
     */
    public void setLinkURL(String linkURL)
    {
        String oldLinkURL = this.linkURL;
        this.linkURL = linkURL;
        propertyChangeSupport.firePropertyChange(PROP_LINKURL, oldLinkURL, linkURL);
    }

    /**
     * Get the value of relativeLocation
     *
     * @return the value of relativeLocation
     */
    public String getRelativeLocation()
    {
        return relativeLocation;
    }

    /**
     * Set the value of relativeLocation
     *
     * @param relativeLocation new value of relativeLocation
     */
    public void setRelativeLocation(String relativeLocation)
    {
        String oldRelativeLocation = this.relativeLocation;
        this.relativeLocation = relativeLocation;
        propertyChangeSupport.firePropertyChange(PROP_RELATIVELOCATION, oldRelativeLocation, relativeLocation);

        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(relativeLocation);
        if (m.find())
        {
            try
            {
                distance = Integer.parseInt(m.group(0));
            } catch (Exception ex)
            {
                distance = 0;
            }
        }
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name)
    {
        String oldName = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, name);
    }

    /**
     * Get the value of latlong
     *
     * @return the value of latlong
     */
    public String getLatlong()
    {
        return latlong;
    }

    /**
     * Set the value of latlong
     *
     * @param latlong new value of latlong
     */
    public void setLatlong(String latlong)
    {
        String oldLatlong = this.latlong;
        this.latlong = latlong;
        propertyChangeSupport.firePropertyChange(PROP_LATLONG, oldLatlong, latlong);
    }

    /**
     * Get the value of stationID
     *
     * @return the value of stationID
     */
    public String getStationID()
    {
        return stationID;
    }

    /**
     * Set the value of stationID
     *
     * @param stationID new value of stationID
     */
    public void setStationID(String stationID)
    {
        String oldStationID = this.stationID;
        this.stationID = stationID;
        propertyChangeSupport.firePropertyChange(PROP_STATIONID, oldStationID, stationID);
    }

    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public String toString()
    {
        return name;
    }

    public String toReportString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("Station ID: <");
        sb.append(stationID);

        sb.append(">, Name: <");
        sb.append(name);

        sb.append(">\n");
        sb.append("Location: <");
        sb.append(latlong);

        sb.append(">, Relative to Search Location: <");
        sb.append(relativeLocation);

        sb.append(">\n");

        sb.append("Report Time: <");
        sb.append(reportTime);
        sb.append(">\n\n");

        Collection<WeatherCondition> data = conditions.values();

        for (WeatherCondition wc : data)
        {
            sb.append(wc.getName());
            sb.append(": <");
            sb.append(wc.getValue());
            sb.append(">\n");
        }

        return sb.toString();
    }

    // Add a name value pair of Weather Condition Variable
    void addWeatherCondition(String tag, String value)
    {
        WeatherCondition wc = new WeatherCondition(tag, value);
        conditions.put(tag, wc);
    }

    public List<WeatherCondition> getConditions()
    {
        Collection<WeatherCondition> col = null;
        col = conditions.values();

        ArrayList<WeatherCondition> listConditions = new ArrayList<>(col);
        Collections.sort(listConditions, new WeatherCondition.NameComparator());

        return listConditions;
    }

    /**
     * An ID Comparator Inner Class to sort Buoys Alphabetically by Station IDs
     */
    public static class IDComparator implements Comparator<Buoy>
    {

        @Override
        public int compare(Buoy o1, Buoy o2)
        {
            if (null == o1 || null == o2)
            {
                return 1;
            } else
            {
                return o1.getStationID().compareTo(o2.getStationID());
            }
        }
    }

    // Add a comparator
    public static class DistanceComparator implements Comparator<Buoy>
    {

        @Override
        public int compare(Buoy o1, Buoy o2)
        {
            if (null == o1 || null == o2)
            {
                return 1;
            } else
            {
                int result = Integer.compare(o1.distance,  o2.distance);
                if ( result == 0 )
                {
                    return o1.relativeLocation.compareTo(o2.relativeLocation);
                }
                else
                {
                    return result;
                }
            }
        }
    }


public int getRelativeDistance()
    {
        return distance;
    }
}
