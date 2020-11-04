package cn.allwayz.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class MalleThirdPartyApplicationTests {

	@Autowired
	private OSSClient ossClient;

	@Test
	void contextLoads() {
	}

	@Test
	public void uploadFileTest(){
		try {
			InputStream inputStream = new FileInputStream("/Users/allwayz/Desktop/截屏2020-11-02 14.18.55.png");
			ossClient.putObject("malle","picture.png",inputStream);
			ossClient.shutdown();
			System.out.println("Upload success...");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
