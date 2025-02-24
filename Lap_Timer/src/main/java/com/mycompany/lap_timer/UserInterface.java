package com.mycompany.lap_timer;
import java.awt.*;
//import java.awt.event.*; Used Lambda expressions
import javax.swing.*;
import javax.swing.border.*;

/**
 * A graphical user interface for the lap timer. No calculation is being
 * done here. This class is responsible for putting up the display on 
 * screen. It then refers to the "TimingEngine" to do all the real work.
 * 
 * @author Michael Kolling
 * @version 20 September 2004
 */

public class UserInterface
{
    private TimingEngine timer;  // Reference to the TimingEngine to manage timing logic

    private JFrame frame;  // The main window for the GUI
    private JLabel status;  // Label for displaying the current status (Timing or Stopped)
    private JLabel lapCountLabel;  // Label for displaying the number of laps completed
    private JLabel firstTimeLabel;  // Label for displaying the first lap time
    private JLabel lastTimeLabel;  // Label for displaying the last lap time
    private JLabel fastestTimeLabel;  // Label for displaying the fastest lap time
    private JLabel slowestTimeLabel;  // Label for displaying the slowest lap time
    private JLabel avgTimeLabel;  // Label for displaying the average lap time
    private JLabel totalTimeLabel;  // Label for displaying the total time of the run
    private JLabel avgSpeedLabel;  // Label for displaying the average speed of the run
    private Font largeFont;  // Font used for labels in the GUI
    private JButton startButton;  // Button to start/next lap
    private JButton stopButton;  // Button to stop counting lap
    private JButton pauseButton; // Button to pause counting lap
    private JLabel lengthLabel;  // Label for displaying lap length
    private JTextField lengthField;  // TextField for entering a new lap length

    /**
     * Create a user interface for a given TimingEngine.
     */
    public UserInterface(TimingEngine engine)
    {
        timer = engine;  // Initialize the timer (TimingEngine)
        makeFrame();  // Create the GUI frame and components (must be first)
        updateDisplay();  // Update the display after all components are initialized
        frame.setVisible(true);  // Show the GUI frame
        startButton.setText("Start");  // Set the button text to "Start"
        
        // Start the background thread to update the total time
        updateRunTime();  // Call to update total time in real-time
    }


    /**
     * Make this interface visible again. (Has no effect if it is already visible.)
     */
    public void setVisible(boolean visible)
    {
        frame.setVisible(visible);  // Set the visibility of the frame
    }
    
    /**
    * The 'Start/Next' button has been pressed. Start a lap time.
    */
    private void startTiming()
    {
        if (!timer.isTiming()) {  // If the timer is stopped, start a new run
            timer.startLap();
            startButton.setText("Next lap");
            updateRunTime();  // Start the real-time update when the timer starts
        } else {  // If the timer is already running, start a new lap
            timer.startLap();  
        }

        updateDisplay();  // Refresh the UI with updated lap count, times, etc.
    }
    
    /**
     * The 'Stop' button has been pressed. Stop the lap timing.
     */
    private void stopTiming()
    {
        if (timer.isTiming()) {
            timer.stop();
            startButton.setText("Start");
        }

        updateDisplay();
    }

    private void pauseTiming()
    {
        if (!timer.isPaused()) {
            timer.pauseLap();
            pauseButton.setText("Resume");
        } else {
            timer.resumeLap();
            pauseButton.setText("Pause");
            updateRunTime();  // Continue updating time
        }
        
        updateDisplay();
    }
    
    /**
     * The "Set length" button has been pressed. Read the text field
     * and, if it contains a sensible value, set this as the new lap length.
     */
    private void setLapLength()
    {
        String length = lengthField.getText();  // Get the input lap length from the text field
        try {
            int newLength = Integer.parseInt(length);  // Try to parse the length as an integer
            timer.setLapLength(newLength);  // Set the new lap length in the timer
            updateDisplay();  // Update the display to reflect the new lap length
        }
        catch(NumberFormatException exc) {
            lengthField.setText("");  // If input is invalid, clear the text field
        }
    }
    
    /**
     * Update the current display to correctly show all timer details.
     */
    private void updateDisplay()
    {
        // Update all the labels with the latest values from the TimingEngine
        status.setText(timer.getStatus());  // Update the status label
        status.setForeground(timer.isTiming() ? Color.GREEN : Color.RED);  // Green if timing, red if stopped
        startButton.setEnabled(!timer.isPaused());   // Disable start button if paused
        stopButton.setEnabled(timer.isTiming());   // Disable stop button if not timing
        pauseButton.setEnabled(timer.isTiming() || timer.isPaused());   // Disable pause button if not timing
        lapCountLabel.setText(Integer.toString(timer.getLapCount()));  // Update the lap count label
        firstTimeLabel.setText(timer.getFirstLapTime());  // Update the new lap time label
        lastTimeLabel.setText(timer.getLastTime());  // Update the last lap time label
        fastestTimeLabel.setText(timer.getFastestLapTime());  // Update the fastest lap time label
        slowestTimeLabel.setText(timer.getSlowestLapTime());  // Update the slowest lap time label
        avgTimeLabel.setText(timer.getAverageTime());  // Update the average lap time label
        totalTimeLabel.setText(timer.getTotalTime());  // Update the total time label
        avgSpeedLabel.setText(timer.getAverageSpeed());  // Update the average speed label
        lengthLabel.setText(timer.getLapLength() + "m");  // Update the lap length label
        updateTitle();  // Update the title with the current lap count
    }

    /**
     * Make the frame for the user interface.
     */
    private void makeFrame()
    {
        frame = new JFrame("Superb Lap Timer 2025");  // Create the main frame for the GUI
        
        // Prepare the main content pane
        JPanel contentPane = (JPanel)frame.getContentPane();
        contentPane.setLayout(new BorderLayout(8, 8));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        status = new JLabel(timer.getStatus());  // Create the status label
        contentPane.add(status, BorderLayout.NORTH);  // Add status label to the top of the frame
        largeFont = status.getFont().deriveFont(24.0f);  // Set font size for the status label
        status.setFont(largeFont);
        status.setForeground(new Color(84, 16, 16));  // Set the text color for status

        // Create the main display in the center with all the data labels
        JPanel mainDisplay = new JPanel(new GridLayout(0, 2, 10, 10));
        mainDisplay.setBackground(Color.WHITE);
        mainDisplay.setFont(largeFont);
        mainDisplay.setBorder(BorderFactory.createCompoundBorder(
                                new EtchedBorder(),
                                new EmptyBorder(10, 10, 10, 10)));
        
        addLabel(mainDisplay, "Number of laps:");  // Add label for laps count
        lapCountLabel = addLabel(mainDisplay, "0");  // Initialize lap count label with 0
        
        addLabel(mainDisplay, "First lap time:");  // Add label for first lap time
        firstTimeLabel = addLabel(mainDisplay, "0");  // Initialize last lap time label with 0
        addLabel(mainDisplay, "Last lap time:");  // Add label for last lap time
        lastTimeLabel = addLabel(mainDisplay, "0");  // Initialize last lap time label with 0
        addLabel(mainDisplay, "Fastest lap time:");  // Add label for fastest lap time
        fastestTimeLabel = addLabel(mainDisplay, "0");  // Initialize last lap time label with 0
        addLabel(mainDisplay, "Slowest lap time:");  // Add label for slowest lap time
        slowestTimeLabel = addLabel(mainDisplay, "0");  // Initialize last lap time label with 0
        
        addLabel(mainDisplay, "Average lap time:");  // Add label for average lap time
        avgTimeLabel = addLabel(mainDisplay, "0");  // Initialize average lap time label with 0
        
        addLabel(mainDisplay, "Total time:");  // Add label for total time
        totalTimeLabel = addLabel(mainDisplay, "0");  // Initialize total time label with 0
        
        addLabel(mainDisplay, "Average speed:");  // Add label for average speed
        avgSpeedLabel = addLabel(mainDisplay, "0");  // Initialize average speed label with 0
        
        contentPane.add(mainDisplay, BorderLayout.CENTER);  // Add main display to the center of the frame
        
        // Create the panel with the lap length label and entry
        JPanel lengthPanel = new JPanel(new GridLayout(0, 1));
        lengthPanel.setBorder(BorderFactory.createCompoundBorder(
                                new EtchedBorder(),
                                new EmptyBorder(10, 10, 10, 10)));
        lengthPanel.add(new JLabel("Lap length:"));
        lengthLabel = new JLabel();  // Initialize lap length label
        lengthPanel.add(lengthLabel);
        lengthField = new JTextField(8);  // Create a text field for entering lap length
        lengthPanel.add(lengthField);
        JButton setButton = new JButton("Set length");  // Button for setting lap length
        setButton.addActionListener(e -> setLapLength());  // Use lambda expression for button action
        lengthPanel.add(setButton);

        JPanel flow = new JPanel();  // Panel for adding the length panel
        flow.add(lengthPanel);
        contentPane.add(flow, BorderLayout.EAST);  // Add length panel to the east side of the frame

        // Create the panel with the three buttons at the bottom
        JPanel buttonPanel = new JPanel();
        startButton = new JButton("Start");  // Create "Start" button
        startButton.addActionListener(e -> startTiming());
        buttonPanel.add(startButton);

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> pauseTiming());
        buttonPanel.add(pauseButton);  // Create "Pause" button
        
        stopButton = new JButton("Stop");  // Create "Stop" button
        stopButton.addActionListener(e -> stopTiming());
        buttonPanel.add(stopButton);
        
        //User-friendly tool tips
        startButton.setToolTipText("Click to start or proceed to next lap");
        pauseButton.setToolTipText("Click to pause the timer");
        stopButton.setToolTipText("Click to stop the timer");
        lengthField.setToolTipText("Enter lap length in meters (e.g., 400)");
        
        contentPane.add(buttonPanel, BorderLayout.SOUTH);  // Add buttons to the bottom of the frame
        frame.pack();  // Pack the frame to fit its components
    }

    /**
     * Updates the title of the JFrame to include the current lap count.
     */
    private void updateTitle() {
        if (timer.getLapCount() > 0) {
            frame.setTitle("Superb Lap Timer 2025 - Current Lap: " + timer.getLapCount());  // Include current lap count in title
        } else {
            frame.setTitle("Superb Lap Timer 2025");  // Default title
        }
    }
    
    /**
    * Updates the total run time in real-time as long as the timer is running.
    */
    private void updateRunTime()
    {
        new Thread(() -> {
            while (timer.isTiming()) {  // Keep running as long as the timer is active
                SwingUtilities.invokeLater(() -> totalTimeLabel.setText(timer.getRunTime()));  // Safely update GUI
                try {
                    Thread.sleep(10);  // Sleep for 10ms to reduce CPU usage
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    /**
     * Add a label to the main display panel.
     */
    private JLabel addLabel(Container panel, String text)
    {
        JLabel label = new JLabel(text);  // Create a new label with the provided text
        label.setFont(largeFont);  // Set the font for the label
        panel.add(label);  // Add the label to the panel
        return label;  // Return the label
    }
}