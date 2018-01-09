package com.geo.service;

import io.dropwizard.jersey.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by dbarr on 12/10/17.
 */

@Path("/service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServiceResource {

    private TestData testData = new TestData();

    @GET
    @Path("topic/{topicName}/next")
    public LocationDTO getNextGeoPoint(@PathParam("topicName") String topicName, @QueryParam("clientId") String clientId) throws InterruptedException {
        LocationDTO loc = null;
        System.out.println("topicName = " + topicName);
        System.out.println("clientId = " + clientId);
        try {
            loc = KafkaManager.get().getNextMessage(topicName, clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("loc = " + loc);
        if (loc == null)
            throw new NotFoundException("No data found.");
        return loc;
    }


    /**
     * API to enable/disable a rule
     *
     * @param topicName
     * @return
     */
    @PATCH
    @Path("topic/{topicName}/reset")
    public Response resetTopicConsumer(@PathParam("topicName") String topicName, @QueryParam("clientId") String clientId) throws IOException, InterruptedException {
        KafkaManager.get().reset(topicName, clientId);
        return Response.ok("Topic reset.").build();
    }

    /**
     * Health API
     *
     * @return
     */
    @GET
    @Path("health")
    public Response getScore() {
        return Response.ok("OK: Service is Up !").build();
    }

}
