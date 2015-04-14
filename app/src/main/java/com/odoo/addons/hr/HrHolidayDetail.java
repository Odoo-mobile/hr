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
 * Created on 10/4/15 6:26 PM
 */
package com.odoo.addons.hr;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.odoo.R;
import com.odoo.addons.hr.models.HrHolidays;
import com.odoo.addons.hr.models.HrHolidaysStatus;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.utils.OActionBarUtils;

import odoo.controls.OForm;

public class HrHolidayDetail extends ActionBarActivity {
    public static final String TAG = HrHolidayDetail.class.getSimpleName();
    private OForm mForm = null;
    private ODataRow record = null;
    private HrHolidays holidays = null;
    private Bundle bundle = null;
    private ActionBar actionBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hr_holidays_detail);
        OActionBarUtils.setActionBar(this, true);
        actionBar = getSupportActionBar();
        holidays = new HrHolidays(this, null);
        bundle = getIntent().getExtras();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_leave_request_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save_request:
                OValues values = mForm.getValues();
                HrHolidaysStatus status = new HrHolidaysStatus(this, null);
                values.put("leave_type", status.browse(values.getInt("holiday_status_id")).
                        getString("name"));
                if (record != null) {
                    holidays.update(record.getInt(OColumn.ROW_ID), values);
                } else {
                    holidays.insert(values);
                }
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        mForm = (OForm) findViewById(R.id.hrDetail);
        if (!bundle.containsKey(OColumn.ROW_ID)) {
            mForm.initForm(null);
            actionBar.setTitle(R.string.label_new);
        } else {
            initFormValues();
        }
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_close);
        mForm.setEditable(true);
    }

    private void initFormValues() {
        record = holidays.browse(bundle.getInt(OColumn.ROW_ID));
        mForm.initForm(record);
        actionBar.setTitle(R.string.label_leave_request);
    }

}
