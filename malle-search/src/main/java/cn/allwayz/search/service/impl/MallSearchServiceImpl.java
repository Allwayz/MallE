package cn.allwayz.search.service.impl;

import cn.allwayz.common.constant.ESConstant;
import cn.allwayz.common.to.es.SkuEsModel;
import cn.allwayz.common.utils.R;
import cn.allwayz.search.config.ProductSearchConfig;
import com.alibaba.fastjson.JSON;
import cn.allwayz.search.config.MalleElasticSearchConfig;
import cn.allwayz.search.service.MallSearchService;
import cn.allwayz.search.vo.SearchParam;
import cn.allwayz.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
     * @param searchParam
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam searchParam) {
        //TODO
        return null;
    }

}
