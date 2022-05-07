package com.cmk.app.config;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * @Author: romens
 * @Date: 2019-11-1 9:17
 * @Desc:
 */
@GlideModule
public class GlobalGlideConfig extends AppGlideModule {

    @Override
    public boolean isManifestParsingEnabled() {
//    return super.isManifestParsingEnabled();
        return false;
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
    }
}
