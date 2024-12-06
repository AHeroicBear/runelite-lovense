package com.lovense;

public class LovenseToy {
    String id;
    String name;
    String status;
    int battery;
    String nickname;
    String version;
    String[] shortFunctionNames;
    String[] fullFunctionNames;

    public LovenseToy() {
        // For Deserialization
    }

    @Override
    public String toString() {
        return name;
    }
}
