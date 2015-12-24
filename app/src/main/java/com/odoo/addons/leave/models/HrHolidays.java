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
 * Created on 8/12/15 3:03 PM
 */
package com.odoo.addons.leave.models;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OText;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;
import com.odoo.core.utils.logger.OLog;

import org.json.JSONArray;

public class HrHolidays extends OModel {
    public static final String TAG = HrHolidays.class.getSimpleName();

    public static final String AUTHORITY = "com.odoo.hr.sync.hr_holiday";

    OColumn name = new OColumn("Description", OVarchar.class);
    OColumn date_from = new OColumn("Start Date", ODateTime.class);
    OColumn date_to = new OColumn("End Date", ODateTime.class);
    OColumn holiday_status_id = new OColumn("Leave Type", HrHolidaysStatus.class,
            OColumn.RelationType.ManyToOne);
    OColumn manager_id = new OColumn("First Approval", HrEmployee.class,
            OColumn.RelationType.ManyToOne);
    OColumn employee_id = new OColumn("Employee", HrEmployee.class, OColumn.RelationType.ManyToOne);
    OColumn user_id = new OColumn("User", HrEmployee.class, OColumn.RelationType.ManyToOne);
    OColumn category_id = new OColumn("Employee Tag", HrEmployee.class,
            OColumn.RelationType.ManyToOne);
    OColumn department_id = new OColumn("Department", HrDepartment.class,
            OColumn.RelationType.ManyToOne);
    OColumn holiday_type = new OColumn("Mode", OSelection.class).
            addSelection("employee", "By Employee").addSelection("category", "By Employee Tag");
    OColumn number_of_days = new OColumn("Duration", OInteger.class);
    @Odoo.Functional(depends = {"holiday_status_id"}, store = true, method = "getLeaveType")
    OColumn leave_type = new OColumn("Type", OVarchar.class).setLocalColumn();
    OColumn notes = new OColumn("Notes", OText.class);
    OColumn type = new OColumn("Type", OVarchar.class);
    OColumn state = new OColumn("State", OVarchar.class);

    public HrHolidays(Context context, OUser user) {
        super(context, "hr.holidays", user);
    }


    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    public String getLeaveType(OValues values) {
        OLog.log("Ovalues " + values.getString("holiday_status_id"));
        try {
            if (!values.getString("holiday_status_id").equals("false")) {
                JSONArray status_id = new JSONArray(values.getString("holiday_status_id"));
                return status_id.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public int setStateView(String state) {
        int color = 0;
        if (state.equals("validate")) {
            color = Color.GREEN;
        } else if (state.equals("cancel")) {
            color = Color.RED;
        } else if (state.equals("draft")) {
            color = Color.GRAY;
        } else if (state.equals("confirm")) {
            color = Color.BLUE;
        }
        return color;
    }
}
