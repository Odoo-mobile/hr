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
 * Created on 24/3/15 12:30 PM
 */
package com.odoo.addons.timesheet;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.odoo.R;
import com.odoo.addons.timesheet.models.ProjectTask;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.utils.OActionBarUtils;

import java.util.ArrayList;
import java.util.List;

public class TimeSheetDetail extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {
    public static final String TAG = TimeSheetDetail.class.getSimpleName();
    private ActionBar actionBar;
    private SeekBar sHour, sMinutes;
    private EditText edtHour, edtMinutes;
    private Spinner spnTask;
    private ProjectTask mProjTask;
    private List<String> mSpinnerArray;
    private ArrayAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timesheet_detail);
        mProjTask = new ProjectTask(this, null);
        mSpinnerArray = new ArrayList<>();
        sHour = (SeekBar) findViewById(R.id.seek_hours);
        sMinutes = (SeekBar) findViewById(R.id.seek_minutes);
        edtHour = (EditText) findViewById(R.id.edt_hour);
        edtMinutes = (EditText) findViewById(R.id.edt_minutes);
        spnTask = (Spinner) findViewById(R.id.spnDetailTask);
        sHour.setOnSeekBarChangeListener(this);
        sMinutes.setOnSeekBarChangeListener(this);
        initActionBar();
        initTaskSpinner();
    }

    private void initActionBar() {
        OActionBarUtils.setActionBar(this, false);
        actionBar = getSupportActionBar();
        actionBar.setTitle("TimeSheetDetail");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initTaskSpinner() {
        for (ODataRow rows : mProjTask.select(new String[]{"name"})) {
            if (!rows.getString("name").equals("false"))
                mSpinnerArray.add(rows.getString("name"));
        }
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mSpinnerArray);
        spnTask.setAdapter(adapter);
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
                break;
            case R.id.menu_detail_discard:
                onBackPressed();
                break;
            default:
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
}
