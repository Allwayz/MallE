package cn.allwayz.auth.service.impl;

import cn.allwayz.auth.exception.RegisterPageException;
import cn.allwayz.auth.feign.MemberFeignService;
import cn.allwayz.auth.service.RegisterService;
import cn.allwayz.auth.vo.RegisterVO;
import cn.allwayz.common.to.MemberRegisterTO;
import cn.allwayz.common.utils.R;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public boolean register(RegisterVO registerVO) {
        MemberRegisterTO to = new MemberRegisterTO();
        BeanUtils.copyProperties(registerVO, to);
        R r = memberFeignService.register(to);
        if (r.getCode() != 0) {
            throw new RegisterPageException(r.getCode(), r.getData("msg",new TypeReference<String>(){}));
        }
        return true;
    }
}

