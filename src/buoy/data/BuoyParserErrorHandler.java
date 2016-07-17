/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buoy.data;

import buoy.common.BuoyException;

/**
 *
 * @author Shankar Krishnan
 */
public interface BuoyParserErrorHandler
{
   public void handleParsingError(BuoyException error);

}
