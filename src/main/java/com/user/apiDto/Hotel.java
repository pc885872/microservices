package com.user.apiDto;

import com.user.entities.Rating;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
public class Hotel {
    @Id
    private String hotelId;
    private String name;
    private String location;
    private String email;
    private String about;
}
