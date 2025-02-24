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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import javax.sound.sampled.*;
//import java.util.Collections;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.net.*;
//import java.io.*;
//import java.io.InputStream;

public class CardPanel extends JPanel {
    private Image cardBack;   // Card back image
    private Image cardFront;  // Card front image
    private boolean isFlipped; // Indicates whether the card is flipped
    private int animationStep; // Controls the animation step (width scaling)
    private int value;        // The card's value (number)
    private int row;          // Row index in the game grid
    private int col;          // Column index in the game grid
    private boolean isMatched = false; // Tracks if the card has been matched
    private float alpha = 1.0f; // Transparency level (1.0 = fully opaque, 0.0 = fully transparent)
    private int fontSize = 20; // Font size for card value

    public CardPanel(Image cardBack, Image cardFront, int value, int row, int col) {
        this.cardBack = cardBack;
        this.cardFront = cardFront;
        this.isFlipped = false; // Initially, the card shows its back
        this.animationStep = 100; // Start with full width
        this.value = value; // Assign the card value
        this.row = row;     // Store row index
        this.col = col;     // Store column index
    }

    public int getValue() {
        return value;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void flipCard() {
        Timer timer = new Timer(5, null); // Timer for smooth animation
        timer.addActionListener(e -> {
            if (animationStep > 0 && !isFlipped) {
                // Shrink phase
                animationStep -= 5; // Decrease width percentage
            } else if (animationStep <= 0 && !isFlipped) {
                // Flip to the front
                isFlipped = true;
                animationStep = 5; // Start expanding
            } else if (animationStep < 100 && isFlipped) {
                // Expand phase
                animationStep += 5; // Increase width percentage
            } else {
                // Stop the animation when fully expanded
                ((Timer) e.getSource()).stop();
            }
            repaint(); // Repaint the panel for the next animation frame
        });
        timer.start();
    }
    
    public void resetFlip() {
    Timer timer = new Timer(5, null); // Timer for smooth animation
        timer.addActionListener(e -> {
            if (animationStep > 0 && isFlipped) {
                // Shrink phase
                animationStep -= 10; // Decrease width percentage
            } else if (animationStep <= 0 && isFlipped) {
                // Flip to the back
                isFlipped = false;
                animationStep = 10; // Start expanding
            } else if (animationStep < 100 && !isFlipped) {
                // Expand phase
                animationStep += 10; // Increase width percentage
            } else {
                // Stop the animation when fully expanded
                ((Timer) e.getSource()).stop();
            }
            repaint(); // Repaint the panel for the next animation frame
        });
        timer.start();
    }
    
    public void setMatched() {
        isMatched = true;

        Timer timer = new Timer(30, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (alpha > 0.3f || fontSize < 30) { // Animate alpha and font size
                    alpha = Math.max(0.3f, alpha - 0.05f); // Reduce transparency
                    fontSize = Math.min(30, fontSize + 1); // Increase font size
                    repaint(); // Trigger repaint to reflect changes
                } else {
                    ((Timer) e.getSource()).stop(); // Stop animation
                    System.out.println("Animation complete for card value: " + value);
                }
            }
        });
        timer.start();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable high-quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Apply transparency if the card is matched
        if (isMatched) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }
        
        // Calculate the width of the card based on the animation step
        int currentWidth = (getWidth() * animationStep) / 100;
        int xOffset = (getWidth() - currentWidth) / 2; // Center the shrinking card

        // Draw the card (back or front) based on the flipping state
        if (!isFlipped) {
        g2d.drawImage(cardBack, xOffset, 0, currentWidth, getHeight(), this);
        } else {
            g2d.drawImage(cardFront, xOffset, 0, currentWidth, getHeight(), this);

            // Draw the card value on top of the front
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
            FontMetrics metrics = g2d.getFontMetrics(); // Measure text size
            int textX = (getWidth() - metrics.stringWidth(String.valueOf(value))) / 2; // Centre horizontally
            int textY = (getHeight() + metrics.getAscent()) / 2; // Centre vertically
            g2d.drawString(String.valueOf(value), textX, textY);
        }
    }
}
