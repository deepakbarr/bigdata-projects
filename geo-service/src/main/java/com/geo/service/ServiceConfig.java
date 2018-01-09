package com.geo.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

/**
 * Created by dbarr on 12/10/17.
 */
public class ServiceConfig extends Configuration {

    @NotNull
    @JsonProperty
    private String kafkaBroker;

    public String getKafkaBroker() {
        return kafkaBroker;
    }

    public void setKafkaBroker(String kafkaBroker) {
        this.kafkaBroker = kafkaBroker;
    }
}
