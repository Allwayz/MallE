package cn.allwayz.common.to;

import lombok.Data;

@Data
public class WeiboUserAuthTO {

    /**
     * access_token : ACCESS_TOKEN
     * expires_in : 1234
     * remind_in : 798114
     * uid : 12341234
     */

    /**
     * A unique ticket authorized by the user to access the user's information on the Weibo platform
     */
    private String accessToken;
    /**
     * The lifetime of access_token, in seconds.
     */
    private int expiresIn;
    /**
     * The access_token lifecycle (this parameter is about to be deprecated; developers should use expires_in).
     */
    private String remindIn;
    /**
     * UID of the current authorized user in Weibo. This field is only returned for the convenience of the developer to reduce one user/show interface call.
     * Third-party applications cannot use this field to identify the user's login status. Only the access_token is the only ticket authorized by the user.
     */
    private String uid;

}
