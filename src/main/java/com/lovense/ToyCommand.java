package com.lovense;

public class ToyCommand {
    private transient String actionFormat = "Vibrate:%d";
    String action;
    String command = "Function";
    int apiVer = 1;
    String toy;
    int timeSec;
    private transient int intensity;

    public ToyCommand(String toyId, int timeSec, int intensity){
        this.toy = toyId;
        this.timeSec = timeSec;
        this.intensity = intensity;
        this.action = String.format(actionFormat, intensity);
    }
}
