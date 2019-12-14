package com.cat.appmonitor.Hook;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class XHook {
    abstract void hook(final XC_LoadPackage.LoadPackageParam packageParam);
}