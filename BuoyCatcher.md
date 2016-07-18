## Synopsis

**BuoyCatcher** is a desktop Java application, which queries weather information from Buoy stations obtained through the RSS Feed from "http://www.ndbc.noaa.gov/rss/ndbc_obs_search.php". It allows users to search for weather conditions at buoys within a radius of a location specified using *latitude* and *longitude*. Users can add available buoys to their favorite list and save this list across sessions. Different buoys report different amounts of information. The BuoyCatcher application displays all available weather information reported by each buoy through the RSS feed.


## Installation

The application is available for download from [Github](https://github.com/skrishnan07/BuoyCatcher). Download a ZIP archive and  unzip it on your system. Copy it to any folder of your liking. Go to the **dist**folder under **BoyCatcher** in the distribution and enter the following command

##### java -jar "BuoyCatcher.jar"

All the required libraries are included in the distribution. You only need a Java runtime (1.8) environment to run the application.


## Using the Application

###User Interface
The Graphical User Interface or GUI of BuoyCatcher consists of a single dialog window. It consists of the following sub-panels
* Search Panel
* Results Panel with 2 tabs displaying available buoys in one tab and a favorite list of buoys in the other
* A Weather Information panel at the bottom displaying a list of weather conditions available from the Buoy

####Search Panel
The user can enter the latitude and longitude of their desired location with a precision of 1/1000 of a degree. There is also a spinner control available to enter a search radius in nautical miles. The search radius can be set between 50 to 250 nautical miles in steps of 5. When the application starts up, it performs an initial search with the default search options or options remembered from a previous session. Clicking the *Refresh* button performs a new search with the specified parameters.

####The Search Results Tab Panel

The list of Buoys returned in the search results is displayed in the "All Buoys" tab of the Search Results panel. This includes fixed buoys as well as ships that happen to be within the search radius. The results include the *Station ID* of the buoy, the *Name* of the buoy station, the *Location* specified in *latitude* and *longitude* and also the relative position of the buoy in relation to the search location.

#####Sort Order of Buoys

By default the list is sorted by proximity of the buoy to the search location. However if buoys have been selected as favorites, the favorite buoys are listed at the top again in the order of proximity, before any non favorite buoy. The favorite buys are also highlighted in **bold** font

####Station URL and Time of Report

When the user makes a selection of a particular buoy, the URL of the buoy station as well as the time when the report from the buoy was collected is displayed in the panel just below the main results panel. 

####Buoy Weather Conditions

Also, the Weather Conditions detail panel below gets populated with all the available information reported by the buoy. The *Weather Variable* column displays the variable being measured and the *Reading* column displays the measurement including units. Some buoys have more detailed information than others so, the list displayed in the details panel is of different length for different buoys.


####Marking Buoys as Favorites

The application allows the user to select a Buoy as a favorite buoy. This may be because of the proximity of the buoy, or the amount, type or accuracy of the information provided by the Buoy. The user can simply click the "Mark As Favorite" button when a buoy is selected to add it to the list of favorites. There user can then view the details of that Buoy by navigating to the *Favorite Buoys* tab. This typically has a smaller list of buoys to scroll through and offers a convenient way to just focus on the buoys of interest. If for any reason the user wants to clear the favorite status on a buoy, it is easy to do. The user can simply click the "Remove from Favorites" button available in the panel.

####Persistent Data"

The application saves a file called **BuoyCatcher.txt** in the working directory containing information to be persisted between session. This includes the last used search location and radius as well as the list of favorite buoys and their latest information obtained from them. If this file is not found in the current working folder of the application, then default values are used for the search and no favorites are recognized. The default value for the search location is 40 degrees N latitude and 73 degrees W longitude with a search radius of 100 nautical miles

####Missing updates from Favorite Buoys

It is possible that when a search is performed, that a buoy selected as a favorite is not included in the response sent back from the RSS feed. This can happen for instance if the favorite buoy falls outside the most recent search radius or for some reason the buoy did not report its information. In this case, the buoy is still retained as a favorite but its row is highlighted in red in the *Favorite Buoys" tab to indicate that the data from the buoy is stale. If the buoy gets picked up in a subsequent search, the red highlight is removed.


## About the Source Code
The source code for the project is included in GitHub. The source code is in Java. The decision to make this application as a Java desktop application was was mainly made from a point of making the deployment easy as well as the ability to leverage code from my code library. 

There are four packages:
*  buoy.common
*  buoy.gui
*  buoy.model
*  buoy.data

The NetBeans 8.02 IDE was used to develop this application. JavaDocs was generated for the project and is included in the distribution. Here is a brief synopsis of each of the packages

####buoy.common
The common package is just a place holder for common utility classes that are used across other packages. Currently it has only one class called *BuoyException*. However, I have found it a good practice to have a common folder in all my projects

####buoy.gui
The *gui* package contains all the user interface for the application as well as the main function. The *gui* package makes references to other packages but the other packages do not refer back to the classes in the *gui* package. In essence, the GUI layer can be replaced with another GUI environment such as a browser or mobile GUI environment and such an application can still use the underlying model and data layers.

The *gui* package reuses a single *BuoyPanel* class to display both panels used in the results tab. It makes significant use of the Beans Binding library to set up a properties-based data binding between the GUI controls and the model data.

####buoy.model
The *model* package contains the business logic addressing the requirements stated in the user stories. It has two main entity bean type objects namely, *Buoy* and *WeatherCondition* and supporting classes and *BuoyCatcher* and *BuoyParser* to manage the processing and interfacing with other layers. *BuoyCatcher* obtains the feed data using objects from the **feed4j** library. It then parses the data by calling the methods of the *BuoyParser* class and stores the resulting objects in memory. The class makes uses of *TreeMap* collection class, which is a sortable HashMap class to mange the list of buoys and favorites. The *BuoyParser* class performs parsing of the feed data making heavy use of *regexp* pattern matching. The buoy feed data was specific and consistent enough to do the job using *regexp* rather than using some more sophisticated XML/HTML parsing libraries. *BuoyParser* uses a two step technique to parse the results. It parses the more structured **HTMLDescription** data in the feed item to extract the report time, location and weather condition variables. Knowing the specific field names, it then parses the **TextDescription** data in the feed item to quickly get the value data in post processed form without having to parse out HTML escape delimiters.

####buoy.data
The *data* package handles the persistence functions for the *BuoyCatcher* application. Again, after reviewing several options such as **JAXB** and **Simple**, I decided to use some home-grown libraries U+I had developed to serialize and de-serialize data into a simple ".csv" file. for persisting a small amount of information, namely the buoy search information and the list andf data from the favorites. The two main classes *BuoyWriter* and *BuoyReader* were simple to customize for this application. The *BuoyWriter* class is the simpler of the two and it just uses a **toString** like function called **toCSV** invoked on the *model* package  classes to obtain the serializable data. The *BuoyReader* class uses a *HashMap* of parsers set up to parse each type of record in the persistent .csv fie. The reader and writer are matched to each other and standardize the manner in which any data type in the application is serialized.  Again the *data* package can be swapped with other libraries depending on the  needs of the application. 

## Third Party Dependencies
*BuoyCatcher* uses following free-ware libraries available under a  are required and are distributed with the application

1. feed4j from Sauron Software (used for parsing various versions of RSS and Atom feed formats). includes the following four dependencies
   * dom4j-1.6.1  
   * nektohtml.jar 
   * xercesImpl.jar
   * xml-apis.jar

2. beansbinding-1.2.1.jar  This is product of the Swinglabs project and is available through a LGPL license

## Contributors

Other than the use of third-party libraries, this application also has code adapted from various code snippets from Stack Overflow and various forums on the Internet

## License

This software and application are free for anyone to use as a learning tool or tutorial.