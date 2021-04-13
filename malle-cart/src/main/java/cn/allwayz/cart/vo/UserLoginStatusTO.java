package cn.allwayz.cart.vo;

import lombok.Data;

/**
 * @author allwayz
 */
@Data
public class UserLoginStatusTO {

    /**
     * The login user has both a key and an ID
     */
    private Long id;

    /**
     * If you are not logged in, you will get a key with a null ID
     */
    private String userKey;

    /**
     * Is it the first time the cart is accessed? The first time the cart is accessed, it will be assigned a user-key
     */
    private boolean firstVisit;
}

