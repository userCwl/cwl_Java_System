package cwl.kill.model.mapper;

import cwl.kill.model.entity.User;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    //long countByExample(UserExample example);

    //int deleteByExample(UserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    //List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(Integer id);

    //int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    //int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUserName(@Param("userName")String userName);

    User selectByUserNamePsd(@Param("userName")String userName,@Param("password")String password);
}