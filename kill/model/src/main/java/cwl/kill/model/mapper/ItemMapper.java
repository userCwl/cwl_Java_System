package cwl.kill.model.mapper;

import cwl.kill.model.entity.Item;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Item record);

    int insertSelective(Item record);

    //List<Item> selectByExample(ItemExample example);

    Item selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Item record);

    int updateByPrimaryKey(Item record);
}