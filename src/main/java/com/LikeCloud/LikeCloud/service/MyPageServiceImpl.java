package com.LikeCloud.LikeCloud.service;

import com.LikeCloud.LikeCloud.domain.entity.DailyPlan;
import com.LikeCloud.LikeCloud.domain.entity.ShortPlan;
import com.LikeCloud.LikeCloud.domain.entity.User;
import com.LikeCloud.LikeCloud.domain.entity.YearPlan;
import com.LikeCloud.LikeCloud.dto.*;
import com.LikeCloud.LikeCloud.repository.DailyPlanRepository;
import com.LikeCloud.LikeCloud.repository.ShortPlanRepository;
import com.LikeCloud.LikeCloud.repository.UserRepository;
import com.LikeCloud.LikeCloud.repository.YearPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MyPageServiceImpl implements MyPageService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    YearPlanRepository yearPlanRepository;

    @Autowired
    ShortPlanRepository shortPlanRepository;

    @Autowired
    DailyPlanRepository dailyPlanRepository;

    @Override
    public DoingPlanResponseDto getPlans(Long userId) {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User를 찾을 수 없습니다."));

        List<YearPlanResponseDto> yearPlans = yearPlanRepository.findByUserId(userId)
                .stream()
                .filter(yearPlan -> !yearPlan.getDone())
                .map(YearPlanResponseDto::new)
                .collect(Collectors.toList());

        List<ShortPlanResponseDto> shortPlans = shortPlanRepository.findByUserId(userId)
                .stream()
                .filter(shortPlan -> !shortPlan.getDone())
                .map(ShortPlanResponseDto::new)
                .collect(Collectors.toList());

        return new DoingPlanResponseDto(yearPlans, shortPlans);
    }

    @Override
    public void deletePlan(Long planId) {
        // 삭제할 목표가 YearPlan인지 ShortPlan인지 확인
        Optional<YearPlan> yearPlanOptional = yearPlanRepository.findById(planId);
        if (yearPlanOptional.isPresent()) {
            // YearPlan 삭제
            yearPlanRepository.deleteById(planId);
            return;
        }

        Optional<ShortPlan> shortPlanOptional = shortPlanRepository.findById(planId);
        if (shortPlanOptional.isPresent()) {
            // ShortPlan 삭제
            shortPlanRepository.deleteById(planId);
            return;
        }

        // 해당 ID에 대한 목표가 존재하지 않는 경우 예외 처리
        throw new RuntimeException("해당 ID에 대한 목표를 찾을 수 없습니다.");
    }

    @Override
    public DonePlanResponseDto getDonePlans(Long userId) {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User를 찾을 수 없습니다."));

        List<YearPlanResponseDto> yearPlans = yearPlanRepository.findByUserId(userId)
                .stream()
                .filter(yearPlan -> yearPlan.getDone())
                .map(YearPlanResponseDto::new)
                .collect(Collectors.toList());

        List<ShortPlanResponseDto> shortPlans = shortPlanRepository.findByUserId(userId)
                .stream()
                .filter(shortPlan -> shortPlan.getDone())
                .map(ShortPlanResponseDto::new)
                .collect(Collectors.toList());

        return new DonePlanResponseDto(yearPlans, shortPlans);
    }

    @Override
    public List<DailyPlanResponseDto> getDailyPlansByPlanId(Long userId, Long planId) {
        // YearPlan 또는 ShortPlan 찾기
        YearPlan yearPlan = yearPlanRepository.findById(planId).orElse(null);
        ShortPlan shortPlan = shortPlanRepository.findById(planId).orElse(null);

        if (yearPlan == null && shortPlan == null) {
            throw new RuntimeException("해당 ID에 대한 목표를 찾을 수 없습니다.");
        }

        // DailyPlans 조회
        List<DailyPlan> dailyPlans;
        if (yearPlan != null) {
            // YearPlan의 경우
            dailyPlans = dailyPlanRepository.findByYearPlanId(planId);
        } else {
            // ShortPlan의 경우
            dailyPlans = dailyPlanRepository.findByShortPlanId(planId);
        }

        // DailyPlan을 DTO로 변환
        return dailyPlans.stream()
                .map(dailyPlan -> new DailyPlanResponseDto(dailyPlan.getDay(), dailyPlan.getPlan()))
                .collect(Collectors.toList());
    }
}
