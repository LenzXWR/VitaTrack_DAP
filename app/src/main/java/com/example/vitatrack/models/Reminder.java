package com.example.vitatrack.models;

public class Reminder {
    private long id;
    private String habitType;
    private String time;
    private String frequency;
    private boolean enabled;

    public Reminder(long id, String habitType, String time, String frequency, boolean enabled) {
        this.id = id;
        this.habitType = habitType;
        this.time = time;
        this.frequency = frequency;
        this.enabled = enabled;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getHabitType() { return habitType; }
    public void setHabitType(String habitType) { this.habitType = habitType; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}