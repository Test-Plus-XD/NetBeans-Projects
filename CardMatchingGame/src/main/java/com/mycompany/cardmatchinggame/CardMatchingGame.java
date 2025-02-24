/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.cardmatchinggame;
import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.*;
import java.io.*;
import java.io.InputStream;

public class CardMatchingGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CardMatchingGameGUI game = new CardMatchingGameGUI();
            game.createAndShowGUI();
        });
    }
}

class CardMatchingGameGUI {
    private Sound sound;
    private JFrame frame;
    private int[][] cardValues;
    private boolean[][] cardRevealed;
    private int firstRow = -1, firstCol = -1;
    private int ROWS = 2;
    private int COLS = 4;
    private Timer gameTimer;
    private int elapsedTime = 0;
    private JLabel timerLabel, scoreLabel;
    private int score = 0;
    private int level = 1;
    private boolean isProcessing = false;
    private CardPanel[][] cardPanels; // Stores all CardPanel objects for the game grid
    //private HashMap<String, String> soundFiles;

    public void createAndShowGUI() {
        frame = new JFrame("Card Matching Game - Level " + level);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS));
        //buttons = new JButton[ROWS][COLS];
        cardPanels = new CardPanel[ROWS][COLS]; // Store the card panels
        cardValues = new int[ROWS][COLS];
        cardRevealed = new boolean[ROWS][COLS];
        initializeBoard();
        
        //loadSounds();
        sound = new Sound();  // Initialize the Sound object
        if (level < 2) {
            sound.playBGM("/sounds/Malkuth1.wav");
        } else if (level == 2) {
            sound.playBGM("/sounds/Malkuth2.wav");
        } else {
            sound.playBGM("/sounds/Malkuth3.wav");
        }
        
        // Load card images
        //cardWidth = gridPanel.getWidth() / COLS; // Desired card width
        //cardHeight = gridPanel.getHeight() / ROWS; // Desired card height
        ImageIcon cardBackIcon = new ImageIcon(getClass().getResource("/images/card_back.png"));
        ImageIcon cardFrontIcon = new ImageIcon(getClass().getResource("/images/card_front.png"));
        Image cardBack = cardBackIcon.getImage();
        Image cardFront = cardFrontIcon.getImage();
        
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int value = cardValues[i][j]; // Get card value from the board
                CardPanel cardPanel = new CardPanel(cardBack, cardFront, value, i, j); // Create CardPanel
                cardPanels[i][j] = cardPanel; // Store in the array

                int row = i, col = j; // Capture the row and column
                cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        if (!cardRevealed[row][col]) {
                            handleCardFlip(cardPanel); // Pass the panel to the flip handler
                        }
                    }
                });
                gridPanel.add(cardPanel); // Add the card panel to the grid
            }
        }

        timerLabel = new JLabel("Time: 0 seconds");
        scoreLabel = new JLabel("Score: " + score);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Custom font
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Custom font

        frame.add(timerLabel, BorderLayout.NORTH);
        frame.add(gridPanel, BorderLayout.CENTER);
        frame.add(scoreLabel, BorderLayout.SOUTH);

        startTimer();
        frame.setSize(COLS*200-100, ROWS*200+150);
        frame.setVisible(true);
    }

    private void initializeBoard() {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 0; i < ROWS * COLS / 2; i++) {
            values.add(i);
            values.add(i);
        }
        Collections.shuffle(values);

        int index = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                cardValues[i][j] = values.get(index++);
                cardRevealed[i][j] = false;
            }
        }
    }

    private void handleCardFlip(CardPanel cardPanel) {
        System.out.println("Flipping card at (" + cardPanel.getRow() + ", " + cardPanel.getCol() + ") with value: " + cardPanel.getValue()); //Debug
        if (isProcessing || cardPanel.isFlipped()) {
            return; // Ignore if already flipped or currently processing
        }

        sound.playSoundEffect("flip"); // Play the flip sound
        cardPanel.flipCard(); // Animate the card flip
        cardRevealed[cardPanel.getRow()][cardPanel.getCol()] = true; // Mark as revealed

        if (firstRow == -1 && firstCol == -1) {
            // First card flipped
            firstRow = cardPanel.getRow();
            firstCol = cardPanel.getCol();
            isProcessing = false;
        } else {
            // Second card flipped
            isProcessing = true;
            if (cardValues[firstRow][firstCol] == cardPanel.getValue()) {
                // Cards match
                cardPanels[firstRow][firstCol].setMatched();
                cardPanel.setMatched();
                score += 10;
                scoreLabel.setText("Score: " + score);
                scoreLabel.repaint();
                firstRow = -1;
                firstCol = -1;
                isProcessing = false;
                if (areAllCardsMatched()) {
                    stopTimer();
                    showCompletionDialog(); // Show "Next Level" dialog
                }
            } else {
                // Cards don't match, reset after delay
                Timer timer = new Timer(1000, e -> {
                    cardPanels[firstRow][firstCol].resetFlip(); // Reset first card
                    cardPanel.resetFlip(); // Reset second card
                    cardRevealed[firstRow][firstCol] = false;
                    cardRevealed[cardPanel.getRow()][cardPanel.getCol()] = false;
                    firstRow = -1;
                    firstCol = -1;
                    isProcessing = false;
                });
                if (level > 1){
                    score --;
                    scoreLabel.setText("Score: " + score);
                    scoreLabel.repaint();
                }
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    private void showCompletionDialog() {
        // Create a custom JDialog
        JDialog dialog = new JDialog(frame, "Level Complete", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());

        // Load the background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/background.png"));
        Image bgImage = bgIcon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);

        // Custom JPanel with background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null); // Use absolute positioning for components

        // Add dialog text
        JLabel messageLabel = new JLabel("<html><center>Congratulations! You wasted " + elapsedTime + " seconds of your life with the score of " + score + ".<br>Which means you got " + score/elapsedTime + " score per second.</center></html>");
        messageLabel.setBounds(50, 50, 300, 100); // Position and size
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Custom font
        messageLabel.setForeground(Color.WHITE); // Text colour for visibility
        backgroundPanel.add(messageLabel);

        // Add buttons
        JButton nextLevelButton = new JButton("Next Level");
        nextLevelButton.setBounds(100, 180, 100, 30); // Position and size
        nextLevelButton.addActionListener(e -> {
            dialog.dispose(); // Close dialog
            nextLevel(); // Start the next level
        });

        JButton exitButton = new JButton("Exit");
        if (level < 5){
            exitButton.setBounds(200, 180, 100, 30); // Position and size
        } else {
            exitButton.setBounds(150, 200, 100, 30);
        }
        exitButton.addActionListener(e -> {
            System.exit(0); // Exit the application
        });

        // Add buttons to the panel
        if (level < 5 ){
        backgroundPanel.add(nextLevelButton);}
        backgroundPanel.add(exitButton);

        // Add background panel to the dialog
        dialog.add(backgroundPanel, BorderLayout.CENTER);

        // Show the dialog
        dialog.setVisible(true);
    }

    private void nextLevel() {
        frame.dispose();
        level++;
        
        // Alternate between increasing ROWS and COLS
        if (level % 2 == 1) {
           COLS++;
        } else {
           ROWS++;
        }
        
        // Ensure ROWS * COLS is always even
        if ((ROWS * COLS) % 2 != 0) {
            COLS++; // Increment COLS to make the total even
        }

       // Rebuild the board
       frame.getContentPane().removeAll(); // Clear existing components
       sound.stopBGM(); // Stop the current BGM
       createAndShowGUI(); // Recreate the game board with new grid size
    }

    private boolean areAllCardsMatched() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (!cardRevealed[i][j]) {
                    return false; // A card is still not matched
                }
            }
        }
        return true; // All cards are matched
    }
    
    private void startTimer() {
        elapsedTime = 0;
        gameTimer = new Timer(1000, e -> {
            elapsedTime++;
            timerLabel.setText("Time: " + elapsedTime + " seconds");
        });
        gameTimer.start();
    }

    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
}