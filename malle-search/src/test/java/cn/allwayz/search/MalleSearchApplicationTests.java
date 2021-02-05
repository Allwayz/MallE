package cn.allwayz.search;

import cn.allwayz.search.config.MalleElasticSearchConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class MalleSearchApplicationTests {

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Test
	void contextLoads() {
		System.out.println(restHighLevelClient);
	}

	/**
	 * 测试给ES中存储数据
	 * 更新也可以
	 */
	@Test
	void insertToEs() throws IOException {
		IndexRequest indexRequest = new IndexRequest("users");
		indexRequest.id("1");
		indexRequest.source("userName","Kobe","age",18,"gender","unknow");

		//执行操作
		IndexResponse indexResponse = restHighLevelClient.index(indexRequest, MalleElasticSearchConfig.COMMON_OPTIONS);
		//提取有用的相应数据
		System.out.println(indexResponse);
	}

	@Test
	void selectES() throws Exception{
		//创建检索请求
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchRequest.indices("bank");

		searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
		System.out.println(searchSourceBuilder.toString());

		searchRequest.source(searchSourceBuilder);

		//执行检索请求
		SearchResponse searchResponse = restHighLevelClient.search(searchRequest,MalleElasticSearchConfig.COMMON_OPTIONS);

		//拿到响应数据，分析结果
		System.out.println(searchResponse.toString());
	}

}
