package GUI.entities;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import Entities.ESensorRecord;

public class PConsumerGUI extends javax.swing.JFrame {

    private final ArrayList<GUI_Panel> panels= new ArrayList<GUI_Panel>();
    private final int temperature;

    public PConsumerGUI(int numConsumers, int numClusters, int temperature) {
        initComponents();
        this.temperature=temperature;
        if (numClusters==0){
            for(int i=0;i<numConsumers;i++){
                GUI_Panel panel=new GUI_Panel(temperature);
                panels.add(panel);
                tabbedPane.addTab("Consumer "+i, panel);
            }
        }else{
            for (int j=0;j<numClusters;j++){
                JTabbedPane clusterTab=new JTabbedPane();
                tabbedPane.addTab("Cluster "+j,clusterTab);
                for(int i=0;i<numConsumers;i++){
                    GUI_Panel panel=new GUI_Panel(temperature);
                    panels.add(panel);
                    clusterTab.addTab("Consumer "+i, panel);
                }
            }
        }
        this.setVisible(true);
    }

    @SuppressWarnings("unchecked")                        
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PConsumer");
        setPreferredSize(new java.awt.Dimension(800, 600));
        setSize(new java.awt.Dimension(800, 600));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
        );

        pack();
    }            
    
    public void addData(ESensorRecord record,int consumerID){
        panels.get(consumerID).addRow(record);
    }

    public void updateMinMax(List<Double> minMax, int sensorID){
        for(GUI_Panel panel: this.panels){
            panel.updateMinMax(minMax, sensorID);
        }
    }

    public void updateAverage(double avg, int sensorID){
        for(GUI_Panel panel: this.panels){
            panel.updateAverage(avg, sensorID);
        }
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PProducerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PProducerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PProducerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PProducerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new PProducerGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration                   
}

