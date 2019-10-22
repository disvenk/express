import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by wanghua on 17/3/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:spring/applicationContext*.xml","classpath*:spring/bootstrap-core.xml"})
public class VerifyCacheTest {
    @Test
    public void getVerify() throws Exception {
        //VerifyCache.putVerificationCode("15306209803", "123678");
        //String val = VerifyCache.getVerificationCode("15306209803");
        //Long l = VerifyCache.deleteVerificationCode("15306209803");
        //System.out.println(l);
        //Assert.assertEquals("123678", val);
    }
}
