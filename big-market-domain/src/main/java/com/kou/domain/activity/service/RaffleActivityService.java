package com.kou.domain.activity.service;

import com.kou.domain.activity.repository.IActivityRepository;
import org.springframework.stereotype.Service;

/**
 * @author KouJY
 * Date: 2024/8/2 15:20
 * Package: com.kou.domain.activity.service
 */
@Service
public class RaffleActivityService extends AbstractRaffleActivity{

    public RaffleActivityService(IActivityRepository activityRepository) {
        super(activityRepository);
    }
}
