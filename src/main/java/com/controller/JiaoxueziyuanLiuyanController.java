package com.controller;


import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.StringUtil;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;

import com.entity.JiaoxueziyuanLiuyanEntity;

import com.service.JiaoxueziyuanLiuyanService;
import com.entity.view.JiaoxueziyuanLiuyanView;
import com.service.JiaoxueziyuanService;
import com.entity.JiaoxueziyuanEntity;
import com.service.YonghuService;
import com.entity.YonghuEntity;
import com.service.JiaoshiService;
import com.utils.PageUtils;
import com.utils.R;

/**
 * 教学资源评价
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/jiaoxueziyuanLiuyan")
public class JiaoxueziyuanLiuyanController {
    private static final Logger logger = LoggerFactory.getLogger(JiaoxueziyuanLiuyanController.class);

    @Autowired
    private JiaoxueziyuanLiuyanService jiaoxueziyuanLiuyanService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;



    //级联表service
    @Autowired
    private JiaoxueziyuanService jiaoxueziyuanService;
    @Autowired
    private YonghuService yonghuService;
    @Autowired
    private JiaoshiService jiaoshiService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isEmpty(role)){
            return R.error(511,"权限为空");
        }
        else if("学生".equals(role)){
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        }
        else if("教师".equals(role)){
            params.put("jiaoshiId",request.getSession().getAttribute("userId"));
        }
        params.put("orderBy","id");
        PageUtils page = jiaoxueziyuanLiuyanService.queryPage(params);

        //字典表数据转换
        List<JiaoxueziyuanLiuyanView> list =(List<JiaoxueziyuanLiuyanView>)page.getList();
        for(JiaoxueziyuanLiuyanView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        JiaoxueziyuanLiuyanEntity jiaoxueziyuanLiuyan = jiaoxueziyuanLiuyanService.selectById(id);
        if(jiaoxueziyuanLiuyan !=null){
            //entity转view
            JiaoxueziyuanLiuyanView view = new JiaoxueziyuanLiuyanView();
            BeanUtils.copyProperties( jiaoxueziyuanLiuyan , view );//把实体数据重构到view中

            //级联表
            JiaoxueziyuanEntity jiaoxueziyuan = jiaoxueziyuanService.selectById(jiaoxueziyuanLiuyan.getJiaoxueziyuanId());
            if(jiaoxueziyuan != null){
                BeanUtils.copyProperties( jiaoxueziyuan , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                view.setJiaoxueziyuanId(jiaoxueziyuan.getId());
            }
            //级联表
            YonghuEntity yonghu = yonghuService.selectById(jiaoxueziyuanLiuyan.getYonghuId());
            if(yonghu != null){
                BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                view.setYonghuId(yonghu.getId());
            }
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody JiaoxueziyuanLiuyanEntity jiaoxueziyuanLiuyan, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,jiaoxueziyuanLiuyan:{}",this.getClass().getName(),jiaoxueziyuanLiuyan.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isEmpty(role)){
            return R.error(511,"权限为空");
        }
        else if("学生".equals(role)){
            jiaoxueziyuanLiuyan.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));
        }
        jiaoxueziyuanLiuyan.setInsertTime(new Date());
        jiaoxueziyuanLiuyan.setCreateTime(new Date());
        jiaoxueziyuanLiuyanService.insert(jiaoxueziyuanLiuyan);
        return R.ok();
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody JiaoxueziyuanLiuyanEntity jiaoxueziyuanLiuyan, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,jiaoxueziyuanLiuyan:{}",this.getClass().getName(),jiaoxueziyuanLiuyan.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isEmpty(role)){
            return R.error(511,"权限为空");
        }
        else if("学生".equals(role)){
            jiaoxueziyuanLiuyan.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));
        }
        jiaoxueziyuanLiuyanService.updateById(jiaoxueziyuanLiuyan);//根据id更新
        return R.ok();
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        jiaoxueziyuanLiuyanService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }



    /**
    * 前端列表
    */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isEmpty(role)){
            return R.error(511,"权限为空");
        }
        else if("学生".equals(role)){
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        }
        else if("教师".equals(role)){
            params.put("jiaoshiId",request.getSession().getAttribute("userId"));
        }
        // 没有指定排序字段就默认id倒序
        if(StringUtil.isEmpty(String.valueOf(params.get("orderBy")))){
            params.put("orderBy","id");
        }
        PageUtils page = jiaoxueziyuanLiuyanService.queryPage(params);

        //字典表数据转换
        List<JiaoxueziyuanLiuyanView> list =(List<JiaoxueziyuanLiuyanView>)page.getList();
        for(JiaoxueziyuanLiuyanView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c);
        }
        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        JiaoxueziyuanLiuyanEntity jiaoxueziyuanLiuyan = jiaoxueziyuanLiuyanService.selectById(id);
            if(jiaoxueziyuanLiuyan !=null){
                //entity转view
                JiaoxueziyuanLiuyanView view = new JiaoxueziyuanLiuyanView();
                BeanUtils.copyProperties( jiaoxueziyuanLiuyan , view );//把实体数据重构到view中

                //级联表
                    JiaoxueziyuanEntity jiaoxueziyuan = jiaoxueziyuanService.selectById(jiaoxueziyuanLiuyan.getJiaoxueziyuanId());
                if(jiaoxueziyuan != null){
                    BeanUtils.copyProperties( jiaoxueziyuan , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setJiaoxueziyuanId(jiaoxueziyuan.getId());
                }
                //级联表
                    YonghuEntity yonghu = yonghuService.selectById(jiaoxueziyuanLiuyan.getYonghuId());
                if(yonghu != null){
                    BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setYonghuId(yonghu.getId());
                }
                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody JiaoxueziyuanLiuyanEntity jiaoxueziyuanLiuyan, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,jiaoxueziyuanLiuyan:{}",this.getClass().getName(),jiaoxueziyuanLiuyan.toString());
        jiaoxueziyuanLiuyan.setInsertTime(new Date());
        jiaoxueziyuanLiuyan.setCreateTime(new Date());
        jiaoxueziyuanLiuyanService.insert(jiaoxueziyuanLiuyan);
        return R.ok();
    }





}

