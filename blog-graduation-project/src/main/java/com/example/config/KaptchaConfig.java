package com.example.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 谷歌 Kaptcha图片验证码
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha producer () {
        Properties propertis = new Properties();
        propertis.put("kaptcha.border", "no"); //不要边框
        propertis.put("kaptcha.image.height", "38"); //验证码图片高度
        propertis.put("kaptcha.image.width", "150"); //验证码图片宽度
        propertis.put("kaptcha.textproducer.font.color", "black"); //验证码图片颜色
        propertis.put("kaptcha.textproducer.font.size", "32"); //验证码图片字体大小
        Config config = new Config(propertis);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }

}
