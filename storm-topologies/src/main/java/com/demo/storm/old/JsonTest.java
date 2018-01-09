package com.demo.storm.old;

import com.demo.commons.pojo.OutputRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dbarr on 12/23/17.
 */
public class JsonTest {

    private static JSONParser parser = new JSONParser();
    private static ObjectMapper mapper = new ObjectMapper();

    private static String json = "{\"customer_id\":\"CT11001\",\"timestamp\":\"2016-01-01 00:29:24\",\"curr_latitude\":-73.92864227294922,\"curr_longitude\":40.68061065673828}";

    public static void main(String[] args) throws ParseException, JsonProcessingException, java.text.ParseException {

        JSONObject jsonObject = (JSONObject) parser.parse(json);
        String customerId = (String) jsonObject.get("customer_id");
        String driver_id = (String) jsonObject.get("driver_id");

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date ts = formatter.parse((String) jsonObject.get("timestamp"));
        System.out.println("ts = " + ts);
        System.out.println("ts.getTime() = " + ts.getTime());

        System.out.println("driver_id = " + driver_id);
        System.out.println("customerId = " + customerId);

        System.out.println(mapper.writeValueAsString(new OutputRecord(10l, 10l, 1.1d, "Good")));


    }
}
