package cn.allwayz.search;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MalleSearchApplicationTests {

	@Autowired
	private RestHighLevelClient restHighLevelClient;
	@Test
	void contextLoads() {
		System.out.println(restHighLevelClient);
	}

}
