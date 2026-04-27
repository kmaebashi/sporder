package com.kmaebashi.sporder.service;

import com.kmaebashi.nctfw.ImageFileResult;
import com.kmaebashi.nctfw.ServiceInvoker;

public class ImageService {
    ImageService() {}

    public static ImageFileResult getMenuThumbnail(ServiceInvoker invoker, String rtId, int menuItemId) {
        return invoker.invoke((context) -> {
           return null;
        });
    }
}
