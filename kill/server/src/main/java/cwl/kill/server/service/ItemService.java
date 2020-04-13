package cwl.kill.server.service;

import cwl.kill.model.entity.ItemKill;

import java.util.List;

/**
 * @Author long
 * @Date 2020/3/7 20:24
 */
public interface ItemService {

    List<ItemKill> getKillItems() throws Exception;

    ItemKill getKillDetail(Integer id) throws Exception;
}
