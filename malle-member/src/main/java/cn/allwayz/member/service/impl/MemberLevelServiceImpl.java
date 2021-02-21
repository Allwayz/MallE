package cn.allwayz.member.service.impl;

import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.Query;
import cn.allwayz.member.dao.MemberLevelDao;
import cn.allwayz.member.entity.MemberLevelEntity;
import cn.allwayz.member.service.MemberLevelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLevelEntity> page = this.page(
                new Query<MemberLevelEntity>().getPage(params),
                new QueryWrapper<MemberLevelEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public MemberLevelEntity getDefaultLevel() {

        return this.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
    }

}