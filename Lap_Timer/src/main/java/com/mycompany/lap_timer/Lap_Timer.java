/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.lap_timer;
/**
 * @author Ng Yu Ham Baldwin
 */
public class Lap_Timer {
    
    private TimingEngine engine;
    private UserInterface gui;

    // Constructor: creates a new lap timer and shows the GUI
    public Lap_Timer() {
        engine = new TimingEngine();
        gui = new UserInterface(engine);
    }

    // In case the window was closed, show it again.
    public void show() {
        gui.setVisible(true);
    }

    // Main method added as the entry point for the application
    public static void main(String[] args) {
        new Lap_Timer();
    }
}