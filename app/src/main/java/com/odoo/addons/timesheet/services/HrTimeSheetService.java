/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 *
 * Created on 23/3/15 3:46 PM
 */
package com.odoo.addons.timesheet.services;

import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.odoo.addons.timesheet.models.HrAnalyticTimeSheet;
import com.odoo.addons.timesheet.models.ProjectTask;
import com.odoo.core.service.ISyncFinishListener;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;
import com.odoo.core.utils.logger.OLog;

public class HrTimeSheetService extends OSyncService implements ISyncFinishListener {
    public static final String TAG = HrTimeSheetService.class.getSimpleName();
    private Context mContext = null;

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        mContext = context;
        OLog.log("111 TimeSheet Service Called");
        return new OSyncAdapter(context, HrAnalyticTimeSheet.class, this, true).onSyncFinish(this);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        // Nothing to Pass
        adapter.syncDataLimit(30);
    }

    @Override
    public OSyncAdapter performNextSync(OUser user, SyncResult syncResult) {
        OLog.log("222 Project Task Service Called");
        return new OSyncAdapter(mContext, ProjectTask.class, this, true);
    }
}
