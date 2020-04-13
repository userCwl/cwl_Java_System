package cwl.kill.model.mapper;

import cwl.kill.model.entity.ItemKill;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemKillMapper {
    //long countByExample(ItemKillExample example);

    //int deleteByExample(ItemKillExample example);

    //int deleteByPrimaryKey(Integer id);

    //int insert(ItemKill record);

    //int insertSelective(ItemKill record);

    //List<ItemKill> selectByExample(ItemKillExample example);

    //ItemKill selectByPrimaryKey(Integer id);

    //int updateByExampleSelective(@Param("record") ItemKill record, @Param("example") ItemKillExample example);

    //int updateByExample(@Param("record") ItemKill record, @Param("example") ItemKillExample example);

    //int updateByPrimaryKeySelective(ItemKill record);

    //int updateByPrimaryKey(ItemKill record);

    List<ItemKill> selectAll();

    ItemKill selectById(@Param("id")Integer id);

    int updateKillItem(@Param("killId")Integer killId);

    ItemKill selectByIdV2(@Param("id")Integer id);

    int updateKillItemV2(@Param("killId")Integer killId);
}