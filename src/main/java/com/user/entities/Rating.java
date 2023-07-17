package com.user.entities;

import com.user.apiDto.Hotel;
import lombok.Data;

@Data
public class Rating {

    private String ratingId;
    private String userId;
    private String hotelId;
    private int rating;
    private String review;
    private Hotel hotel;
}
