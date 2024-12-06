package com.bangbangbwa.backend.domain.sns.business;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class PostRecommendationStrategy {

    private final ThreadLocalRandom rand = ThreadLocalRandom.current();

    // 각 게시물에 대한 확률 (팔로우한 사람, 비슷한 태그, 랜덤)
    private static final int[][] probabilities = {
            {70, 20, 10}, // 첫 번째 게시물
            {60, 25, 15}, // 두 번째 게시물
            {50, 30, 20}, // 이후    게시물
    };


    public int[] getPostReturnCount(int postSize) {
        int[] result = {0, 0, 0}; // [0: 팔로우한 사람, 1: 비슷한 태그, 2: 랜덤]

        for (int i = 0; i < postSize; i++) {
            int randomValue = rand.nextInt(100);
            int[] currentProbabilities = probabilities[Math.min(i, probabilities.length - 1)];

            int followProbability = currentProbabilities[0];
            int tagProbability = followProbability + currentProbabilities[1];

            if (randomValue < followProbability) {
                result[0]++;
            } else if (randomValue < tagProbability) {
                result[1]++;
            } else {
                result[2]++;
            }
        }
        return result;
    }
}
