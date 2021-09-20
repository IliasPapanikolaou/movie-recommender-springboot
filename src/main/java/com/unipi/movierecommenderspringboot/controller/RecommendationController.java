package com.unipi.movierecommenderspringboot.controller;

import com.unipi.movierecommenderspringboot.model.Recommendation;
import com.unipi.movierecommenderspringboot.model.UserRating;
import com.unipi.movierecommenderspringboot.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Recommendation>> getRecommendationsById(@PathVariable("id") String id) {
        List<Recommendation> recommendations = recommendationService.getRecommendationsById(id);
        return !recommendations.isEmpty() ? new ResponseEntity<>(recommendations, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<List<Recommendation>> getRecommendationsByUserRating(@RequestBody UserRating userRating) {
        List<Recommendation> recommendations = recommendationService.getRecommendationsByUserRating(userRating);
        return !recommendations.isEmpty() ? new ResponseEntity<>(recommendations, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
