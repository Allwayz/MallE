package cn.allwayz.search.service;

import cn.allwayz.common.to.es.SkuEsModel;

import java.util.List;

/**
 * @author allwayz 
 */
public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> esSkuModels);
}
