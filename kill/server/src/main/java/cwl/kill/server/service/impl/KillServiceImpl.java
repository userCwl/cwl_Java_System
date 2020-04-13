package cwl.kill.server.service.impl;

import cwl.kill.model.entity.ItemKill;
import cwl.kill.model.entity.ItemKillSuccess;
import cwl.kill.model.mapper.ItemKillMapper;
import cwl.kill.model.mapper.ItemKillSuccessMapper;
import cwl.kill.server.enums.SysConstant;
import cwl.kill.server.service.KillService;
import cwl.kill.server.service.RabbitSenderService;
import cwl.kill.server.utils.RandomUtil;
import cwl.kill.server.utils.SnowFlake;
import org.joda.time.DateTime;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author long
 * @Date 2020/3/8 11:37
 */
@Service
public class KillServiceImpl implements KillService {

    private static final Logger log = LoggerFactory.getLogger(KillServiceImpl.class);

    private SnowFlake snowFlake=new SnowFlake(2,3);

    private int count1 = 0,count2 = 0,count3 = 0,count4 = 0;// 测试用

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;
    @Autowired
    private ItemKillMapper itemKillMapper;
    @Autowired
    private RabbitSenderService rabbitSenderService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 记录用户秒杀成功后生成的订单-并进行异步邮件消息的通知
     * @param itemKill
     * @param userId
     * @throws Exception
     */
    public void commonRecordKillSuccessInfo(ItemKill itemKill, Integer userId) throws Exception {

        ItemKillSuccess entity = new ItemKillSuccess();
        String orderNo=String.valueOf(snowFlake.nextId());

        //entity.setCode(RandomUtil.generateOrderCode());//传统时间戳+N位随机数
        entity.setCode(orderNo);//使用雪花算法
        entity.setItemId(itemKill.getItemId());//关联着商品详情的ID
        entity.setCreateTime(DateTime.now().toDate());//时间
        entity.setKillId(itemKill.getId());//关联着待秒杀商品的ID
        entity.setUserId(userId.toString());//用户ID
        entity.setStatus(SysConstant.OrderStatus.SuccessNotPayed.getCode().byteValue());//设置状态



        // MySql层的优化-仿照单例模式的双重检验锁写法
        if(itemKillSuccessMapper.countByKillUserId(itemKill.getId(),userId) <= 0){
            int res = itemKillSuccessMapper.insertSelective(entity);

            count2++;//测试多少个订单执行插入操作
            System.out.println("执行订单插入的操作数count2: "+count2);

            if(res > 0){

                count3++;//测试多少个订单执行插入操作成功
                System.out.println("订单插入成功数count3: "+count3);


                //进行异步邮件消息的通知=rabbitmq+mail
                rabbitSenderService.sendKillSuccessEmailMsg(orderNo);
                //加入死信队列，用于“失效”超过指定的TTL时间仍未支付的订单
                rabbitSenderService.sendKillSuccessOrderExpireMsg(orderNo);
            }
        }
    }

    /**
     * 商品秒杀核心业务逻辑的处理
     * @param killId
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public Boolean killItem(Integer killId, Integer userId) throws Exception {
        Boolean result = false;

        //判断当前用户是否已经抢购过当前商品
        if(itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0){
            //查询待秒杀商品详情
            ItemKill itemKill = itemKillMapper.selectById(killId);
            //判断是否可以被秒杀，即canKill=1？
            if(itemKill != null && itemKill.getCanKill() == 1){                //扣减库存，减一
                int res = itemKillMapper.updateKillItem(killId);

                count4++;//测试多少个用户尝试更新库存
                System.out.println("尝试更新库存数count4: "+count4);


                //判断扣减是否成功？是-生成订单，同时通知用户秒杀成功的消息
                if(res >0 ){

                    count1++;//测试多少个线程走到这（更新库存成功）
                    System.out.println("更新库存成功数count1: "+count1);

                    commonRecordKillSuccessInfo(itemKill,userId);
                    result = true;
                }
            }
        }else{
            throw new Exception("您已经抢购过该商品了！");
        }
        return result;
    }

    /**
     * 商品秒杀核心业务逻辑的处理-mysql的优化
     * @param killId
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public Boolean killItemV2(Integer killId, Integer userId) throws Exception {
        Boolean result = false;

        //TODO:判断当前用户是否已经抢购过当前商品
        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
            //TODO:A.查询待秒杀商品详情
            ItemKill itemKill=itemKillMapper.selectByIdV2(killId);

            //TODO:判断是否可以被秒杀canKill=1?优化：itemKill.getTotal()>0
            if (itemKill!=null && 1==itemKill.getCanKill() && itemKill.getTotal()>0){
                //TODO:B.扣减库存-减一
                int res=itemKillMapper.updateKillItemV2(killId);

                //TODO:扣减是否成功?是-生成秒杀成功的订单，同时通知用户秒杀成功的消息
                if (res>0){
                    commonRecordKillSuccessInfo(itemKill,userId);

                    result=true;
                }
            }
        }else{
            throw new Exception("您已经抢购过该商品了!");
        }
        return result;
    }

    /**
     * 商品秒杀核心业务的逻辑处理-redis的分布式锁
     * @param killId
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public Boolean killItemV3(Integer killId, Integer userId) throws Exception {
        Boolean result = false;

        //判断当前用户是否已经抢购过当前商品
        if(itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0){

            // 借助Redis的原子操作实现分布式锁-对共享操作-资源进行控制
            ValueOperations valueOperations = stringRedisTemplate.opsForValue();
            // 根据一个用户只能抢购一个商品得出唯一key
            final String key = new StringBuffer().append(killId).append(userId).append("-RedisLock").toString();
            final String value = RandomUtil.generateOrderCode();// value无实际意义
            Boolean cacheRes = valueOperations.setIfAbsent(key,value);//若不存在此key，则创建并返回true，否则返回false
            //当执行到上面，如果服务挂了，就无法为创建的这个key设置过期时间
            if(cacheRes){

                /*count1++;//测试多少个线程走到这（更新库存成功）
                System.out.println("count1: "+count1);*/


                // key 30s到期
                stringRedisTemplate.expire(key,30, TimeUnit.SECONDS);
                try{
                    ItemKill itemKill = itemKillMapper.selectById(killId);
                    if(itemKill != null && itemKill.getCanKill() == 1 /*&& itemKill.getTotal()>0*/) {

                        count1++;//测试多少个线程走到这（更新库存）
                        System.out.println("count1: "+count1);

                        int res = itemKillMapper.updateKillItem(killId);



                        if (res > 0) {

                            count1++;//测试多少个线程走到这（更新库存成功）
                            System.out.println("更新库存成功数count1: "+count1);

                            commonRecordKillSuccessInfo(itemKill, userId);



                            result = true;
                        }
                    }
                }catch (Exception e){
                        throw new Exception("还没到抢购日期、已过了抢购时间或已被抢购完毕！");
                    }finally {
                        if (value.equals(valueOperations.get(key).toString())){
                            stringRedisTemplate.delete(key);// 删除此value对应的key
                        }
                }
            }
        }else{
            throw new Exception("您已经抢购过该商品了！");
        }
        return result;
    }


    @Autowired
    private RedissonClient redissonClient;
    /**
     * 商品秒杀核心业务逻辑的处理-redisson的分布式锁(解决了killItemV3的服务挂了key一直存在的缺陷)
     * @param killId
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public Boolean killItemV4(Integer killId, Integer userId) throws Exception {
        Boolean result = false;

        final String lockKey = new StringBuffer().append(killId).append(userId).append("-RedissonLock").toString();
        RLock lock = redissonClient.getLock(lockKey);// 获取锁

        try{
            Boolean cacheRes = lock.tryLock(30,10,TimeUnit.SECONDS);//最多等待30s，锁有效期10s
            if(cacheRes){
                // 核心业务逻辑的处理
                if(itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
                    ItemKill itemKill = itemKillMapper.selectByIdV2(killId);
                    if(itemKill != null && itemKill.getCanKill() == 1 && itemKill.getTotal()>0){
                        int res = itemKillMapper.updateKillItemV2(killId);
                        if(res>0){
                            commonRecordKillSuccessInfo(itemKill,userId);
                            result = true;
                        }
                    }
                }else {
                    throw new Exception("redisson-您已经购买过该商品了！");
                }
            }
        }finally {
            lock.unlock();
        }

        return result;
    }


}
