/*
 * Copyright 2019 Pnoker. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pnoker.center.dbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pnoker.center.dbs.mapper.RtmpMapper;
import com.pnoker.center.dbs.service.RtmpService;
import com.pnoker.common.bean.Pages;
import com.pnoker.common.constant.Common;
import com.pnoker.common.dto.transfer.RtmpDto;
import com.pnoker.common.entity.rtmp.Rtmp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>Rtmp 接口实现
 *
 * @author : pnoker
 * @email : pnokers@icloud.com
 */
@Slf4j
@Service
@Transactional
public class RtmpServiceImpl implements RtmpService {
    @Resource
    private RtmpMapper rtmpMapper;

    @Override
    @Caching(
            put = {@CachePut(value = "dbs_rtmp", key = "#rtmp.id", unless = "#result==null")},
            evict = {@CacheEvict(value = "dbs_rtmp_list", allEntries = true)}
    )
    public Rtmp add(Rtmp rtmp) {
        return rtmpMapper.insert(rtmp) > 0 ? rtmp : null;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "dbs_rtmp", key = "#id"),
                    @CacheEvict(value = "dbs_rtmp_list", allEntries = true)
            }
    )
    public boolean delete(Long id) {
        return rtmpMapper.deleteById(id) > 0;
    }

    @Override
    @Caching(
            put = {@CachePut(value = "dbs_rtmp", key = "#rtmp.id", unless = "#result==null")},
            evict = {@CacheEvict(value = "dbs_rtmp_list", allEntries = true)}
    )
    public Rtmp update(Rtmp rtmp) {
        return rtmpMapper.updateById(rtmp) > 0 ? rtmp : null;
    }

    @Override
    @Cacheable(value = "dbs_rtmp", key = "#id", unless = "#result==null")
    public Rtmp selectById(Long id) {
        return rtmpMapper.selectById(id);
    }

    @Override
    @Cacheable(value = "dbs_rtmp_list", keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<Rtmp> list(RtmpDto rtmpDto) {
        return rtmpMapper.selectPage(pagination(rtmpDto.getPage()), fuzzyQuery(rtmpDto));
    }

    @Override
    public QueryWrapper<Rtmp> fuzzyQuery(RtmpDto rtmpDto) {
        QueryWrapper<Rtmp> queryWrapper = new QueryWrapper<>();
        Optional.ofNullable(rtmpDto).ifPresent(dto -> {
            if (null != dto.getAutoStart()) {
                queryWrapper.eq(Common.Cloumn.Rtmp.AUTO_START, BooleanUtils.isTrue(dto.getAutoStart()));
            }
            if (StringUtils.isNotBlank(dto.getName())) {
                queryWrapper.like(Common.Cloumn.NAME, dto.getName());
            }
        });
        return queryWrapper;
    }

    @Override
    public Page<Rtmp> pagination(Pages pages) {
        Page<Rtmp> page = new Page<>(pages.getPageNum(), pages.getPageSize());
        Optional.ofNullable(pages.getOrders()).ifPresent(orderItems -> {
            List<OrderItem> tmps = new ArrayList<>();
            orderItems.forEach(item -> {
                if (Common.Cloumn.ID.equals(item.getColumn())) {
                    tmps.add(item);
                }
                if (Common.Cloumn.NAME.equals(item.getColumn())) {
                    tmps.add(item);
                }
            });
            page.setOrders(tmps);
        });
        return page;
    }

}
