/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 10/4/15 5:25 PM
 */
package com.odoo.addons.timesheet.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;

public class ProjectProject extends OModel {
    public static final String TAG = ProjectProject.class.getSimpleName();

    OColumn use_timesheets = new OColumn("TimeSheets", OBoolean.class);
    OColumn analytic_account_id = new OColumn("AnalyticAccountId", AccountAnalyticAccount.class,
            OColumn.RelationType.ManyToOne);

    @Odoo.Functional(method = "storeAccountName", depends = {"analytic_account_id"}, store = true)
    OColumn account_name = new OColumn("Account Name", OVarchar.class).setLocalColumn();

    public ProjectProject(Context context, OUser user) {
        super(context, "project.project", user);
    }


    public String storeAccountName(OValues values) {
        if (!values.getString("analytic_account_id").equals("false")) {
            try {
                List<Object> analytic_account_id = (ArrayList<Object>) values.get("analytic_account_id");
                return analytic_account_id.get(1) + "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
