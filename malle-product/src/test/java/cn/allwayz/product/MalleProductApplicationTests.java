package cn.allwayz.product;

import cn.allwayz.product.entity.BrandEntity;
import cn.allwayz.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MalleProductApplicationTests {

	@Autowired
	BrandService brandService;

	@Test
	void contextLoads() {
//		BrandEntity brandEntity = new BrandEntity();
//		brandEntity.setName("HuaWei");
//		brandService.save(brandEntity);
//		System.out.printf("Save Successfully");
		List<BrandEntity> brandEntityList = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_ id",1));
		brandEntityList.forEach((item) ->{
			System.out.println(item.toString());
		});
	}


}
