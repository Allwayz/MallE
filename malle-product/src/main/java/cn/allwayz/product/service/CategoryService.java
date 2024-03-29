package cn.allwayz.product.service;

import cn.allwayz.product.vo.Catelog2VO;
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

    /**
     * Delete Categories
     * @param asList
     */
    void removeMenuByIds(List<Long> asList);

    /**
     *
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    /**
     * 级联更新所有关联的数据
     * @param category
     */
    void updateCascade(CategoryEntity category);

    /**
     *
     * @return
     */
    List<CategoryEntity> getLevel1Categories();

    /**
     *
     * @return
     */
    Map<String,List<Catelog2VO>> getCatalogJson();
}

