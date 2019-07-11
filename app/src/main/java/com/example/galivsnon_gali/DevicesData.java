package com.example.galivsnon_gali;

import io.particle.android.sdk.cloud.ParticleDevice;

public class DevicesData {
    ParticleDevice device;
    boolean hasVoted;
    String vote;
    int score;

    public DevicesData(ParticleDevice device){
        this.device = device;
        hasVoted = false;
        vote = "";
        score = 0;
    }

    public ParticleDevice getDevice() {
        return device;
    }

    public void setDevice(ParticleDevice device) {
        this.device = device;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
