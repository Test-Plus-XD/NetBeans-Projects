package com.mycompany.lap_timer; 
/**
 * The timer engine for a lap timer application. This engine can keep
 * track of a 'run'. A run is a sequence of one or more consecutive laps.
 * The timer records single lap times and the time for the total run, 
 * and it calculate averages and speed. 
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

 /*# 
  * Your task: 
  * - complete the comment above (add your names and version)
  * - implement this class
  * - test until you are convinced that it works correctly
  * - complete the README note in the project
  */

public class TimingEngine
{   
    public boolean isTiming;        // Indicates if timing is in progress
    public boolean isPaused;        // Indicates if the timer is paused
    
    private long runStartTime;      // Stores the time when the run started (milliseconds)
    private long lapStartTime;      // Stores the time when the current lap started (milliseconds)
    private long totalTime;         // Accumulates the total run time (milliseconds)
    private long pausedTime;        // Stores the time when the timer was paused (milliseconds)

    private long firstLapTime;      // Stores the time of the first lap (milliseconds)
    private long lastLapTime;       // Stores the time of the last lap (milliseconds)
    private long fastestLapTime;    // Stores the time of the fastest lap (milliseconds)
    private long slowestLapTime;    // Stores the time of the slowest lap (milliseconds)
    
    private int lapCount;           // Tracks the number of laps completed
    private int lapLength;          // Lap length in meters (default 400)
    
    /**
     * Creates a TimingEngine object.
     * Initialises the timer in a "Stopped" state, ready to start timing.
     * The default lap length is set to 400 metres.
     */
    public TimingEngine()
    {
        isTiming = isPaused = false;
        runStartTime = lapStartTime = totalTime = pausedTime = firstLapTime = lastLapTime = fastestLapTime = slowestLapTime = lapCount = 0;
        lapLength = 400;
    }

    /**
     * Starts timing a lap.
     * If the timer is not running, it starts a new run.
     * If already running, it ends the current lap and starts a new one.
     */
    public void startLap()
    {
        long currentTime = getSystemTime();
        
        // Start a new run
        if (!isTiming && !isPaused) {
            isTiming = true;
            runStartTime = currentTime;
            lapStartTime = currentTime;
            lapCount = 0;
            totalTime = 0;
            firstLapTime = 0;
            lastLapTime = 0;
            fastestLapTime = 0;
            slowestLapTime = 0;
        }
        // Complete the current lap and start a new one if the timer is running
        else if (isTiming) {
            long lapDuration = currentTime - lapStartTime;  // Calculate current lap duration
            lastLapTime = lapDuration;                      // Set last lap time
            totalTime += lapDuration;                       // Update total time
            lapCount++;                                     // Increment lap count
            lapStartTime = currentTime;  // Reset lap start time for the next lap
        }
    }
    
    /**
     * Pauses the timer.
     * Records the time elapsed in the current lap and sets the state to paused.
     */
    public void pauseLap()
    {
       if (isTiming) {
           pausedTime = getSystemTime() - lapStartTime;  // Calculate elapsed time in current lap before pausing
           isPaused = true;
           isTiming = false;
       }
    }

    /**
     * Resumes the timer if it's paused.
     * Adjusts the lap start time so that the paused duration is excluded.
     */
    public void resumeLap()
    {
        if (isPaused) {
            lapStartTime = getSystemTime() - pausedTime; // Adjust lap start time so that the pause duration is skipped
            isPaused = false;
            isTiming = true;
        }
    }
    
    /**
     * Stops the timer.
     * Records the final lap time, updates total time, increments lap count, and marks the timer as stopped.
     */
    public void stop()
    {
        if (isTiming) {
            long lapDuration = getSystemTime() - lapStartTime;
            lastLapTime = lapDuration;
            totalTime += lapDuration;
            lapCount++;
            isTiming = false;
        }
    }
    
    /**
     * Returns the current status of the timer.
     * The result is either "Timing..." or "Stopped".
     */
    public String getStatus()
    {
        return isTiming ? "Timing..." : "Stopped";
    }
    
    /**
    * Returns whether the timer is currently running.
    */
    public boolean isTiming()
    {
        return isTiming;
    }
    public boolean isPaused()
    {
        return isPaused;
    }

    /**
     * Returns the number of laps completed in the run.
     */
    public int getLapCount()
    {
        return lapCount;
    }
    
    /**
     * Returns the time of the first lap in "m:ss:hh" format.
     * Also updates it if this is the first lap.
     */
    public String getFirstLapTime()
    {
        // Update first and fastest lap time on the first lap
        if (lapCount == 1 && firstLapTime == 0) {
            firstLapTime = fastestLapTime = lastLapTime;
        }
        return formatTime(firstLapTime);
    }
    /**
     * Returns the time of the last lap in "m:ss:hh" format.
     * Also updates it if necessary.
     */
    public String getLastTime()
    {
        return formatTime(lastLapTime);
    }
    /**
     * Returns the time of the fastest lap in "m:ss:hh" format.
     * Updates it if this lap is the fastest.
     */
    public String getFastestLapTime()
    {
        // Update fastest lap time after every lap
        if (lastLapTime < fastestLapTime) {
            fastestLapTime = lastLapTime;
        }
        return formatTime(fastestLapTime);
    }
    /**
     * Returns the time of the slowest lap in "m:ss:hh" format.
     * Updates it if this lap is the slowest.
     */
    public String getSlowestLapTime()
    {
        // Update slowest lap time after every lap
        if (lastLapTime > slowestLapTime) {
            slowestLapTime = lastLapTime;
        }
        return formatTime(slowestLapTime);
    }
    
    /**
     * Returns the average lap time in "m:ss:hh" format.
     */
    public String getAverageTime()
    {
        if (lapCount == 0) {
            return formatTime(0);
        }
        long average = totalTime / lapCount;
        return formatTime(average);
    }
    
    /**
     * Returns the total time of the run in "m:ss:hh" format.
     */
    public String getTotalTime()
    {
        return formatTime(totalTime);
    }
    
    /**
    * Returns the elapsed run time in "m:ss:hh" format.
    */
    public String getRunTime()
    {
        long elapsedTime = 0;
        if (isTiming) {
            elapsedTime = getSystemTime() - lapStartTime; // Time since the last lap started
        } else if (isPaused) {
            elapsedTime = getSystemTime(); // If paused, display the time up to the last lap
        }
        return formatTime(elapsedTime + totalTime); // Add time from paused laps
    }
    
    /**
     * Returns the average speed in metres per second.
     * The result is a string such as "73.46 m/s".
     */
    public String getAverageSpeed()
    {
        if (totalTime == 0 || lapCount == 0) {
            return "0 m/s";
        }
        double avgSpeed = (lapCount * lapLength) / (totalTime / 1000.0);
        return String.format("%.2f m/s", avgSpeed);
    }
    
    /**
     * Returns the lap length in metres.
     */
    public int getLapLength()
    {
        return lapLength;
    }
    
    /**
     * Sets the lap length to a new value.
     */
    public void setLapLength(int length)
    {
        if (length > 0) {
            lapLength = length;
        }
    }
    
    /**
     * Returns the current system time in milliseconds.
     */
    private long getSystemTime()
    {
        return System.currentTimeMillis();
    }
    
    /**
     * Converts a time in milliseconds to "m:ss:hh" format.
     */
    private String formatTime(long timeInMs)
    {
        long minutes = timeInMs / 60000;
        long remainingMs = timeInMs % 60000;
        long seconds = remainingMs / 1000;
        long hundredths = (remainingMs % 1000) / 10;
        return String.format("%d:%02d:%02d", minutes, seconds, hundredths);
    }
}