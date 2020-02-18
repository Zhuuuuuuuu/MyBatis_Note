import com.zhuuu.dao.Mapper;
import com.zhuuu.pojo.User;
import com.zhuuu.utils.MybatisUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

public class daoTest {
    //limit实现分页
    @Test
    public void test(){
        SqlSession session = MybatisUtils.getSession();


        Mapper mapper = session.getMapper(Mapper.class);

        int curentPage = 1;//第几页
        int pageSize = 2;//每页显示几个

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("startIndex",(curentPage-1)*pageSize);
        map.put("pageSize",pageSize);


        List<User> userList = mapper.getUserList(map);
        for (User user : userList) {
            System.out.println(user);
        }


        session.close();


    }


    //RowBounds实现分页
    @Test
    public void RowBoundstest(){
        SqlSession session = MybatisUtils.getSession();
        int currentPage = 2;
        int pageSize = 2;

        RowBounds rowBounds = new RowBounds((currentPage - 1) * pageSize, pageSize);


        List<User> users  = session.selectList("com.zhuuu.dao.Mapper.getUserByRowBounds", null, rowBounds);
        for (User user : users) {
            System.out.println(user);
        }

    }

}
