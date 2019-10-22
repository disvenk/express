import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by wanghua on 17/3/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:spring/applicationContext*.xml","classpath*:spring/bootstrap-core.xml"})
public class RedisPoolTest {
    @Test
    public void testRedisPool() throws Exception {
//        long start = new Date().getTime();
//        for (int i = 0; i < 100; i++) {
//            System.out.println(String.valueOf(i) + "," + VerifyCache.getVerificationCode("15306209803"));
//        }
//        long end = new Date().getTime();
//        System.out.println("耗时:" + ((double)(end - start))/1000 + "s");
    }
}
