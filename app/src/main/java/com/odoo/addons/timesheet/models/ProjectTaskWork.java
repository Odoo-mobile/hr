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
 * Created on 14/4/15 11:07 AM
 */
package com.odoo.addons.timesheet.models;

import android.content.Context;

import com.odoo.base.addons.res.ResUsers;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class ProjectTaskWork extends OModel {
    public static final String TAG = ProjectTaskWork.class.getSimpleName();

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn hours = new OColumn("Spent Time", OFloat.class);
    OColumn date = new OColumn("Date", ODateTime.class);
    OColumn user_id = new OColumn("Done By", ResUsers.class, OColumn.RelationType.ManyToOne);
    OColumn task_id = new OColumn("Task Id", ProjectTask.class, OColumn.RelationType.ManyToOne);

    public ProjectTaskWork(Context context, OUser user) {
        super(context, "project.task.work", user);
    }
}
