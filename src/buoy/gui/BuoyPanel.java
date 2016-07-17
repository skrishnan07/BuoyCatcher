/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buoy.gui;

import buoy.Buoy;
import buoy.BuoyCatcher;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sanjana
 */
public class BuoyPanel extends javax.swing.JPanel
{

    /**
     * Creates new form FavoritesPanel
     */
    public BuoyPanel()
    {
        initComponents();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jScrollPane1 = new javax.swing.JScrollPane();
        tableBuoys = new javax.swing.JTable();
        lblTop = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableConditions = new javax.swing.JTable();
        btnFavorite = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        tableBuoys.setColumnSelectionAllowed(true);
        tableBuoys.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${buoyList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, tableBuoys);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${stationID}"));
        columnBinding.setColumnName("Station ID");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Full Name");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${latlong}"));
        columnBinding.setColumnName("Location");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relativeLocation}"));
        columnBinding.setColumnName("Relative Location");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(tableBuoys);
        tableBuoys.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tableBuoys.getColumnModel().getColumnCount() > 0)
        {
            tableBuoys.getColumnModel().getColumn(0).setResizable(false);
            tableBuoys.getColumnModel().getColumn(0).setPreferredWidth(150);
            tableBuoys.getColumnModel().getColumn(1).setResizable(false);
            tableBuoys.getColumnModel().getColumn(1).setPreferredWidth(250);
            tableBuoys.getColumnModel().getColumn(2).setResizable(false);
            tableBuoys.getColumnModel().getColumn(2).setPreferredWidth(150);
            tableBuoys.getColumnModel().getColumn(3).setResizable(false);
            tableBuoys.getColumnModel().getColumn(3).setPreferredWidth(250);
        }

        lblTop.setText("Favorite Buoys:");

        jLabel2.setText("Conditions:");

        tableConditions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String []
            {
                "Attribute", "Value"
            }
        ));
        jScrollPane2.setViewportView(tableConditions);

        btnFavorite.setText("Remove As Favorite");
        btnFavorite.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnFavoriteActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Last Reported At:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFavorite))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTop)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblTop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(btnFavorite)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFavoriteActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnFavoriteActionPerformed
    {//GEN-HEADEREND:event_btnFavoriteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFavoriteActionPerformed

    public static int MODE_ALL = 0;
    public static int MODE_FAVORITES = 1;
    
    private int buoyMode = MODE_ALL;
    protected void setMode(int mode)
    {
        buoyMode = mode;
        if ( buoyMode == MODE_ALL)
        {
            btnFavorite.setText("Mark As Favorite");
            lblTop.setText("All Buoys");
        }
        else
        {
            btnFavorite.setText("Remove From Favorites");
            lblTop.setText("My Favorite Buoys");
        }
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFavorite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTop;
    private javax.swing.JTable tableBuoys;
    private javax.swing.JTable tableConditions;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables


    private List<Buoy> buoyList;
    public static final String PROP_BUOYLIST = "buoyList";
    
    private Buoy selectedBuoy;
    public static final String PROP_SELECTEDBUOY = "selectedBuoy";
    
    private List<Buoy> buoyList;
    public static final String PROP_BUOYLIST = "buoyList";

    /**
     * Get the value of selectedBuoy
     *
     * @return the value of selectedBuoy
     */
    public Buoy getSelectedBuoy()
    {
        return selectedBuoy;
    }

    /**
     * Set the value of selectedBuoy
     *
     * @param selectedBuoy new value of selectedBuoy
     */
    public void setSelectedBuoy(Buoy selectedBuoy)
    {
        Buoy oldSelectedBuoy = this.selectedBuoy;
        this.selectedBuoy = selectedBuoy;
        propertyChangeSupport.firePropertyChange(PROP_SELECTEDBUOY, oldSelectedBuoy, selectedBuoy);
        
        btnFavorite.setEnabled(selectedBuoy != null);
    }

    
    protected BuoyCatcher buoyCatcher = null;

    /**
     * Get the value of buoyList
     *
     * @return the value of buoyList
     */
    public List<Buoy> getBuoyList()
    {
        return buoyList;
    }

    /**
     * Set the value of buoyList
     *
     * @param buoyList new value of buoyList
     */
    public void setBuoyList(List<Buoy> buoyList)
    {
        List<Buoy> oldBuoyList = this.buoyList;
        this.buoyList = buoyList;
        propertyChangeSupport.firePropertyChange(PROP_BUOYLIST, oldBuoyList, buoyList);
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

  

   void populateBuoyList(List <Buoy> listB)
    {
        ArrayList <Buoy> arr = new ArrayList<>();
      
        if ( listB != null && listB.size() > 0)
        {
            arr.addAll(listB);
            
        }
        setBuoyList(arr);
    }







}
