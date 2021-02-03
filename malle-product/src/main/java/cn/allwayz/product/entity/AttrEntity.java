package cn.allwayz.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品属性
 * 
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:14
 */
@Data
@TableName("pms_attr")
public class AttrEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * attrId
	 */
	@TableId
	private Long attrId;
	/**
	 * attrName
	 */
	private String attrName;
	/**
	 * searchType[0-dontNees，1-Need]
	 */
	private Integer searchType;
	/**
	 * icon
	 */
	private String icon;
	/**
	 * valueSelect
	 */
	private String valueSelect;
	/**
	 * attrType[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
	 */
	private Integer attrType;
	/**
	 * enable[0 - 禁用，1 - 启用]
	 */
	private Long enable;
	/**
	 * catelogId
	 */
	private Long catelogId;
	/**
	 * showDesc【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整
	 */
	private Integer showDesc;

	/**
	 * Multiple or single
	 */
	private Integer valueType;

}
