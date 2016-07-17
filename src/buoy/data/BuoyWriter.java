/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buoy.data;

import buoy.model.Buoy;
import buoy.model.BuoyCatcher;
import buoy.model.WeatherCondition;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shankar.Krishnan
 */
public class BuoyWriter
{

    // Some useful symbolic constants
    // for managing persistence
    public static final String FS_WRITE = "@";
    public static final String FS_READ = ",";
    public static final String COMMA = ",";
    public static final String COMMA_REPLACEMENT = "<COMMA>";
    public static final String NEWLINE = "\n";
    public static final String NEWLINE_REPLACEMENT = "<NL>";

    public static final String BUOY_CATCHER_FILE = "BuoyCatcher.txt";

    public static final String BUOY_CATCHER_OPTIONS_RECORD_ID = "BCOPTS";
    public static final String BUOY_FAVORITE_RECORD_ID = "BCFAV";
    public static final String BUOY_WEATHER_CONDITION_RECORD_ID = "BCWC";

    public void saveBuoyCatcher(BuoyCatcher bCatcher)
    {
        writeBuoyCatcherCSV(bCatcher, BUOY_CATCHER_FILE);
    }

    /**
     * Open File
     *
     * @param fullPath String
     * @throws Exception
     * @return BufferedWriter
     */
    public BufferedWriter openFile(String fullPath) throws Exception
    {
        File file = new File(fullPath);

        if (file.exists())
        {
            file.delete();
        }

        file.createNewFile();
        return new BufferedWriter(new FileWriter(fullPath));
    }

    /**
     * Close File
     *
     * @param bw BufferedWriter
     * @throws Exception
     */
    public void closeFile(BufferedWriter bw) throws Exception
    {
        bw.flush();
        bw.close();
    }

    public void writeLine(BufferedWriter bw,
            String line) throws Exception
    {
        //Replace the following

        String output = line.replaceAll(COMMA, COMMA_REPLACEMENT);
        output = output.replaceAll(NEWLINE, NEWLINE_REPLACEMENT);
        output = output.replaceAll("\\" + FS_WRITE, FS_READ);

        bw.write(output + "\r\n");
        bw.flush();
    }

    public void writeLineAsIs(BufferedWriter bw,
            String line) throws Exception
    {
        bw.write(line + "\r\n");
        bw.flush();
    }

    public void writeBlankLine(BufferedWriter bw
    ) throws Exception
    {
        bw.write("\r\n");
        bw.flush();
    }

    public void writeBuoyCatcherCSV(BuoyCatcher bCatcher, String fullPath)
    {
        try
        {

            if (bCatcher != null)
            {
                BufferedWriter bw = openFile(fullPath);

                // First write the BuoyCatcher Search Options
                writeLineAsIs(bw, "###BuoyCatcher Options: BCOPTS,LAT,LONG,RADIUS");
                writeLine(bw, bCatcher.toCSVRecord());
                writeBlankLine(bw);

                // Write all the Favorites
                writeLineAsIs(bw, "###Favorite:  BCFAV,ID,NAME,LOCATION,RELATIVE_LOC,REPORT_TIME");
                writeLineAsIs(bw, "###Weather Condition:  BCWC,NAME,VALUE");
                writeFavoriteBuoyCSV(bw, bCatcher);

                closeFile(bw);

            }

        } catch (Exception ex)
        {
            Logger.getGlobal().log(Level.WARNING, ex.getMessage());
        }
    }

    /**
     * Write CSV record(s) to persist the content of a whole Buoy at a time for
     * all favorite buoys
     *
     * @param bw BufferedWriter
     * @param bCatcher BuoyCatcher object holding the favorite buoys
     */
    private void writeFavoriteBuoyCSV(BufferedWriter bw, BuoyCatcher bCatcher)
    {
        List<Buoy> listBuoys = bCatcher.getFavoriteBuoys();
        for (Buoy buoy : listBuoys)
        {
            try
            {
                writeLine(bw, buoy.toCSVRecord());

                writeWeatherConditionsCSV(bw, buoy);
                writeBlankLine(bw);
            } catch (Exception ex)
            {
                Logger.getGlobal().log(Level.WARNING, ex.getMessage());
            }
        }
    }

    /**
     * Write CSV record(s) to persist the content of weather conditions for the
     * specified buoy
     *
     * @param bw BufferedWriter
     * @param buoy buoy object holding the weather conditions collections
     */
    private void writeWeatherConditionsCSV(BufferedWriter bw, Buoy buoy)
    {
        try
        {
            List<WeatherCondition> listWC = buoy.getConditions();
            for (WeatherCondition wc : listWC)
            {
                writeLine(bw, wc.toCSVRecord());
            }
        } catch (Exception ex)
        {
            Logger.getGlobal().log(Level.WARNING, ex.getMessage());
        }
    }

}
