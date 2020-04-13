package cwl.kill.model.mapper;

import cwl.kill.model.dto.KillSuccessUserInfo;
import cwl.kill.model.entity.ItemKillSuccess;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemKillSuccessMapper {
    //long countByExample(ItemKillSuccessExample example);

    //int deleteByExample(ItemKillSuccessExample example);

    int deleteByPrimaryKey(String code);

    int insert(ItemKillSuccess record);

    int insertSelective(ItemKillSuccess record);

    //List<ItemKillSuccess> selectByExample(ItemKillSuccessExample example);

    ItemKillSuccess selectByPrimaryKey(String code);

    //int updateByExampleSelective(@Param("record") ItemKillSuccess record, @Param("example") ItemKillSuccessExample example);

    //int updateByExample(@Param("record") ItemKillSuccess record, @Param("example") ItemKillSuccessExample example);

    int updateByPrimaryKeySelective(ItemKillSuccess record);

    int updateByPrimaryKey(ItemKillSuccess record);

    int countByKillUserId(@Param("killId") Integer killId, @Param("userId") Integer userId);

    KillSuccessUserInfo selectByCode(@Param("code") String code);

    int expireOrder(@Param("code") String code);

    List<ItemKillSuccess> selectExpireOrders();
}