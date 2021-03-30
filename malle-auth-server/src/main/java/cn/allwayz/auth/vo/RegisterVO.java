package cn.allwayz.auth.vo;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class RegisterVO {
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,6}$|^[\\dA-Za-z_]{4,20}$", message = "User names are 2 to 6 Chinese or 4 to 20 character combinations")
    private String username;

    @Pattern(regexp = "^[a-zA-Z0-9_/.]{8,16}$", message = "The password is a combination of 8 - to 16-bit alphanumeric symbols")
    private String password;

    @Pattern(regexp = "^0[0-9]{10}$", message = "The format of mobile phone number is not legal")
    private String phone;

    @Pattern(regexp = "^[a-zA-Z0-9]{4}$", message = "Captcha error")
    private String code;
}
