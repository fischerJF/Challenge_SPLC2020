package com.puppycrawl.tools.checkstyle.packageobjectfactory.foo;

import api.AbstractCheck;

public class FooCheck extends AbstractCheck {
    @Override
    public int[] getDefaultTokens() {
        return new int[] {0};
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }
}
