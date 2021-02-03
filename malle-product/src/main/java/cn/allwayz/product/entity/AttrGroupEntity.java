package cn.allwayz.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 属性分组
 * 
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:14
 */
@Data
@TableName("pms_attr_group")
public class AttrGroupEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * GroupId
	 */
	@TableId
	private Long attrGroupId;
	/**
	 * GroupName
	 */
	private String attrGroupName;
	/**
	 * sort
	 */
	private Integer sort;
	/**
	 * descript
	 */
	private String descript;
	/**
	 * icon
	 */
	private String icon;
	/**
	 * catelogId
	 */
	private Long catelogId;

	@TableField(exist = false)
	private Long[] catelogPath;
}
