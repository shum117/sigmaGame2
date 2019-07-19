/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame;

import com.sigma.sigmagame.model.Lobby;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author anton
 */
public class LobbyWindow implements Runnable,ActionListener{
    private GameClient gc;
    private JFrame frame;
    private JPanel grid;
    private JPanel legend;
    private ArrayList<Color> colors;
    private ArrayList<KryoConfig.SenatorDto> senators;

    public LobbyWindow(GameClient gc) {
        this.gc = gc;
        colors = new ArrayList<>();
        colors.add(new Color(230, 25, 75));
        colors.add(new Color(60, 180, 75));
        colors.add(new Color(255, 225, 25)); 
        colors.add(new Color(0, 130, 200)); 
        colors.add(new Color(245, 130, 48)); 
        colors.add(new Color(145, 30, 180));
        colors.add(new Color(70, 240, 240));
        colors.add(new Color(240, 50, 230));
        colors.add(new Color(210, 245, 60));
        colors.add(new Color(250, 190, 190));
        colors.add(new Color(0, 128, 128));
        colors.add(new Color(230, 190, 255));
        colors.add(new Color(170, 110, 40)); 
        colors.add(new Color(255, 250, 200));
        colors.add(new Color(128, 0, 0)); 
        colors.add(new Color(170, 255, 195));
        colors.add(new Color(128, 128, 0));
        colors.add(new Color(255, 215, 180));
        colors.add(new Color(0, 0, 128)); 
        colors.add(new Color(128, 128, 128));
    }
    
    @Override
    public void run() {
        frame = new JFrame("Lobby");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout(20,20));
        grid = new JPanel();
        grid.setLayout(new GridLayout(10, 10));
        frame.add(grid, BorderLayout.CENTER);
        legend = new JPanel(new GridLayout(Model.CORPORATIONS.length,1));
        for (int i = 0; i < Model.CORPORATIONS.length; i++) {
            String string = Model.CORPORATIONS[i];
            JButton label = new JButton(string);
            label.addActionListener(this);
            label.setBackground(colors.get(i));
            legend.add(label);
        }
        frame.add(legend,BorderLayout.EAST);
        gc.lobbyList();
        frame.setVisible(true);
    }
    
    public void update(KryoConfig.SenatorsListDto list){
        senators = list.senators;
        grid.setVisible(false);
        /*SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {*/
                System.out.println("removeall");
                ArrayList<String> corps = new ArrayList<>();
                for (int i = 0; i < Model.CORPORATIONS.length; i++) {
                    String string = Model.CORPORATIONS[i];
                    corps.add(string);
                }
                //frame.remove(grid);
                //grid = new JPanel();
                grid.removeAll();
                //grid.setLayout(new GridLayout(10, 10));
                for (KryoConfig.SenatorDto senator : senators) {
                    JButton b = new JButton(String.valueOf((senator.level+1)*Lobby.LEVEL_PRICE));
                    if(!senator.corp.isEmpty())
                        b.setBackground(colors.get(corps.indexOf(senator.corp)));
                    int a = senators.indexOf(senator);
                    b.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            gc.buySenator(a);
                        }
                    });
                    grid.add(b);
                }
                //frame.add(grid,BorderLayout.CENTER);
                grid.setVisible(true);
            /*}
        });*/
        
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        int ret = JOptionPane.showConfirmDialog(frame, "Vote");
        if(ret != 2){
            gc.askToVote(ret == 0);
        }
        
    }
}
