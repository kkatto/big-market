# big-market 大营销系统

## 开发日志

- [x] 240923-zookeeper-enable 增加 Zookeeper 对 dcc 动态值启动配置操作，可以在不需要时关闭
- [x] fix-240923-clear-queue-by-sku 队列指定sku，避免全部清空。
- [x] fix-240923-decr-zero 库存扣减到0bug修复，避免丢失1个库存。
- [x] 增加多线程场景 2024年08月31日 `UpdateActivitySkuStockJob` 增加多线程场景
- [x] 权重抽奖只给一次，`>=` 修改为等于，增加两种算法策略。
- [x] 在后续阶段增加限流设计
- [x] convert逻辑校验 0.0018 返回值确认
