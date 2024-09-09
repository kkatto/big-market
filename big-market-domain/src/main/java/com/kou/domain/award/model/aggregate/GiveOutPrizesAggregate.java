package com.kou.domain.award.model.aggregate;

import com.kou.domain.award.model.entity.UserAwardRecordEntity;
import com.kou.domain.award.model.entity.UserCreditAwardEntity;
import com.kou.domain.award.model.valobj.AwardStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author KouJY
 * Date: 2024/9/9 11:28
 * Package: com.kou.domain.award.model.aggregate
 *
 * 发放奖品聚合对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GiveOutPrizesAggregate {

    /** 用户ID */
    private String userId;
    /** 用户发奖记录 */
    private UserAwardRecordEntity userAwardRecordEntity;
    /** 用户积分奖品 */
    private UserCreditAwardEntity userCreditAwardEntity;

    public static UserAwardRecordEntity buildDistributeUserAwardRecordEntity(String userId, String orderId, Integer awardId, AwardStateVO awardStateVO) {
        return UserAwardRecordEntity.builder()
                .userId(userId)
                .orderId(orderId)
                .awardId(awardId)
                .awardState(awardStateVO)
                .build();
    }

    public static UserCreditAwardEntity buildUserCreditAwardEntity(String userId, BigDecimal creditAmount) {
        return UserCreditAwardEntity.builder()
                .userId(userId)
                .creditAmount(creditAmount)
                .build();
    }
}
