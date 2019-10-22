import com.xxx.utils.MD5Utils;

import java.util.Arrays;


/**
 * Created with IntelliJ IDEA.
 * User:
 * Date: 2017/3/10/0010
 * Time: 18:04
 */
public class ImageUpload {
	public static void main(String[] args) {
		//把本地文件转 base64
		/*String base64 = Base64ImageUtils.getImageBase64Str
				("D:\\ideaworkspace\\workspace\\业务相关\\文档\\图片库(2)\\图片\\新建文件夹\\养生\\610686483030059322.jpg");
		//上传
		// id , base64, 格式
		OSSClientUtil.putBase64Img(123000011 + "", base64, "png");
		//url
		System.out.println(OSSClientUtil.getObjectUrl(123000011 + ""));*/

		demo();
		//test();
	}


	private static void demo() {
		String value = " 中国 . 北京 . 海淀 . 学院路 ";
		String[] aa = value.split("\\.");
		System.out.println(Arrays.toString(aa));
//
//		Long l = null;
//		System.out.println(l + "");
		/*Double d = 95.0;
		System.out.println(d / 100);*/
		System.out.println(MD5Utils.md5Hex("000000"));

	}
}
