package cn.allwayz.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class ItemAttrGroupWithAttrVO {

    private Long attrGroupId;

    private String attrGroupName;

    private List<Attr> attrs;
}