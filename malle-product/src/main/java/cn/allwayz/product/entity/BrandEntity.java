package cn.allwayz.product.entity;

import cn.allwayz.common.valid.AddGroup;
import cn.allwayz.common.valid.ListValue;
import cn.allwayz.common.valid.UpdateGroup;
import cn.allwayz.common.valid.UpdateStatusGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;


/**
 * 品牌
 * 
 * @author allwayz
 * @email allwayz_org@icloud.com
 * @date 2020-10-22 19:24:13
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

//	/**
//	 * Brand id
//	 */
//	@NotNull(message = "Update must within brandId",groups = {UpdateGroup.class})
//	@Null(message = "insert can not use Id attribute",groups = {AddGroup.class})
	@TableId
	private Long brandId;
//	/**
//	 * Brand anme
//	 */
//	@NotBlank(message = "Brand Name Can Not Blank",groups = {UpdateGroup.class,AddGroup.class})
	private String name;
//	/**
//	 * Logo Url
//	 */
//	@NotBlank(groups = {AddGroup.class})
//	@URL(message = "Unauthenticated Url address",groups = {UpdateGroup.class,AddGroup.class})
	private String logo;
	/**
	 * Introduction
	 */
	private String descript;
//	/**
//	 * showStatus
//	 */
//	@NotNull(groups = {AddGroup.class, UpdateStatusGroup.class})
//	@ListValue(vals={0,1},groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
//	/**
//	 * firstLetter
//	 */
//	@NotBlank
//	@Pattern(regexp="^[a-zA-Z]$",message = "Must be a Letter",groups={AddGroup.class,UpdateGroup.class})
	private String firstLetter;
//	/**
//	 * sort
//	 */
//	@NotNull(groups={AddGroup.class})
//	@Min(value = 0,message = "The sort must be greater than 0",groups={AddGroup.class,UpdateGroup.class})
	private Integer sort;

}
