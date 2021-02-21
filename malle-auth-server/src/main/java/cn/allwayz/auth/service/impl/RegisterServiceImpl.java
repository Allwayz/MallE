package cn.allwayz.auth.service.impl;

import cn.allwayz.auth.exception.RegisterPageException;
import cn.allwayz.auth.feign.MemberFeignService;
import cn.allwayz.auth.service.RegisterService;
import cn.allwayz.auth.vo.RegisterVO;
import cn.allwayz.common.to.MemberRegisterTO;
import cn.allwayz.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    MemberFeignService memberFeignService;

    /**
     * 发送验证码，同一用户，一分钟内只发送一次
     * @param phone
     */
    @Override
    public void sendCode(String phone) {

    }

    @Override
    public boolean register(RegisterVO registerVO) {
        System.out.println(">>>>>>>>>>开始执行校验");
        // 调用远程服务完成保存
        MemberRegisterTO to = new MemberRegisterTO();
        BeanUtils.copyProperties(registerVO, to);
        R r = memberFeignService.register(to);
        if (r.getCode() != 0) {
            // 抛出异常,远程服务执行失败，会返回json格式的r，里面包含了错误信息
            throw new RegisterPageException(r.getCode(), r.getData(String.class));
        }
        return true;
    }

    /**
     * 生成6位随机验证码
     * @return	生成的验证码
     */
    private String randomCode() {

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < 6; i++) {
            // 1.生成随机数
            double doubleRandom = Math.random();
            // 2.调整
            int integerRandom = (int) (doubleRandom * 10);
            // 3.拼接
            builder.append(integerRandom);
        }
        return builder.toString();
    }
}

