package cn.allwayz.ware.service.impl;

import cn.allwayz.common.utils.PageUtils;
import cn.allwayz.common.utils.Query;
import cn.allwayz.ware.dao.WareOrderTaskDetailDao;
import cn.allwayz.ware.entity.WareOrderTaskDetailEntity;
import cn.allwayz.ware.service.WareOrderTaskDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity> implements WareOrderTaskDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskDetailEntity> page = this.page(
                new Query<WareOrderTaskDetailEntity>().getPage(params),
                new QueryWrapper<WareOrderTaskDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<WareOrderTaskDetailEntity> listByTtaskId(Long taskId) {

        return this.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", taskId));
    }

}