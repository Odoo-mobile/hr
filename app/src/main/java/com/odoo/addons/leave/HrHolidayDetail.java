package com.odoo.addons.leave;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.addons.leave.models.HrHolidays;
import com.odoo.addons.leave.models.HrHolidaysStatus;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.utils.OActionBarUtils;

import odoo.controls.OForm;

public class HrHolidayDetail extends AppCompatActivity {

    public static final String TAG = HrHolidayDetail.class.getSimpleName();
    private OForm mForm = null;
    private ODataRow record = null;
    private HrHolidays holidays = null;
    private Bundle bundle = null;
    private ActionBar actionBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hr_holiday_detail);
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
        actionBar.setTitle(R.string.label_new);
        if (!bundle.containsKey(OColumn.ROW_ID)) {
            mForm.initForm(null);
            actionBar.setTitle(R.string.label_new);
            if (bundle.containsKey("holiday_status_id")) {
                HrHolidaysStatus status = new HrHolidaysStatus(this, null);
                ODataRow row = status.browse(bundle.getInt("holiday_status_id"));
                TextView viewTitle = (TextView) findViewById(R.id.statusTitle);
                viewTitle.setText(row.getString("name"));
                viewTitle.setVisibility(View.VISIBLE);
                mForm.findViewById(R.id.statusId).setVisibility(View.GONE);
                if(!row.getString("color_name").toUpperCase().equals("false"))
                viewTitle.setBackgroundColor(Color.parseColor(row.getString("color_name").
                        toUpperCase()));
            }
        } else {
            initFormValues();
            mForm.findViewById(R.id.viewState).setBackgroundColor(holidays.setStateView(record.
                    getString("state")));

        }
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_close);
        mForm.setEditable(true);
        mForm.findViewById(R.id.note).setVisibility(View.GONE);
        if (bundle.containsKey(HrHolidayList.KEY_MENU) && bundle.getString(HrHolidayList.KEY_MENU).equals(HrHolidayList.Type.ALLOCATION_REQUEST.
                toString())) {
            mForm.findViewById(R.id.dateSelection).setVisibility(View.GONE);
            mForm.findViewById(R.id.note).setVisibility(View.VISIBLE);
        }
    }

    private void initFormValues() {
        record = holidays.browse(bundle.getInt(OColumn.ROW_ID));
        mForm.initForm(record);
        actionBar.setTitle(R.string.label_leave_request);
    }
}
