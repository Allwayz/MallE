package cn.allwayz.auth.service;

import cn.allwayz.common.vo.MemberInfoVO;

public interface Oauth2WeiboService {
    MemberInfoVO access(String code);
}
