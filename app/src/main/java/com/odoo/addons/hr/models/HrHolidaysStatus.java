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
 * Created on 16/3/15 4:59 PM
 */

import android.content.Context;

import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HrHolidaysStatus extends OModel {
    public static final String TAG = HrHolidaysStatus.class.getSimpleName();

    OColumn name = new OColumn("Leave Type", OVarchar.class);
    OColumn color_name = new OColumn("Color in Report", OVarchar.class).setRequired().setDefaultValue("red");
    //    OColumn temp_limit = new OColumn("Allow to Override Limit", OBoolean.class);
    OColumn double_validation = new OColumn("Apply Double Validation", OBoolean.class);
    OColumn allocated_leave = new OColumn("Total Allocated", OInteger.class).setLocalColumn().setDefaultValue(0);
    OColumn taken_leave = new OColumn("Total Taken Leaves", OInteger.class).setLocalColumn().setDefaultValue(0);
    OSyncAdapter adapter = null;
    private Context mContext = null;
    private HashMap<String, String> colourCodeSet = new HashMap<>();
    private String[] server_color = new String[]{"black", "red", "lavender", "brown"};
    private String[] colourCodeDark = new String[]{"#212121", "#F44336", "#CE93D8", "8D6E63"};
    private String[] colourCodeLight = new String[]{"#424242", "#EF5340", "#E1BEE7", "#A1887F"};
    private String[] textColor = new String[]{"#FAFAFA", "#FAFAFA", "#212121", "#FAFAFA"};

    public HrHolidaysStatus(Context context, OUser user) {
        super(context, "hr.holidays.status", user);
        mContext = context;
//        temp_limit.setName("limit");
    }


    @Override
    public boolean checkForCreateDate() {
        return false;
    }

    @Override
    public void onSyncFinished() {

        /*

        1. loop : status : [1,2,3,4,5]

        get holidays as per status : [4,8,9] based on status
        2. loop based on status records
        int total_taken = 0;
        int total_given = 0;
        for(....){
            ...
        }
        update(status with total_taken and total_given)


         */


        HrHolidays holiday = new HrHolidays(mContext, null);
        int total_allocated = 0;
        int total_taken = 0;
        List<ODataRow> holidayStatusIds = select(new String[]{OColumn.ROW_ID});
        for (ODataRow statusId : holidayStatusIds) {
            List<ODataRow> holidayRowList = holiday.select(null, OColumn.ROW_ID +
                            " = ? and holiday_type = ? and " + "state != ? ",
                    new String[]{statusId.getString(OColumn.ROW_ID), "employee", "refuse"});
            OValues values = new OValues();
            for (ODataRow holidayRow : holidayRowList) {
                int number_of_days = holidayRow.getInt("number_of_days");
                if (number_of_days > 0) {
                    total_allocated += number_of_days;
                    values.put("allocated_leave", total_allocated);
                } else if (number_of_days < 0) {
                    total_taken += Math.abs(number_of_days);
                    values.put("taken_leave", total_taken);
                }
            }
            update(statusId.getInt("holiday_status_id"), values);
        }
    }

    public HashMap<String, String> getColorCode(String color) {
        List<String> serverColrList = new ArrayList<>();
        serverColrList.addAll(Arrays.asList(server_color));
        int index = serverColrList.indexOf(color);
        index = (index == -1) ? 0 : index;
        HashMap<String, String> colors = new HashMap<>();
        colors.put("dark", colourCodeDark[index]);
        colors.put("text", textColor[index]);
        return colors;
    }

}