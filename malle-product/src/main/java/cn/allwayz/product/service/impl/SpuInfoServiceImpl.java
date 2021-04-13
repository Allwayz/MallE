package cn.allwayz.product.service.impl;

import cn.allwayz.common.constant.ProductConstant;
import cn.allwayz.common.exception.BizCodeEnum;
import cn.allwayz.common.exception.BizException;
import cn.allwayz.common.to.*;
import cn.allwayz.common.to.es.SkuEsModel;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.Query;
import cn.allwayz.common.utils.R;
import cn.allwayz.product.dao.SpuInfoDao;
import cn.allwayz.product.entity.*;
import cn.allwayz.product.feign.CouponFeignService;
import cn.allwayz.product.feign.SearchFeignService;
import cn.allwayz.product.feign.WareFeignService;
import cn.allwayz.product.service.*;
import cn.allwayz.product.vo.*;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Resource
    SpuInfoDescService spuInfoDescService;
    @Resource
    SpuImagesService imagesService;
    @Resource
    AttrService attrService;
    @Resource
    ProductAttrValueService attrValueService;
    @Resource
    SkuInfoService skuInfoService;
    @Resource
    SkuImagesService skuImagesService;
    @Resource
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Resource
    BrandService brandService;
    @Resource
    CategoryService categoryService;

    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    SearchFeignService searchFeignService;
    @Autowired
    WareFeignService wareFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * @param vo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        //1、Save SPU basic information pms_spu_info
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);

        //2、Save the description image of the SPU pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        //3、Save the SPU's photo gallery pms_spu_images
        List<String> images = vo.getImages();
        imagesService.saveImages(infoEntity.getId(),images);

        //4、Save SPU specification parameters;pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity id = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(id.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());

            return valueEntity;
        }).collect(Collectors.toList());
        attrValueService.saveProductAttr(collect);

        //5、Save SPU credit information；malle_sms->sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode() != 0){
            log.error("Failed to save SPU credit information remotely");
        }

        //5、Save all SKU information corresponding to the current SPU;
        List<Skus> skus = vo.getSkus();
        if(skus!=null && skus.size()>0){
            skus.forEach(item->{
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if(image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //5.1）、Basic information about the SKU；pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();

                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity->{
                    //Returns true for required and false for cull
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //5.2）、SKU picture information；pms_sku_image
                skuImagesService.saveBatch(imagesEntities);
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);

                    return attrValueEntity;
                }).collect(Collectors.toList());
                //5.3）、Sales attribute information for the SKU：pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // //5.4）、SKU discount, full reduction and other information；malle_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount() >0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r1.getCode() != 0){
                        log.error("Failed to save SKU offers remotely");
                    }
                }
            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }

        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * products release
     *
     * 1、Change the property to Shelved，
     * 2、Build the SkuesModel model, then call the search service remotely and index records to the search service
     *
     * @param spuId
     *              */
    @Override
    public void up(Long spuId) {
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);

        List<Long> skuIdList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        List<ProductAttrValueEntity> baseAttrs = attrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());

        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);

        Set<Long> idSet = new HashSet<>(searchAttrIds);

        List<SkuEsModel.Attr> attrsList = baseAttrs.stream().filter(o -> idSet.contains(o.getAttrId()))
                .map(o -> {
                    SkuEsModel.Attr attrs1 = new SkuEsModel.Attr();
                    BeanUtils.copyProperties(o, attrs1);
                    return attrs1;
                }).collect(Collectors.toList());

        Map<Long, Boolean> stockMap = null;
        try {
            R r = wareFeignService.getSkusHasStock(skuIdList);
            TypeReference<List<SkuHasStockVO>> typeReference = new TypeReference<List<SkuHasStockVO>>() {};
            stockMap = r.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVO::getSkuId, SkuHasStockVO::getHasStock));
        } catch (Exception e) {
            log.error("Inventory service query abnormal, cause：", e);
        }

        // 2 Encapsulate information for each SKU
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> upProducts = skuInfoEntities.stream()
                .map(skuInfoEntity -> {
                    // Data required for assembly
                    SkuEsModel esSkuModel = new SkuEsModel();
                    BeanUtils.copyProperties(skuInfoEntity, esSkuModel);
                    // Manually set values for fields with inconsistent names
                    // skuPrice -> price skuImg -> skuDefaultImg
                    esSkuModel.setSkuPrice(skuInfoEntity.getPrice());
                    esSkuModel.setSkuImg(skuInfoEntity.getSkuDefaultImg());
                    if (finalStockMap == null) {
                        esSkuModel.setHasStock(true);
                    } else {
                        esSkuModel.setHasStock(finalStockMap.get(skuInfoEntity.getSkuId()));
                    }

                    esSkuModel.setHotScore(0L);

                    BrandEntity brandEntity = brandService.getById(esSkuModel.getBrandId());
                    esSkuModel.setBrandName(brandEntity.getName());
                    esSkuModel.setBrandImg(brandEntity.getLogo());
                    CategoryEntity categoryEntity = categoryService.getById(esSkuModel.getCatalogId());
                    esSkuModel.setCatalogName(categoryEntity.getName());

                    // Set retrieval properties
                    esSkuModel.setAttrs(attrsList);

                    return esSkuModel;
                }).collect(Collectors.toList());

        R r = searchFeignService.productStatusUp(upProducts);
        if (r.getCode() == 0) {
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
            // Failed to put on the shelf
        /**
        * Feign call flow
        * 1. Construct the request data to convert the object to JSON
        *      RequestTemplate
        * 2. Send a request for execution (successful execution will decode the corresponding data)
        *     executeAndDecode(template)
        * 3. There is a retry mechanism for executing requests
        */
        }
    }

    @Override
    public SpuInfoTO getBySkuId(Long skuId) {

        SpuInfoTO spuInfoTO = new SpuInfoTO();

        SkuInfoEntity sku = skuInfoService.getById(skuId);
        Long spuId = sku.getSpuId();

        SpuInfoEntity spu = this.getById(spuId);
        // Copy of public attributes
        BeanUtils.copyProperties(spu, spuInfoTO);

        CategoryEntity categoryEntity = categoryService.getById(spu.getCatalogId());
        BrandEntity brandEntity = brandService.getById(spu.getBrandId());
        // Unique attribute assignment
        spuInfoTO.setCatelogName(categoryEntity.getName());
        spuInfoTO.setBrandName(brandEntity.getName());

        R r = couponFeignService.getBySpuId(spuId);
        if (r.getCode() != 0) {
            log.error("The malle-product call to malle-coupon failed");
            throw new BizException(BizCodeEnum.CALL_FEIGN_SERVICE_FAILED, "Query credits failed");
        }
        SpuBoundsTO boundsTO = r.getData(SpuBoundsTO.class);

        spuInfoTO.setGrowBounds(boundsTO.getGrowBounds());
        spuInfoTO.setIntegration(boundsTO.getBuyBounds());

        return spuInfoTO;
    }

}