/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buoy.gui;

import buoy.model.Buoy;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Shankar Krishnan
 */
public class MissingBuoyCellRenderer extends DefaultTableCellRenderer
{

    private List<Buoy> buoyList = null;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        Buoy buoy = null;
        try
        {
            buoyList = (List<Buoy>) table.getClientProperty(BuoyCatcherDialog.CLIENT_PROPERTY_BUOY_LIST);
            if (buoyList != null)
            {
                try
                {
                    buoy = buoyList.get(row);
                } catch (Exception e)
                {
                }

            }
        } catch (Exception ex)
        {
        }

        if (buoy != null)
        {
            if (buoy.isStale())
            {

                  cellComponent.setForeground(Color.RED);

            } else
            {
                  cellComponent.setForeground(Color.BLACK);
            }
        } else
        {
              cellComponent.setForeground(Color.BLACK);
        }
        return cellComponent;
    }

}
