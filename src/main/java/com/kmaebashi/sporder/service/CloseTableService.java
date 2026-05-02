package com.kmaebashi.sporder.service;

import com.kmaebashi.jsonparser.ClassMapper;
import com.kmaebashi.nctfw.BadRequestException;
import com.kmaebashi.nctfw.InvokerOption;
import com.kmaebashi.nctfw.JsonResult;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.sporder.controller.data.ApiResult;
import com.kmaebashi.sporder.dbaccess.CloseTableDbAccess;
import com.kmaebashi.sporder.dto.CloseTableDto;

import java.time.LocalDateTime;

public class CloseTableService {
    private CloseTableService() {}

    public static JsonResult close(ServiceInvoker invoker, String tableCode) {
        return invoker.invoke((context) -> {
            CloseTableDto tableDto = CloseTableDbAccess.getTableForUpdate(context.getDbAccessInvoker(), tableCode);
            if (tableDto == null || tableDto.currentOrderGroup == null) {
                throw new BadRequestException("テーブルが利用中ではありません。");
            }

            LocalDateTime now = LocalDateTime.now();
            CloseTableDbAccess.updateOrderGroupClosedAt(context.getDbAccessInvoker(), tableDto.rtId,
                    tableDto.currentOrderGroup, now);
            CloseTableDbAccess.clearTableCurrentOrderGroup(context.getDbAccessInvoker(), tableDto.rtId,
                    tableDto.tableId, now);

            ApiResult result = new ApiResult("成功しました。");
            return new JsonResult(ClassMapper.toJson(result));
        }, InvokerOption.TRANSACTIONAL);
    }
}
