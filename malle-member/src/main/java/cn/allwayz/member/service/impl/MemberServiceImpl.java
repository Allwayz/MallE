package cn.allwayz.member.service.impl;

import cn.allwayz.common.constant.MemberConstant;
import cn.allwayz.common.exception.BizCodeEnum;
import cn.allwayz.common.exception.BizException;
import cn.allwayz.common.to.MemberInfoTO;
import cn.allwayz.common.to.MemberLoginTO;
import cn.allwayz.common.to.MemberRegisterTO;
import cn.allwayz.common.utils.HttpUtils;
import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.Query;
import cn.allwayz.member.dao.MemberDao;
import cn.allwayz.member.entity.MemberEntity;
import cn.allwayz.member.entity.MemberLevelEntity;
import cn.allwayz.member.service.MemberLevelService;
import cn.allwayz.member.service.MemberService;
import cn.allwayz.member.vo.WeiboUserInfoVO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Resource
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public boolean register(MemberRegisterTO registerTO) {

        MemberEntity memberEntity = new MemberEntity();

        MemberLevelEntity levelEntity = memberLevelService.getDefaultLevel();
        // The default level
        memberEntity.setLevelId(levelEntity.getId());
        // Registered source
        memberEntity.setSourceType(MemberConstant.REGISTER_TYPE_MALLE);
        // Creation time
        memberEntity.setCreateTime(new Date());
        // Encrypted password
        memberEntity.setPassword(passwordEncoder.encode(registerTO.getPassword()));
        // The user name
        memberEntity.setUsername(registerTO.getUsername());
        memberEntity.setNickname(registerTO.getUsername());
        // Mobile phone
        memberEntity.setMobile(registerTO.getPhone());

        MemberEntity entityByMobile = this.getOne(new QueryWrapper<MemberEntity>().eq("mobile", memberEntity.getMobile()));
        if(!(entityByMobile == null)){
            throw new BizException(BizCodeEnum.MEMBER_ALREADY_EXIST, "UserName or Phone Number Already Exist");
        }
        MemberEntity entityByUserName = this.getOne(new QueryWrapper<MemberEntity>().eq("username", memberEntity.getUsername()));
        if(!(entityByUserName == null)){
            throw new BizException(BizCodeEnum.MEMBER_ALREADY_EXIST, "UserName or Phone Number Already Exist");
        }
        try {
            this.save(memberEntity);
            // Unique index reported an error
        } catch (DuplicateKeyException e) {
            throw new BizException(BizCodeEnum.MEMBER_ALREADY_EXIST, "UserName or Phone Number Already Exist");
        }

        return true;
    }

    @Override
    public MemberInfoTO login(MemberLoginTO loginTO) {
        // Check if the account exists
        MemberEntity entity = this.baseMapper.getByAccount(loginTO.getAccount());
        if (entity == null) {
            throw new BizException(BizCodeEnum.MEMBER_NOT_EXIST);
        }
        // The existence compares the password, compares the password to fail
        if (!passwordEncoder.matches(loginTO.getPassword(), entity.getPassword())) {
            throw new BizException(BizCodeEnum.MEMBER_ACCOUNT_PASSWORD_NOT_MATCH);
        }
        // Authentication success
        return convertMemberEntity2MemberInfoTO(entity);
    }

    /**
     * Convert the user information retrieved from the database to the user information needed by the front end
     * @param entity
     * @return
     */
    private MemberInfoTO convertMemberEntity2MemberInfoTO(MemberEntity entity) {
        MemberInfoTO memberInfoTO = new MemberInfoTO();
        // Copy of basic attributes
        BeanUtils.copyProperties(entity, memberInfoTO);
        // Set the member rank name
        MemberLevelEntity levelEntity = memberLevelService.getById(entity.getLevelId());
        memberInfoTO.setLevel(levelEntity.getName());
        return memberInfoTO;
    }

    /**
     * 通过微博accessToken拿到该用户在微博平台的基本信息，用于注册
     */
    private WeiboUserInfoVO getUserFromWeibo(String accessToken, String uid) {
        MemberEntity entity = new MemberEntity();
        HashMap<String, String> param = new HashMap<>();
        param.put("access_token", accessToken);
        param.put("uid", uid);
        try {
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), param);
            if (response.getStatusLine().getStatusCode() == 200) {
                String json = EntityUtils.toString(response.getEntity());
                WeiboUserInfoVO weiboUserInfoVO = JSON.parseObject(json, WeiboUserInfoVO.class);
                return weiboUserInfoVO;
            } else {
                log.warn("Failed to obtain user third party information");
                return null;
                //EntityUtils.toString(response.getEntity())
            }
        } catch (Exception e) {
            log.warn("Failed to obtain user third party information");
            return null;
        }
    }

    @Override
    public boolean comparePasswd(String username, String password) {
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username",username));

        System.out.println(memberEntity);
        return passwordEncoder.matches(password, memberEntity.getPassword());
    }
}