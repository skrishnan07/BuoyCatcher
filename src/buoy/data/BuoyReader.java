/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buoy.data;

import buoy.common.BuoyException;
import buoy.model.Buoy;
import buoy.model.BuoyCatcher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

/**
 *
 * @author Shankar Krishnan
 */
public class BuoyReader
{

    // Minimum Argument Count for records in the Buoy Persistent store
    private static final int MIN_BCOPTS_ARGS = 3;
    private static final int MIN_BCFAV_ARGS = 5;
    private static final int MIN_BCWC_ARGS = 2;

    // Stores a line of raw input in case of error
    private String rawInputLine = "";
    private int lineNum = 0;

    // Parsers for parsing buoy lines
    private BuoyParserErrorHandler buoyErrorHandler = null;
    private TreeMap<String, BuoyLineParser> mapParsers = new TreeMap<>();
    
    // Remembers the buoy reference when parsing associated weather condition
    //records
    private Buoy currentFavorite = null;

    /**
     * Constructor for BuoyReader
     */
    public BuoyReader()
    {
        // Set up the parsers for each record type
        mapParsers.put(BuoyWriter.BUOY_CATCHER_OPTIONS_RECORD_ID, new BCOptionsLineParser());
        mapParsers.put(BuoyWriter.BUOY_FAVORITE_RECORD_ID, new BCFavoriteLineParser());
        mapParsers.put(BuoyWriter.BUOY_WEATHER_CONDITION_RECORD_ID, new BCWeaConLineParser());
    }

    /**
     * Open File
     *
     * @param fileName String
     * @throws Exception
     * @return BufferedWriter
     */
    private BufferedReader openFile(String fullPath)
    {
        BufferedReader br = null;
        File file = new File(fullPath);

        if (file.exists() && file.canRead())
        {

            try
            {
                br = new BufferedReader(new FileReader(fullPath));

            } catch (Exception ex)
            {

            }
        }
        if (br == null)
        {
            //buoyErrorHandler.handleParsingError(new BuoyException("Error occurred when opening Buoy Catcher file:" + fullPath));
        }
        return br;
    }

    /**
     * Close File
     *
     * @param bw BufferedWriter
     * @throws Exception
     */
    private void closeFile(BufferedReader br) throws IOException
    {

        br.close();

    }

    /**
     * Reads a line of input from the archived file and makes some string
     * conversions to account for reserved delimiters in data strings
     *
     * @param br BufferReader object
     * @return returns a line of text read from the file
     * @throws IOException
     */
    private String readLine(BufferedReader br) throws IOException
    {
        String line = null;

        String input = br.readLine();
        if (input != null)
        {
            rawInputLine = input;
            line = input.replaceAll(BuoyWriter.FS_READ, BuoyWriter.FS_WRITE);

            line = line.replaceAll(BuoyWriter.COMMA_REPLACEMENT, BuoyWriter.FS_READ);
            line = line.replaceAll(BuoyWriter.NEWLINE_REPLACEMENT, BuoyWriter.NEWLINE);

//            line = line.replaceAll("\\<COMMA\\>", ",");
//            line = line.replaceAll("\\<NL\\>", "\n");
        }
        return line;

    }

    public boolean readBuoyCatcherCSV(BuoyCatcher bcatcher, BuoyParserErrorHandler buoyErrorHandler)
    {

        boolean fileRead = false;
        boolean bEndOfFile = false;
        String line = null;
        BuoyLineParser recordParser = null;
        
        String fileName = BuoyWriter.BUOY_CATCHER_FILE;

        lineNum = 0;

        if (bcatcher == null)
        {
            return fileRead;
        }

        try
        {
            if (fileName != null)
            {

                BufferedReader br = openFile(fileName);
                if ( br == null)
                {
                    return fileRead;
                }

                String tokens[] = null;

                while (!bEndOfFile)
                {
                    try
                    {
                        lineNum++;
                        line = readLine(br);

                        if (line == null)
                        {
                            bEndOfFile = true;
                        } else
                        {
                            if (line.isEmpty())
                            {
                                continue;
                            }

                            tokens = line.split("\\" + BuoyWriter.FS_WRITE);
                            if (tokens != null && tokens.length > 0)
                            {
                                fileRead = true;
                                recordParser = mapParsers.get(tokens[0]);
                                if (recordParser != null)
                                {
                                    recordParser.parseLine(tokens, bcatcher);
                                }
                            }
                        }
                    } catch (Exception ex)
                    {
                        String msg = "Exception while parsing [" + lineNum + "] <" + rawInputLine + ">: " + ex.getMessage();
                        buoyErrorHandler.handleParsingError(new BuoyException(msg));

                    }
                }
                closeFile(br);

            }
        } catch (Exception ex)
        {

            buoyErrorHandler.handleParsingError(new BuoyException(ex.getMessage()));
        }
        
        return fileRead;

    }

// Common Interface for parser
    interface BuoyLineParser
    {

        public void parseLine(String tokens[], BuoyCatcher bcatcher) throws BuoyException;
    }

    /**
     * Common base class for most of the line parsers used in parsing buoy
     * persistence data
     */
    class BCCommonLineParser
    {

        // Common fields useful in parsing CSV lines
        protected int minNumArgs = 0;
        protected int nextToken = 0;
        protected int numTokens = 0;

        // Construcor used to set minimum number of arguments
        public BCCommonLineParser(int minargs)
        {
            minNumArgs = minargs;

        }

        public void checkSufficientArguments(String tokens[]) throws BuoyException
        {
            nextToken = 0;
            numTokens = tokens.length;
            if (numTokens <= minNumArgs)
            {
                throw new BuoyException("Insufficient number of arguments in record");
            }
        }
    }

    /**
     * Parser class for reading BC Options Line
     */
    class BCOptionsLineParser extends BCCommonLineParser implements BuoyLineParser
    {

        // Construcor used to set minimum number of arguments
        public BCOptionsLineParser()
        {
            super(MIN_BCOPTS_ARGS);

        }

        @Override
        public void parseLine(String[] tokens, BuoyCatcher bcatcher) throws BuoyException
        {
            // Check if we have sufficient arguments. 
            // Otherwise, en exception is raised
            checkSufficientArguments(tokens);

            nextToken++;
            String lat = tokens[nextToken];

            nextToken++;
            String lng = tokens[nextToken];

            int radius = 1;

            try
            {
                nextToken++;
                radius = Integer.parseInt(tokens[nextToken]);
            } catch (Exception ex)
            {
                throw new BuoyException("Error parsing BuoyCatcher Options Record");
            }

            bcatcher.initSearchOptions(lat, lng, radius);
        }

    }

    /**
     * Parser class for reading BC Options Line
     */
    class BCFavoriteLineParser extends BCCommonLineParser implements BuoyLineParser
    {

        // Construcor used to set minimum number of arguments
        public BCFavoriteLineParser()
        {
            super(MIN_BCFAV_ARGS);

        }

        @Override
        public void parseLine(String[] tokens, BuoyCatcher bcatcher) throws BuoyException
        {
             // Check if we have sufficient arguments. 
            // Otherwise, en exception is raised
            checkSufficientArguments(tokens);

            nextToken++;
            String station = tokens[nextToken];

            nextToken++;
            String name = tokens[nextToken];
            
            nextToken++;
            String location = tokens[nextToken];
            
            nextToken++;
            String relLocation = tokens[nextToken];

            nextToken++;
            String reportTime = tokens[nextToken];
            
            Buoy fav = new Buoy(station, name);
            fav.setFavorite(true);
            fav.setLatlong(location);
            fav.setRelativeLocation(relLocation);
            fav.setReportTime(reportTime);
            
            bcatcher.addFavorite(fav);
            
            currentFavorite = fav;
        }
    }

    /**
     * Parser class for reading BC Options Line
     */
    class BCWeaConLineParser extends BCCommonLineParser implements BuoyLineParser
    {

        // Construcor used to set minimum number of arguments
        public BCWeaConLineParser()
        {
            super(MIN_BCWC_ARGS);

        }

        @Override
        public void parseLine(String[] tokens, BuoyCatcher bcatcher) throws BuoyException
        {
            // Check if we have sufficient arguments. 
            // Otherwise, en exception is raised
            checkSufficientArguments(tokens);
           
            nextToken++;
            String name = tokens[nextToken];

            nextToken++;
            String value = tokens[nextToken];
            
            if ( currentFavorite != null )
            {
                currentFavorite.addWeatherCondition(name, value);
            }
            else
            {
                throw new BuoyException("Error when parsing Weather Condition. No Favorite Buoy reference found");
            }
        }
    }

}
