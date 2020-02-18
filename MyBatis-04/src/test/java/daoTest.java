import com.zhuuu.dao.Mapper;
import com.zhuuu.pojo.User;
import com.zhuuu.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.apache.log4j.Logger;

import java.util.List;



public class daoTest {

    static Logger logger = Logger.getLogger(daoTest.class);


    public void selectUser(){
        logger.info("info:进入了selectUser方法");
        logger.debug("debug:进入了selectUser方法");
        logger.error("error:进入了selectUser方法");
    }



    @Test
    public void test(){

//          获取session对象
        SqlSession session = MybatisUtils.getSession();

//          执行sql
        Mapper mapper = session.getMapper(Mapper.class);
        List<User> userList = mapper.getUserList();

        for (User user : userList) {
            System.out.println(user);
        }


//        关闭sqlsession
        session.close();
    }
    @Test
    public void getUserByID(){
        logger.info("info:进入了selectUser方法");
        logger.debug("debug:进入了selectUser方法");
        logger.error("error:进入了selectUser方法");
        SqlSession session = MybatisUtils.getSession();

        Mapper mapper = session.getMapper(Mapper.class);
        User userbyID = mapper.getUserbyID(1);
        System.out.println(userbyID);

        session.close();
    }

}
