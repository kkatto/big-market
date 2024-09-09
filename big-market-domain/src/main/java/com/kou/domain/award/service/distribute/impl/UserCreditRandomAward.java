package com.kou.domain.award.service.distribute.impl;

import com.kou.domain.award.model.aggregate.GiveOutPrizesAggregate;
import com.kou.domain.award.model.entity.DistributeAwardEntity;
import com.kou.domain.award.model.entity.UserAwardRecordEntity;
import com.kou.domain.award.model.entity.UserCreditAwardEntity;
import com.kou.domain.award.model.valobj.AwardStateVO;
import com.kou.domain.award.repository.IAwardRepository;
import com.kou.domain.award.service.distribute.IDistributeAward;
import com.kou.types.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * @author KouJY
 * Date: 2024/9/9 11:10
 * Package: com.kou.domain.award.service.distribute.impl
 *
 */
@Component("user_credit_random")
public class UserCreditRandomAward implements IDistributeAward {

    @Resource
    private IAwardRepository awardRepository;

    @Override
    public void giveOutPrizes(DistributeAwardEntity distributeAwardEntity) {
        // 奖品ID
        Integer awardId = distributeAwardEntity.getAwardId();
        // 查询奖品配置 「优先走透传的随机积分奖品配置」
        String awardConfig = distributeAwardEntity.getAwardConfig();
        if (StringUtils.isBlank(awardConfig)) {
            awardConfig = awardRepository.queryAwardConfig(awardId);
        }

        String[] creditRange = awardConfig.split(Constants.SPLIT);
        if (2 != creditRange.length) {
            throw new RuntimeException("award_config 「" + awardConfig + "」配置不是一个范围值，如 1,100");
        }

        // 生成随机积分值
        BigDecimal creditAmount = this.generateRandom(new BigDecimal(creditRange[0]), new BigDecimal(creditRange[1]));

        // 构建聚合对象
        UserAwardRecordEntity userAwardRecordEntity = GiveOutPrizesAggregate.buildDistributeUserAwardRecordEntity(
                distributeAwardEntity.getUserId(),
                distributeAwardEntity.getOrderId(),
                distributeAwardEntity.getAwardId(),
                AwardStateVO.complete
        );

        UserCreditAwardEntity userCreditAwardEntity = GiveOutPrizesAggregate.buildUserCreditAwardEntity(
                distributeAwardEntity.getUserId(),
                creditAmount
        );

        GiveOutPrizesAggregate giveOutPrizesAggregate = GiveOutPrizesAggregate.builder()
                .userId(distributeAwardEntity.getUserId())
                .userAwardRecordEntity(userAwardRecordEntity)
                .userCreditAwardEntity(userCreditAwardEntity)
                .build();

        // 存储发奖对象
        awardRepository.saveGiveOutPrizesAggregate(giveOutPrizesAggregate);
    }

    private BigDecimal generateRandom(BigDecimal min, BigDecimal max) {
        if (min.equals(max)) return min;
        BigDecimal randomBigDecimal = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return randomBigDecimal.round(new MathContext(3));
    }
}
