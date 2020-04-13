package cwl.kill.model.mapper;

import cwl.kill.model.entity.RandomCode;

import org.springframework.stereotype.Repository;

@Repository
public interface RandomCodeMapper {
    //long countByExample(RandomCodeExample example);

    //int deleteByExample(RandomCodeExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(RandomCode record);

    int insertSelective(RandomCode record);

    //List<RandomCode> selectByExample(RandomCodeExample example);

    RandomCode selectByPrimaryKey(Integer id);

    //int updateByExampleSelective(@Param("record") RandomCode record, @Param("example") RandomCodeExample example);

    //int updateByExample(@Param("record") RandomCode record, @Param("example") RandomCodeExample example);

    int updateByPrimaryKeySelective(RandomCode record);

    int updateByPrimaryKey(RandomCode record);
}