package com.nbb.flowable;

import cn.hutool.core.util.IdUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NbbFlowableApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) {
        String s = String.valueOf(IdUtil.getSnowflakeNextId());
        System.out.println(s.length());
    }

}
