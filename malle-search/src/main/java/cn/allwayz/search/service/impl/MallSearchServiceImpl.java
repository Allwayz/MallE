package cn.allwayz.search.service.impl;

import cn.allwayz.common.to.es.SkuEsModel;
import cn.allwayz.search.config.MalleElasticSearchConfig;
import cn.allwayz.search.config.ProductSearchConfig;
import cn.allwayz.search.feign.ProductFeignService;
import cn.allwayz.search.service.MallSearchService;
import cn.allwayz.search.vo.SearchParam;
import cn.allwayz.search.vo.SearchResult;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author allwayz
 */
@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {

    @Resource
    private RestHighLevelClient highLevelClient;
    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam searchParam) {
        SearchResult result = null;
        SearchRequest request = buildSearchRequest(searchParam);
        try {
            SearchResponse response = highLevelClient.search(request, MalleElasticSearchConfig.COMMON_OPTIONS);
            result = buildSearchResult(response, searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Build Search Data
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchRequest searchRequest = new SearchRequest();

        SearchSourceBuilder builder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 1.keyword
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        // 2.Filter(CategoryId, BrandId, Price Rang，HasStock?，Attrs)，
        // 2.1 CategoryId
        if (param.getCatelog3Id() != null &&  param.getCatelog3Id() > 0) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCatelog3Id()));
        }
        // 2.2 BrandId
        List<Long> brandId = param.getBrandId();
        if (!CollectionUtils.isEmpty(brandId)) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brandId));
        }
        // 2.3 Price Rang 1_500 / _500 / 500_
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
        String price = param.getSkuPrice();
        if (!StringUtils.isEmpty(price)) {
            String[] priceInfo = price.split("_");
            // 1_500
            if (priceInfo.length == 2) {
                rangeQueryBuilder.gte(priceInfo[0]).lte(priceInfo[1]);
                //    _500
            } else if (price.startsWith("_")) {
                rangeQueryBuilder.lte(priceInfo[0]);
                //    500_
            } else {
                rangeQueryBuilder.gte(priceInfo[0]);
            }
        }
        boolQueryBuilder.filter(rangeQueryBuilder);
        // 2.4 Stock
        if (param.getHasStock() != null) {
            boolean flag = param.getHasStock() == 0 ? false : true;
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", flag));
        }
        // 2.5 Attr
        // attrs=1_CPU:A14&attrs=2_OS:IOS ==> attrs=[1_CPU:A14,2_OS:IOS]
        List<String> attrs = param.getAttrs();
        if (!CollectionUtils.isEmpty(attrs)) {
            for (String attr : attrs) {
                String[] attrInfo = attr.split("_");
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrInfo[0]));
                boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrInfo[1].split(":")));
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }

        // The first part of the bool query completes the composition
        builder.query(boolQueryBuilder);

        // 3. Sort，sort=hotScore_asc/desc
        String sortStr = param.getSort();
        if (!StringUtils.isEmpty(sortStr)) {
            String[] sortInfo = sortStr.split("_");
            SortOrder sortType = sortInfo[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            builder.sort(sortInfo[0], sortType);
        }

        // 4. Pages
        builder.from(param.getPageNum() == null ? 0 : (param.getPageNum() - 1) * ProductSearchConfig.PAGE_SIZE);
        builder.size(ProductSearchConfig.PAGE_SIZE);

        // 5. HighLight
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle").preTags("<b style='color:red'>").postTags("</b>");
            builder.highlighter(highlightBuilder);
        }


        // 6. Aggregate analysis，In order to obtain the category, brand, and specification parameters involved in the product,
        // 6.1 Category Agg
        TermsAggregationBuilder catelogAgg = AggregationBuilders.terms("catelogAgg").field("catalogId");
        catelogAgg.subAggregation(AggregationBuilders.terms("catelogNameAgg").field("catalogName").size(1));
        builder.aggregation(catelogAgg);

        // 6.2 Brand Agg
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brandAgg").field("brandId");
        brandAgg.subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brandImgAgg").field("brandImg").size(1));
        builder.aggregation(brandAgg);

        // 6.3 Attr Agg
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("attrAgg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId");
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"));
        nestedAggregationBuilder.subAggregation(attrIdAgg);
        builder.aggregation(nestedAggregationBuilder);


        System.out.println("The Query that was created" + builder.toString() );
        searchRequest.source(builder);
        return searchRequest;
    }

    /**
     * Build Result Data
     *
     * @param response
     * @param param
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {
        SearchResult result = new SearchResult();
        SearchHits hits = response.getHits();
        //???
        List<SkuEsModel> esModels = Arrays.stream(hits.getHits()).map(hit -> {
            // 每个命中的记录的_source部分是真正的数据的json字符串
            String sourceAsString = hit.getSourceAsString();
            SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
            if (!StringUtils.isEmpty(param.getKeyword())) {
                String skuTitle = hit.getHighlightFields().get("skuTitle").getFragments()[0].toString();
                esModel.setSkuTitle(skuTitle);
            }
            return esModel;
        }).collect(Collectors.toList());
        result.setSkuList(esModels);

        //Category
        Aggregations aggregations = response.getAggregations();
        // debug模式下确定这个返回的具体类型
        ParsedLongTerms catelogAgg = aggregations.get("catelogAgg");
        // 每一个bucket是一种分类，有几个bucket就会有几个分类
        List<SearchResult.CatelogVO> catelogs = catelogAgg.getBuckets().stream().map(bucket -> {
            // debug查看下结果
            long catelogId = bucket.getKeyAsNumber().longValue();
            // debug模式下确定这个返回的具体类型
            ParsedStringTerms catelogNameAgg = bucket.getAggregations().get("catelogNameAgg");
            // 根据id分类后肯定是同一类，只可能有一种名字，所以直接取第一个bucket
            String catelogName = catelogNameAgg.getBuckets().get(0).getKeyAsString();
            SearchResult.CatelogVO catelogVO = new SearchResult.CatelogVO();
            catelogVO.setCatelogId(catelogId);
            catelogVO.setCatelogName(catelogName);
            return catelogVO;
        }).collect(Collectors.toList());
        result.setCatelogs(catelogs);

        //Brand
        ParsedLongTerms brandAgg = aggregations.get("brandAgg");
        List<SearchResult.BrandVO> brands = brandAgg.getBuckets().stream().map(bucket -> {
            long brandId = bucket.getKeyAsNumber().longValue();
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brandNameAgg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brandImgAgg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            SearchResult.BrandVO brandVO = new SearchResult.BrandVO();
            brandVO.setBrandId(brandId);
            brandVO.setBrandName(brandName);
            brandVO.setBrandImg(brandImg);
            return brandVO;
        }).collect(Collectors.toList());
        result.setBrands(brands);

        //Attribute
        ParsedNested attrAgg = aggregations.get("attrAgg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        List<SearchResult.AttrVO> attrs = attrIdAgg.getBuckets().stream().map(bucket -> {
            long attrId = bucket.getKeyAsNumber().longValue();
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            // 根据id分类后肯定是同一类，只可能有一种名字，所以直接取第一个bucket
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            // 根据id分类后肯定是同一类，但是可以有多个值，所以会有多个bucket，把所有值组合起来
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            List<String> attrValue = attrValueAgg.getBuckets().stream().map(b -> b.getKeyAsString()).collect(Collectors.toList());
            SearchResult.AttrVO attrVO = new SearchResult.AttrVO();
            attrVO.setAttrId(attrId);
            attrVO.setAttrName(attrName);
            attrVO.setAttrValue(attrValue);
            return attrVO;
        }).collect(Collectors.toList());
        result.setAttrs(attrs);

        //Paging
        // 总记录数
        result.setTotalCount(hits.getTotalHits().value);
        // 每页大小
        result.setPageSize(ProductSearchConfig.PAGE_SIZE);
        // 总页数
        result.setTotalPage((result.getTotalCount() + ProductSearchConfig.PAGE_SIZE - 1) / ProductSearchConfig.PAGE_SIZE);
        // 当前页码
        int pageNum = param.getPageNum() == null ? 1 : param.getPageNum();
        result.setCurrPage(pageNum);
        // 构建页码导航,以当前页为中心，连续5页
        ArrayList<Integer> pageNavs = new ArrayList<>();
        for (int i = pageNum - 2; i <= pageNum + 2; ++i) {
            if (i <= 0) {
                continue;
            }
            if (i >= result.getTotalPage()) {
                break;
            }
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        List<SearchResult.BreadCrumbsVO> breadCrumbsVOS = new LinkedList<>();
        return result;
    }

}
