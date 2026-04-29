package com.kmaebashi.sporder.service;

import com.kmaebashi.nctfw.ImageFileResult;
import com.kmaebashi.nctfw.NotFoundException;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.sporder.dbaccess.ImageDbAccess;
import com.kmaebashi.sporder.dto.MenuItemDto;

import java.nio.file.Path;

public class ImageService {
    ImageService() {}

    public static ImageFileResult getMenuThumbnail(ServiceInvoker invoker, Path imageRoot, String rtId, int menuItemId) {
        return invoker.invoke((context) -> {
            MenuItemDto dto = ImageDbAccess.getMenuItem(context.getDbAccessInvoker(), rtId, menuItemId);
            if (dto == null) {
                throw new NotFoundException("画像が見つかりません。");
            }

            String fileName = dto.photoS;
            if (fileName == null) {
                fileName = "SDUMMY.jpg";
            }
            Path imagePath = imageRoot.resolve(rtId).resolve("thumbnails").resolve(fileName);

            return new ImageFileResult(imagePath);
        });
    }

    public static ImageFileResult getMenuFullsize(ServiceInvoker invoker, Path imageRoot, String rtId, int menuItemId) {
        return invoker.invoke((context) -> {
            MenuItemDto dto = ImageDbAccess.getMenuItem(context.getDbAccessInvoker(), rtId, menuItemId);
            if (dto == null) {
                throw new NotFoundException("画像が見つかりません。");
            }

            String fileName = dto.photoL;
            if (fileName == null) {
                fileName = "LDUMMY.jpg";
            }
            Path imagePath = imageRoot.resolve(rtId).resolve("fullsize").resolve(fileName);

            return new ImageFileResult(imagePath);
        });
    }
}
