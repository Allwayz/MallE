package cn.allwayz.product;

import cn.allwayz.product.entity.SpuInfoEntity;
import cn.allwayz.product.service.SpuInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class MalleProductApplicationTests {
	@Resource
	SpuInfoService spuInfoService;

	@Test
	void contextLoads() {
	}

	@Test
	public void updateSkuName(){
		List<SpuInfoEntity> spuInfoEntities = spuInfoService.list();
		for (SpuInfoEntity spuInfoEntity:spuInfoEntities) {
			if (spuInfoEntity.getBrandId() == 2){
				spuInfoEntity.setSpuName("Apple "+spuInfoEntity.getSpuName());
				spuInfoService.updateById(spuInfoEntity);
			}
		}
	}

}
