package com.user.service.impl;

import com.user.apiDto.Hotel;
import com.user.entities.Rating;
import com.user.entities.User;
import com.user.exceptions.ResourceNotFound;
import com.user.external.HotelServiceFeign;
import com.user.repositories.UserRepository;
import com.user.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private HotelServiceFeign hotelServiceFeign;
    @Override
    public User saveUser(User user) {
        String userId  = UUID.randomUUID().toString();
        user.setUserId(userId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    int retryCount = 1;
    @Override
    //@CircuitBreaker(name="hotelRatingBreaker", fallbackMethod = "getUserByIdFallback")
  //  @Retry(name = "hotelRatingRetry", fallbackMethod = "getUserByIdFallback")

    public User getUserById(String id) {
        logger.info("Retry Count: {}", retryCount++);
        User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFound("User not found for id on " +
                                                                                              "server !!"));
        user.setRatings(getRatings(user.getUserId()));
        return user;
    }

    public User getUserByIdFallback(String id, Exception ex) {
        User user = User.builder().name("dummy").email("dummy@dummy.com").about("This is dummy because Rating service" +
                                                                                        " is down").build();
        return user;
    }

    private List<Rating> getRatings(String userId) {
        Rating[] ratingArray = restTemplate.getForObject("http://RATING-SERVICE/rating/ratingsByUserId/"+userId,
                Rating [].class);
        List<Rating> ratings = Arrays.stream(ratingArray).toList();

        ratings = ratings.stream().map(rating -> {
            Hotel hotel = hotelServiceFeign.getHotel(rating.getHotelId()).getBody();
            rating.setHotel(hotel);
            return rating;
        }).collect(Collectors.toList());
        return ratings;
    }
}
