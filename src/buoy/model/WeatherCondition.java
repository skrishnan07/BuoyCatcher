/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buoy.model;

import buoy.data.BuoyWriter;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Shankar Krishnan 
 * A specific property describing a weather condition
 * at a buoy
 */
public class WeatherCondition implements Serializable
{

    private String name;
    public static final String PROP_NAME = "name";

    private String value;
    public static final String PROP_VALUE = "value";

    WeatherCondition(String tag, String val)
    {
        name = tag;
        value = val;
    }

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Set the value of value
     *
     * @param value new value of value
     */
    public void setValue(String value)
    {
        String oldValue = this.value;
        this.value = value;
        propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
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

    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener listens for changes to data members declared as properties
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener Listener for property changes previously registered
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
     // Add a comparator
    public static class NameComparator implements Comparator<WeatherCondition>
    {

        @Override
        public int compare(WeatherCondition o1, WeatherCondition o2)
        {
            if (null == o1 || null == o2)
            {
                return 1;
            } else
            {
                return o1.getName().compareTo(o2.getName());
            }
        }
    }
    
    /**
     * Serializes itself into a CSV string
     * Constructs a CSV string as a name value pair
     * @return A CSV version of the Weather Condition's data
     */
    public String toCSVRecord()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(BuoyWriter.BUOY_WEATHER_CONDITION_RECORD_ID);
        sb.append(BuoyWriter.FS_WRITE);

        sb.append(name);
        sb.append(BuoyWriter.FS_WRITE);
        
        sb.append(value);
        
        return sb.toString();
    }
}
