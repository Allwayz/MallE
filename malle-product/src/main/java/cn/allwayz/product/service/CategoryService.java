package cn.allwayz.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:13
 */
public interface CategoryService extends IService<CategoryEntity> {

    /**
     * Paging query
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * Select All item category and shown in a tree structure
     * @return
     */
    List<CategoryEntity> listWithTree();

}

