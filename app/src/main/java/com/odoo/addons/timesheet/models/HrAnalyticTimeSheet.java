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
 * Created on 23/3/15 3:44 PM
 */
package com.odoo.addons.timesheet.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.base.addons.res.ResUsers;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class HrAnalyticTimeSheet extends OModel {
    public static final String TAG = HrAnalyticTimeSheet.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.hr.addons.timesheet.models.hr_timesheet";

    OColumn name = new OColumn("Description", OVarchar.class).setSize(64);
    OColumn unit_amount = new OColumn("Duration", OFloat.class);
    OColumn amount = new OColumn("Amount", OFloat.class);
    OColumn date = new OColumn("Date", ODateTime.class);

    OColumn user_id = new OColumn("User", ResUsers.class, OColumn.RelationType.ManyToOne);
    OColumn product_id = new OColumn("Product", ProductProduct.class, OColumn.RelationType.ManyToOne);
    OColumn account_id = new OColumn("Analytic Account", AccountAnalyticAccount.class,
            OColumn.RelationType.ManyToOne);
    OColumn general_account_id = new OColumn("Financial Account", AccountAccount.class,
            OColumn.RelationType.ManyToOne);
    OColumn journal_id = new OColumn("Analytic Journal", AccountAnalyticJournal.class,
            OColumn.RelationType.ManyToOne);
    OColumn sheet_id = new OColumn("Sheet", HrTimeSheetSheet.class, OColumn.RelationType.ManyToOne);

    public HrAnalyticTimeSheet(Context context, OUser user) {
        super(context, "hr.analytic.timesheet", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }
}
