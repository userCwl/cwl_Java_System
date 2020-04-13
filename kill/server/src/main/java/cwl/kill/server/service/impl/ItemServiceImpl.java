package cwl.kill.server.service.impl;

import cwl.kill.model.entity.ItemKill;
import cwl.kill.model.mapper.ItemKillMapper;
import cwl.kill.server.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author long
 * @Date 2020/3/7 20:24
 */
@Service
public class ItemServiceImpl implements ItemService {
    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    @Autowired
    private ItemKillMapper itemKillMapper;

    /**
     * 获取待秒杀商品列表
     * @return
     * @throws Exception
     */
    @Override
    public List<ItemKill> getKillItems() throws Exception {
        return itemKillMapper.selectAll();
    }

    /**
     * 获取待秒杀商品的详情
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public ItemKill getKillDetail(Integer id) throws Exception {
        ItemKill itemKill = itemKillMapper.selectById(id);
        if(itemKill == null){
            throw new Exception("获取待秒杀商品详情-记录不存在");
        }
        return itemKill;
    }
}
