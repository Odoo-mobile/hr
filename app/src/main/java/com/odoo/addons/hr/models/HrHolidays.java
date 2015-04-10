package com.odoo.addons.hr.models; /**
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
 * Created on 16/3/15 3:40 PM
 */

import android.content.Context;
import android.net.Uri;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class HrHolidays extends OModel {
    public static final String TAG = HrHolidays.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.hr.core.provider.content.sync.hr_holidays";

    OColumn name = new OColumn("Description", OVarchar.class);
    OColumn date_from = new OColumn("Start Date", ODateTime.class);
    OColumn date_to = new OColumn("End Date", ODateTime.class);
    OColumn holiday_status_id = new OColumn("Leave Type", HrHolidaysStatus.class,
            OColumn.RelationType.ManyToOne);
    OColumn manager_id = new OColumn("First Approval", HrEmployee.class,
            OColumn.RelationType.ManyToOne);
    OColumn payslip_status = new OColumn("Payslip Status", OBoolean.class);
    OColumn employee_id = new OColumn("Employee", HrEmployee.class, OColumn.RelationType.ManyToOne);
    OColumn user_id = new OColumn("User", HrEmployee.class, OColumn.RelationType.ManyToOne);
    OColumn category_id = new OColumn("Employee Tag", HrEmployee.class,
            OColumn.RelationType.ManyToOne);
    OColumn department_id = new OColumn("Department", HrDepartment.class,
            OColumn.RelationType.ManyToOne);
    OColumn holiday_type = new OColumn("Mode", OSelection.class).
            addSelection("employee", "By Employee").addSelection("category", "By Employee Tag");
    OColumn number_of_days = new OColumn("Duration", OInteger.class);


    public HrHolidays(Context context, OUser user) {
        super(context, "hr.holidays", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }
}