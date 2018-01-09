package com.demo.geo.supplydemand;

import com.demo.commons.GeoUtil;
import com.demo.commons.pojo.OutputRecord;
import com.github.davidmoten.geo.LatLong;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;
import processing.core.PFont;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * When user hovers over the marker its label is displayed.
 * <p>
 * The highlight check is done manually for the marker in mouseMoved().
 */
public class SupplyDemandGeoVisualization extends PApplet {

    private String fontFile = "/Users/dbarr/coderep_2/Workspace_bigdata/bigdata-projects/geo-visualization/src/main/resources/Lato-Bold-14.vlw";
    UnfoldingMap map;
    Set<String> geoSet = new HashSet();

    //    Location nyc = new Location(40.68061065673828f, -73.92864227294922f);
    Location nyc = new Location(40.71258545f, -73.94348145f);
    PFont font;
    private KafkaPoller poller = new KafkaPoller();

    public void setup() {
        size(800, 600, OPENGL);

        map = new UnfoldingMap(this, new Google.GoogleMapProvider());
//        map = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
        map.zoomToLevel(12);
        map.panTo(nyc);
        MapUtils.createDefaultEventDispatcher(this, map);
        font = loadFont(fontFile);
    }

    public void draw() {
        updateMarkers(poller);
        map.draw();
    }

    private void updateMarkers(KafkaPoller poller) {
        Map<String, OutputRecord> recordMap;
        try {
            recordMap = poller.runConsumer();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Now adding markers to map....");
        LabeledMarker marker;
        for (String key : recordMap.keySet()) {

            if (geoSet.contains(key)) {
                System.out.println("Key already exists. Keys = " + geoSet.size());
                continue;
            }
            LatLong latLong = GeoUtil.toLatLong(key);
            marker = new LabeledMarker(new Location(latLong.getLat(), latLong.getLon()), createLabel(recordMap.get(key)), font, 12);
            marker.setColor(orange());
            marker.setRadius(10);
            map.addMarkers(marker);
            geoSet.add(key);
        }
    }

    private String createLabel(OutputRecord record) {
        return String.format("%.2f", record.getRatio()) + ", " + record.getWeather();
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

    public int yellow() {
        return color(255, 255, 0);
    }

    public int orange() {
        return color(255, 128, 0);
    }

    public int red() {
        return color(206, 54, 59);
    }
}

