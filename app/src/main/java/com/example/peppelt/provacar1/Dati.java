package com.example.peppelt.provacar1;

/**
 * Created by Ros on 08/05/2018.
 */

public class Dati {
    String distance, duration, html_instructions;

    public Dati(String distance, String duration, String html_instructions) {
        this.distance = distance;
        this.duration = duration;
        this.html_instructions=html_instructions;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public String getHtml_instructions() {
        return html_instructions;
    }

    public void setHtml_instructions(String html_instructions) {
        this.html_instructions = html_instructions;
    }
}
