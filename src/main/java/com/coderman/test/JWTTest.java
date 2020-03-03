package com.coderman.test;


import com.coderman.modal.User;
import com.coderman.repository.UserRepository;
import com.coderman.util.JWTUtils;
import com.coderman.util.MD5Utils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

/**
 * @Author zhangyukang
 * @Date 2020/2/29 21:22
 * @Version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class JWTTest {

    @Autowired
    private UserRepository userRepository;


    public static void main(String[] args) {
        String token = JWTUtils.sign("admin", "d8bcf3f395c2cc7d6d9abfc4fa48b0c1");
        System.out.println(token);
    }

    @Test
    public void testMd5(){
       String salt= UUID.randomUUID().toString();
        String password="zhangyukang";
        String s = MD5Utils.md5Encryption(password, salt);
        System.out.println("salt="+salt);
        System.out.println("password="+s);
    }
    @Test
    public void test1(){
        System.out.println(new Date());
    }

    @Test
    public void testSaveUser(){
        for(int i=0;i<100;i++){
            User user = new User();
            String salt=UUID.randomUUID().toString();
            user.setPasswordSalt(salt);
            user.setPassword(MD5Utils.md5Encryption("zhangyukang",salt));
            user.setType(1);
            user.setUsername(UUID.randomUUID().toString().substring(0,9));
            user.setCreatetime("2020-2-13");
            user.setUpdatetime("2020-3-1");
            user.setIsdel(0);
            user.setIsban(0);
            userRepository.save(user);
        }

    }
}
