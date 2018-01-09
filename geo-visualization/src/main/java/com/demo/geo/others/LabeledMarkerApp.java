package com.demo.geo.others;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;
import processing.core.PFont;

import java.util.List;

/**
 * When user hovers over the marker its label is displayed.
 * <p>
 * The highlight check is done manually for the marker in mouseMoved().
 */
public class LabeledMarkerApp extends PApplet {

    private String fontFile = "/Users/dbarr/coderep_2/Workspace_bigdata/bigdata-projects/geo-util/src/main/resources/OpenSans-12.vlw";
    UnfoldingMap map;

    Location nyc = new Location(40.68061065673828f, -73.92864227294922f);
    LabeledMarker berlinMarker;
    PFont font;
    private KafkaPoller poller = new KafkaPoller();

    public void setup() {
        size(800, 600, OPENGL);

        map = new UnfoldingMap(this, new Google.GoogleMapProvider());
        map.zoomToLevel(12);
        map.panTo(nyc);
        MapUtils.createDefaultEventDispatcher(this, map);

        font = loadFont(fontFile);
//        berlinMarker = new LabeledMarker(berlinLocation, "Berlin", font, 10);
//        map.addMarkers(berlinMarker);
    }

    public void draw() {

        updateMarkers(poller);
//        background(240);
        map.draw();
    }

    private void updateMarkers(KafkaPoller poller) {
        List<BaseRecord> recordList;
        try {
            recordList = poller.runConsumer();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Now adding markers to map....");
        LabeledMarker marker;
//        SimplePointMarker marker;
        for (BaseRecord br : recordList) {
            marker = new LabeledMarker(new Location(br.getCurr_longitude(), br.getCurr_latitude()), br.getId(), font, 10);
            System.out.println("br = " + br);
//            marker = new SimplePointMarker(new Location(br.getCurr_longitude(), br.getCurr_latitude()));
            marker.setColor(color(255, 255, 0));
            marker.setRadius(10);
            map.addMarkers(marker);
        }
    }

    /**
     * Check for hit test directly with marker.
     */
    public void mouseMoved() {

        for (Marker marker : map.getMarkers())
            if (marker.isInside(map, mouseX, mouseY)) {
                marker.setSelected(true);
            } else {
                marker.setSelected(false);
            }
    }

    @Override
    public void destroy() {
        poller.close();
        super.destroy();
    }

}

