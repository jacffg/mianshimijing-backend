package com.yupi.mianshiya.config;

/**
 * @author 万佳羊
 * {@code @date}  2024-12-12  17:39
 * @version 1.0
 */

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.yupi.mianshiya.model.dto.mail.Message;
import org.springframework.stereotype.Component;


import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;

@Component
public class MailConfig {

    @NacosValue(value = "${mail.host:smtp.163.com}", autoRefreshed = true)
    private String host;

    @NacosValue(value = "${mail.port:465}", autoRefreshed = true)
    private int port;

    @NacosValue(value = "${mail.from:13517950816@163.com}", autoRefreshed = true)
    private String from;

    @NacosValue(value = "${mail.user:13517950816@163.com}", autoRefreshed = true)
    private String user;

    @NacosValue(value = "${mail.pass:EJgNJen3jEQLd8ak}", autoRefreshed = true)
    private String pass;

    @NacosValue(value = "${mail.ssl-enable:true}", autoRefreshed = true)
    private boolean sslEnable;

    // Getters for the fields

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getFrom() {
        return from;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public boolean isSslEnable() {
        return sslEnable;
    }

    // Method to send email
    public boolean sendEmail(Message message) {
        MailAccount account = new MailAccount();
        account.setHost(host);
        account.setPort(port);
        account.setFrom(from);
        account.setUser(user);
        account.setPass(pass);
        account.setSslEnable(sslEnable);

        try {
            // 发送邮件
            MailUtil.send(account, message.getToEmail(), message.getSubject(), message.getContent(), false);
            System.out.println("邮件发送成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("邮件发送时出现错误: " + e.getMessage());
            return false;
        }
    }
}

