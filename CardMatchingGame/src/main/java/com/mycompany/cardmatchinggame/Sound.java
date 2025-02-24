/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cardmatchinggame;

/**
 *
 * @author Test-Plus
 */
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.InputStream;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.Collections;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.net.*;
//import java.io.*;

public class Sound {

    private Clip BGMClip;  // Global variable to store the background music clip

    // Method to play background music
    public void playBGM(String bgmFile) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(bgmFile);  // Load BGM file from resources
            if (audioSrc == null) {
                System.err.println("BGM file not found: " + bgmFile);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrc);
            BGMClip = AudioSystem.getClip();  // Store the clip globally
            BGMClip.open(audioStream);
            BGMClip.loop(Clip.LOOP_CONTINUOUSLY);  // Loop indefinitely
        } catch (Exception e) {
            e.printStackTrace();
            // Show an error message if the sound file cannot be played
            JOptionPane.showMessageDialog(null, "Failed to play BGM: " + bgmFile, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to stop the currently playing background music
    public void stopBGM() {
        if (BGMClip != null && BGMClip.isRunning()) {
            BGMClip.stop();  // Stop the background music
        }
    }

    // Method to play sound effects
    public void playSoundEffect(String action) {
        try {
            String soundFile = "/sounds/" + action + ".wav";  // Path to sound effects
            InputStream audioSrc = getClass().getResourceAsStream(soundFile);
            if (audioSrc == null) {
                System.err.println("Sound effect file not found: " + soundFile);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrc);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();  // Play the sound effect
            clip.drain();  // Wait until the sound is finished
        } catch (Exception e) {
            e.printStackTrace();
            // Show an error message if the sound file cannot be played
            JOptionPane.showMessageDialog(null, "Failed to play sound effect: " + action, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
