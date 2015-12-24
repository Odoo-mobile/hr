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
 * Created on 24/12/15 11:35 AM
 */
package com.odoo.addons.leave.utils;

import com.alamkanak.weekview.WeekViewEvent;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.utils.ODateUtils;

import java.util.Calendar;
import java.util.Date;

public class WeekViewWrapper {

    private ODataRow row;
    public WeekViewWrapper(ODataRow row){
        this.row = row;
    }
    public static WeekViewEvent get(ODataRow row){
        return new WeekViewWrapper(row).parse();
    }

    private WeekViewEvent parse(){
        if (!row.getString("date_from").equals("false") &&
                !row.getString("date_to").equals("false")) {
            Date date_from = ODateUtils.createDateObject(row.getString("date_from"), ODateUtils.DEFAULT_FORMAT,
                    false);
            Date date_to = ODateUtils.createDateObject(row.getString("date_to"), ODateUtils.DEFAULT_FORMAT,
                    false);

            Calendar calDateFrom = Calendar.getInstance();
            calDateFrom.setTime(date_from);

            Calendar calDateTo = Calendar.getInstance();
            calDateTo.setTime(date_to);
            return new WeekViewEvent(row.getInt(OColumn.ROW_ID), row.getString("name"),
                    calDateFrom, calDateTo);
        }
        return null;
    }

}
