/**
 * Copyright 2016 Zhougaofeng
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tm.demo;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;


public class MagicSDK extends Application{

    private static Application  self;

    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
        FileDownloader.setupOnApplicationOnCreate(this);
    }

    public static Application getContext() {
        return self;
    }
}
