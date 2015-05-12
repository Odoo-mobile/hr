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
 * Created on 24/3/15 12:30 PM
 */
package com.odoo.addons.timesheet;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.timesheet.models.ProjectProject;
import com.odoo.addons.timesheet.models.ProjectTask;
import com.odoo.addons.timesheet.models.ProjectTaskWork;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.support.OUser;
import com.odoo.core.utils.OActionBarUtils;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.List;

public class TimeSheetDetail extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener,
        View.OnFocusChangeListener, AdapterView.OnItemSelectedListener {
    public static final String TAG = TimeSheetDetail.class.getSimpleName();
    private ActionBar actionBar;
    private SeekBar sHour, sMinutes;
    private TextView txvPrjName, txvTaskname;
    private EditText edtHour, edtMinutes, edtWorkSummary;
    private Spinner spnTask;
    private ProjectTask mProjTask;
    private List<String> mSpinnerArray;
    private ArrayAdapter mAdapter = null;
    private ProjectTaskWork mPTWork;
    private Context mContext = null;
    private int mTaskId = 0;
    private String mTaskName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timesheet_detail);
        mContext = this;
        mSpinnerArray = new ArrayList<>();
        initActionBar();
        initControls();
        initTaskSpinner();
        sHour.setOnSeekBarChangeListener(this);
        sMinutes.setOnSeekBarChangeListener(this);
        edtHour.setOnFocusChangeListener(this);
        edtMinutes.setOnFocusChangeListener(this);
        initProjectValues();
    }

    private void initProjectValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            spnTask.setVisibility(View.GONE);
            txvPrjName.setText(bundle.getString("project_name"));
            txvTaskname.setText(bundle.getString("task_name"));
            mTaskName = bundle.getString("task_name");
            mTaskId = bundle.getInt("task_id");
        } else {
            txvTaskname.setVisibility(View.GONE);
            mTaskName = spnTask.getSelectedItem().toString();
        }
    }

    private void initActionBar() {
        OActionBarUtils.setActionBar(this, false);
        actionBar = getSupportActionBar();
        actionBar.setTitle("TimeSheetDetail");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initControls() {
        mProjTask = new ProjectTask(this, null);
        mPTWork = new ProjectTaskWork(this, null);
        mSpinnerArray = new ArrayList<>();
        txvPrjName = (TextView) findViewById(R.id.txvDetailProjetName);
        txvTaskname = (TextView) findViewById(R.id.txvDetailTaskName);
        sHour = (SeekBar) findViewById(R.id.seek_hours);
        sMinutes = (SeekBar) findViewById(R.id.seek_minutes);
        edtHour = (EditText) findViewById(R.id.edt_hour);
        edtMinutes = (EditText) findViewById(R.id.edt_minutes);
        spnTask = (Spinner) findViewById(R.id.spnDetailTask);
        sHour = (SeekBar) findViewById(R.id.seek_hours);
        sMinutes = (SeekBar) findViewById(R.id.seek_minutes);
        edtWorkSummary = (EditText) findViewById(R.id.edt_work_summary);
        spnTask.setOnItemSelectedListener(this);
        sHour.setOnSeekBarChangeListener(this);
        sMinutes.setOnSeekBarChangeListener(this);
        edtHour.setOnFocusChangeListener(this);
        edtMinutes.setOnFocusChangeListener(this);
    }


    private void initTaskSpinner() {
        mSpinnerArray.add(OResource.string(this, R.string.label_spinner_select_task));
        for (ODataRow rows : mProjTask.select(new String[]{"name"})) {
            if (!rows.getString("name").equals("false"))
                mSpinnerArray.add(rows.getString("name"));
        }
        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mSpinnerArray);
        spnTask.setAdapter(mAdapter);
        txvPrjName.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timesheet_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_detail_save:
                float hour = Float.parseFloat(!edtHour.getText().toString().equals("") ? edtHour.getText().toString() : "0");
                float min = Float.parseFloat(!edtMinutes.getText().toString().equals("") ? edtMinutes.getText().toString() : "0");
                if (hour > 12 || min > 60)
                    Toast.makeText(this, OResource.string(this, R.string.toast_max_limit_crossed), Toast.LENGTH_LONG).show();
                else {
                    String time = "";
                    min = min / 60;
                    time = hour + min + "";
                    OValues values = new OValues();
                    values.put("name", mTaskName);
                    values.put("hours", time);
                    values.put("date", ODateUtils.getDate());
                    values.put("user_id", OUser.current(this).getUserId());
                    values.put("task_id", mTaskId);
                    int id = mPTWork.insert(values);
                    if (id > 0) {
                        Toast.makeText(this, OResource.string(this, R.string.toast_record_inserted), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }
                finish();
                break;
            case R.id.menu_detail_discard:
                onBackPressed();
                break;
            default:
                Toast.makeText(this, OResource.string(this, R.string.toast_invalid_choice), Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seek_hours:
                edtHour.setText(progress + "");
                break;
            case R.id.seek_minutes:
                edtMinutes.setText(progress + "");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        int hour = Integer.parseInt(!edtHour.getText().toString().equals("") ? edtHour.getText().toString() : "0");
        switch (view.getId()) {
            case R.id.edt_hour:
                if (hour <= 12)
                    sHour.setProgress(hour);
                else if (hour > 12) {
                    sHour.setProgress(0);
                    edtHour.setText("0");
                }
                break;
            case R.id.edt_minutes:
                int min = Integer.parseInt(!edtMinutes.getText().toString().equals("") ? edtMinutes.getText().toString() : "0");
                if (min == 60) {
                    sMinutes.setProgress(0);
                    edtMinutes.setText("0");
                    hour++;
                    edtHour.setText(hour + "");
                    sHour.setProgress(hour);
                    if (hour > 12) {
                        sHour.setProgress(0);
                        edtHour.setText("0");
                    }
                } else if (min > 0 && min < 60)
                    sMinutes.setProgress(min);
                else if (min >= 60) {
                    sMinutes.setProgress(0);
                    edtMinutes.setText("0");
                }
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ProjectProject project = new ProjectProject(mContext, null);
        if (position != 0) {
            ODataRow row = mProjTask.browse(new String[]{"id", "name", "project_id"}, "name = ?", new String[]{mSpinnerArray.get(position)});
            mTaskId = row.getInt("id");
            mTaskName = mSpinnerArray.get(position).toString();
            ODataRow prow = project.browse(row.getInt("project_id"));
            if (!prow.getString("account_name").equals("false")) {
                txvPrjName.setText(prow.getString("account_name"));
            }
        } else
            txvPrjName.setText("");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
