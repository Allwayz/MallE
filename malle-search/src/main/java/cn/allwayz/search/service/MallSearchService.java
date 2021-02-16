package cn.allwayz.search.service;

import cn.allwayz.search.vo.SearchParam;
import cn.allwayz.search.vo.SearchResult;

/**
 * @author allwayz
 */
public interface MallSearchService {

    /**
     * 检索服务
     * @param searchParam 所有检索参数
     * @return 检索结果
     */
    SearchResult search(SearchParam searchParam);

}
