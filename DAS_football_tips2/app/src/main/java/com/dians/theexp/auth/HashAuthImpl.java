package com.dians.theexp.auth;

import com.lambdaworks.crypto.SCryptUtil;

/**
 * Created by k1ko on 12/2/15.
 */
public class HashAuthImpl implements HashAuth {

    private String originalPass;
    private String generatedSecuredPasswordHash;

    public HashAuthImpl(String originalPass) {
        this.originalPass = originalPass;
        this.generatedSecuredPasswordHash = SCryptUtil.scrypt(originalPass, 16, 16, 16);
    }

    public boolean checkPass(String currentPass) {
        return SCryptUtil.check(currentPass, generatedSecuredPasswordHash);
    }

    public String getGeneratedSecuredPasswordHash() {
        return generatedSecuredPasswordHash;
    }
}
