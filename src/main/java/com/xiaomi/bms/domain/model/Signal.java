package com.xiaomi.bms.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Signal {
    @JsonProperty("Mx")
    private double mx;

    @JsonProperty("Mi")
    private double mi;

    @JsonProperty("Ix")
    private double ix;

    @JsonProperty("Ii")
    private double ii;
}