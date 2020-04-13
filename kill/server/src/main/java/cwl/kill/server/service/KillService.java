package cwl.kill.server.service;

import cwl.kill.model.entity.ItemKill;

/**
 * @Author long
 * @Date 2020/3/8 11:35
 */
public interface KillService {

    Boolean killItem(Integer killId,Integer userId) throws Exception;

    Boolean killItemV2(Integer killId,Integer userId) throws Exception;

    Boolean killItemV3(Integer killId,Integer userId) throws Exception;

    Boolean killItemV4(Integer killId,Integer userId) throws Exception;

}
