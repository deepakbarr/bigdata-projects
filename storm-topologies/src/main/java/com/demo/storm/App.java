package com.demo.storm;

import java.util.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
//
//        List<Integer> list1 = Arrays.asList(1, 2, 3);
//        List<Integer> list2 = Arrays.asList(4, 5, 6);
//
//        list1.addAll(list2);
//
//        System.out.println("list1 = " + list1);

//        Map<String, List<Double>> map1 = new HashMap();
//        map1.put("A", getList(1d, 2d, 3d));
//        map1.put("B", getList(11d, 22d, 33d));
//
//        Map<String, List<Double>> map2 = new HashMap();
//        map2.put("A", getList(4d, 5d, 6d));
//        map2.put("C", getList(111d, 222d, 333d));
//
//        map1 = TrafficUtility.merge(map1, map2);
//
//        for (String key : map1.keySet())
//            System.out.println(String.format("Key = %s, Value %s", key, map1.get(key).toString()));

        String payload="{\"customer_id\":\"CT24020\",\"driver_id\":\"DR24020\",\"timestamp\":1514108947000, \"geohash\":\"abc\"}";
//        Client client = ClientBuilder.newClient();
//        WebTarget target=client.target("http://localhost:8200/v1/post/test_data");
//        String out=target.request().post();
//        System.out.println("out = " + out);
//
//        target.request().accept(MediaType.APPLICATION_JSON_TYPE).post(payload);



//        Calendar cal = DateUtils.truncate(Calendar.getInstance(), Calendar.MINUTE);
//        System.out.println("cal = " + cal.getTime());
//
//        System.out.println("latlong = " + GeoUtil.toLatLong("dr5rky"));
//
//        List<DriverBeacon> beacons = new ArrayList();
//        beacons.add(new DriverBeacon("A", new LatLong(1, 2), System.currentTimeMillis()));
//        Thread.sleep(100);
//        beacons.add(new DriverBeacon("B", new LatLong(1, 2), System.currentTimeMillis() - 1000));
//        Thread.sleep(100);
//
//        beacons.add(new DriverBeacon("C", new LatLong(1, 2), System.currentTimeMillis()));
//
//
//        TrafficUtility.sortByTimestamp(beacons);
//        for (DriverBeacon b : beacons)
//            System.out.println("b = " + b);

    }

    public static List<Double> getList(Double... values) {
        List<Double> list = new ArrayList();

        for (Double v : values)
            list.add(v);
        return list;
    }
}
