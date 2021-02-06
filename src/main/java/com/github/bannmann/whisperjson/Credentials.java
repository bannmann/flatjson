package com.github.bannmann.whisperjson;

import java.util.Arrays;

import lombok.experimental.UtilityClass;

@UtilityClass
class Credentials
{
    public void wipe(char[] target)
    {
        if (target != null)
        {
            Arrays.fill(target, (char) 0);
        }
    }
}
