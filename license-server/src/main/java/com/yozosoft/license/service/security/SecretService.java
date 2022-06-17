package com.yozosoft.license.service.security;

import com.yozosoft.license.util.RSAUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 密码管理service
 *
 * @author zhouf
 */
@Service("secretService")
public class SecretService {

    private Map<Long, String> secrets = new ConcurrentHashMap<>();

    private String publicRSAKey;

    private String privateRSAKey;

    @PostConstruct
    public void init() {
        publicRSAKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCN+B4QpQU1QjixzRuKAxKkBi1wZWtoXuzyRKS4Glv8yO4P66mUW9NdX9rQrmLtWwcx4iO18VQMsXS+loVmBODhdaUWzR6uF6mNrAEjFuPhrlVNJ76/8OKin882Mf86SWm6RiR1QhORwCjwhZYehdF9dKmBOqCSKNnsaOStJe9rrQIDAQAB";
        privateRSAKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI34HhClBTVCOLHNG4oDEqQGLXBla2he7PJEpLgaW/zI7g/rqZRb011f2tCuYu1bBzHiI7XxVAyxdL6WhWYE4OF1pRbNHq4XqY2sASMW4+GuVU0nvr/w4qKfzzYx/zpJabpGJHVCE5HAKPCFlh6F0X10qYE6oJIo2exo5K0l72utAgMBAAECgYAyh6M7zWBjKmS/c/9fSeSymLhHjvGBvnebay8tj8Q53tJMvFYpRd2fGGTrbYYscP/Ik3KeaXy+39Jrm2tzej0Hun9qDgb6hUfXwPgzczkClrznQcDxQwPVd59x/Z8XzSx0rEFSrDApUkuDBZ11vLDl5ZZ5zbwfd39gi89Vz0zyAQJBAMAJYVjaBRseFU6nqxigk3CslCTUf8DiNC0ZG9fzqA7I3bqsWXN+SgUmxdVD9YEnoDgvolhYIqzKsu6/J5VchUECQQC9QZO5ORYpZ/dQKH6ExBY/8oovcuu0D7QDSMwiD7neCp367hmu5oWJN581J9OFsv0lVkC7zks6T4ltVEdlY+9tAkAyzoiF1HDDYusqOywGQP2hMtejAuGl63L4d4spUFqnRrd7GvwzlTcKM97ldKxwdkZqdxfRncfWxW4fwk07tBXBAkBoPu3i0boOVrEYWyXNdcEXuj45gqCoTxoF0Rx4CpeD4e9BH9PPzjRx78xnWCef9oaQAWGo0SoI1XTKXujwxUpFAkEArzxaT+1Dz/Prh4MYjZXnur60Apzy9S913V8k2I1L7gb6K7CyQgOqzc00c8MDFwzUlaksp7oWGLfB4yM9JNNU9w==";
    }

    public String getPublicRSAKey() {
        return publicRSAKey;
    }

    public String decryptSecret(String channelSecret) throws Exception {
        String decrypt = RSAUtils.decrypt(channelSecret, privateRSAKey);
        return decrypt;
    }

    public void saveSecret(Long uuid, String secret) {
        secrets.put(uuid, secret);
    }

    public String getSecretByUuid(Long uuid) {
        String secret = secrets.get(uuid);
        return secret;
    }
}
