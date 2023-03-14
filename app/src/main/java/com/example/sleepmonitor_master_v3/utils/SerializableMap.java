package com.example.sleepmonitor_master_v3.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.SortedMap;

public class SerializableMap implements Serializable {
    private SortedMap<String, String> mapInbed;
    private SortedMap<String, String> mapOffbed;
    private SortedMap<String, Integer> heart;
    private SortedMap<String, Integer> snore;
    private SortedMap<String, Integer> intervention;
    private SortedMap<String, Integer> error;


    private SortedMap<String, Integer> weight;
    private SortedMap<String, Integer> rem;

    public SerializableMap(SortedMap<String, String> inbed,SortedMap<String, Integer> weight, SortedMap<String, Integer> rem,SortedMap<String, Integer> offbed) {
        this.mapInbed = inbed;
        this.weight = weight;
        this.rem = rem;
    }

    public SortedMap<String, Integer> getWeight() {
        return weight;
    }

    public void setWeight(SortedMap<String, Integer> weight) {
        this.weight = weight;
    }

    public SortedMap<String, Integer> getRem() {
        return rem;
    }

    public void setRem(SortedMap<String, Integer> rem) {
        this.rem = rem;
    }

    public SerializableMap(SortedMap<String, Integer> snore, SortedMap<String, Integer> intervention, SortedMap<String, Integer> error) {
        this.snore = snore;
        this.intervention = intervention;
        this.error = error;
    }

    public SortedMap<String, Integer> getSnore() {
        return snore;
    }

    public void setSnore(SortedMap<String, Integer> snore) {
        this.snore = snore;
    }

    public SortedMap<String, Integer> getIntervention() {
        return intervention;
    }

    public void setIntervention(SortedMap<String, Integer> intervention) {
        this.intervention = intervention;
    }

    public SortedMap<String, Integer> getError() {
        return error;
    }

    public void setError(SortedMap<String, Integer> error) {
        this.error = error;
    }

    public SerializableMap(SortedMap<String, Integer> heart) {
        this.heart = heart;
    }

    public SortedMap<String, Integer> getHeart() {
        return heart;
    }

    public void setHeart(SortedMap<String, Integer> heart) {
        this.heart = heart;
    }

    public SerializableMap(SortedMap<String, String> mapInbed, SortedMap<String, String> mapOffbed) {
        this.mapInbed = mapInbed;
        this.mapOffbed = mapOffbed;
    }

    public SortedMap<String, String> getMapInbed() {
        return mapInbed;
    }

    public void setMapInbed(SortedMap<String, String> mapInbed) {
        this.mapInbed = mapInbed;
    }

    public SortedMap<String, String> getMapOffbed() {
        return mapOffbed;
    }

    public void setMapOffbed(SortedMap<String, String> mapOffbed) {
        this.mapOffbed = mapOffbed;
    }


}