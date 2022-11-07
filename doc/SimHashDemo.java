package com.xzixi.algorithm.simhash.demo;

import com.xzixi.algorithm.simhash.analyzer.extractor.JcsegKeywordsExtractor;
import com.xzixi.algorithm.simhash.analyzer.hash.FVNHashGenerator;
import com.xzixi.algorithm.simhash.common.HashGenerator;
import com.xzixi.algorithm.simhash.common.KeywordsExtractor;
import com.xzixi.algorithm.simhash.core.SimHashUtil;
import com.xzixi.algorithm.simhash.core.SimHasher;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

@Slf4j
public class Main {

    public static void main(String[] args) {
        KeywordsExtractor keywordsExtractor = new JcsegKeywordsExtractor();
        HashGenerator hashGenerator = new FVNHashGenerator();
        SimHasher simHasher = new SimHasher(keywordsExtractor, hashGenerator);
        BigInteger sign1 = simHasher.simhash("您的服务器111.111.111.111（ali-server01）存在异常登录行为：详情可登录云盾-安骑士控制台进行查看和处理，如果确认是您自己在登录。");
        BigInteger sign2 = simHasher.simhash("您的服务器111.111.111.111（ali-server03）存在异常登录行为：详情可登录云盾-安骑士控制台进行查看和处理，如果确认是您自己在登录，可忽略该短信。");
        log.info(String.format("distance: %d", SimHashUtil.getHammingDistance(sign1, sign2)));
    }

}
