package com.chatfood.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Recommendation {

    // Python의 "food_name" JSON 키를 이 필드에 매핑합니다.
    @JsonProperty("food_name")
    private String foodName;

    // Python의 "price_min" JSON 키를 이 필드에 매핑합니다.
    @JsonProperty("price_min")
    private int priceMin;

    // Python의 "price_max" JSON 키를 이 필드에 매핑합니다.
    @JsonProperty("price_max")
    private int priceMax;
}

