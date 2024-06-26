package com.service.impl;

import com.utils.StringUtil;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import java.util.*;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;

import com.dao.JiaoxueziyuanLiuyanDao;
import com.entity.JiaoxueziyuanLiuyanEntity;
import com.service.JiaoxueziyuanLiuyanService;
import com.entity.view.JiaoxueziyuanLiuyanView;

/**
 * 教学资源评价 服务实现类
 */
@Service("jiaoxueziyuanLiuyanService")
@Transactional
public class JiaoxueziyuanLiuyanServiceImpl extends ServiceImpl<JiaoxueziyuanLiuyanDao, JiaoxueziyuanLiuyanEntity> implements JiaoxueziyuanLiuyanService {

    @Override
    public PageUtils queryPage(Map<String,Object> params) {
        if(params != null && (params.get("limit") == null || params.get("page") == null)){
            params.put("page","1");
            params.put("limit","10");
        }
        Page<JiaoxueziyuanLiuyanView> page =new Query<JiaoxueziyuanLiuyanView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page,params));
        return new PageUtils(page);
    }


}
