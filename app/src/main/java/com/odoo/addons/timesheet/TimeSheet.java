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
 * Created on 23/3/15 3:48 PM
 */
package com.odoo.addons.timesheet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.timesheet.models.HrAnalyticTimeSheet;
import com.odoo.addons.timesheet.utils.OChronometer;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TimeSheet extends BaseFragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = TimeSheet.class.getSimpleName();
    private View mView;
    private Context mContext = null;
    private Button btnStartStop;
    private TextView txvMotivation;
    private OCursorListAdapter mAdapter = null;
    private OChronometer mOchronometer;
    public static final String TASK_KEY = "task_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.timesheet_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        mView = view;
        mContext = getActivity();
        mOchronometer = (OChronometer) mView.findViewById(R.id.chn_Ochronometer);
        btnStartStop = (Button) mView.findViewById(R.id.btn_start_stop);
        txvMotivation = (TextView) mView.findViewById(R.id.txv_motivation);
        btnStartStop.setOnClickListener(this);
        if (db().isEmptyTable()) {
            parent().sync().requestSync(HrAnalyticTimeSheet.AUTHORITY);
        }
        displayMotivationMesssage();
    }

    private void displayMotivationMesssage() {
        String[] array = mContext.getResources().getStringArray(R.array.motivation_messages);
        String randomStr = array[new Random().nextInt(array.length)];
        txvMotivation.setText(randomStr);
        txvMotivation.postDelayed(new Runnable() {
            @Override
            public void run() {
                txvMotivation.setVisibility(View.GONE);
            }
        }, 15000);
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> menu = new ArrayList<>();
        menu.add(new ODrawerItem(TAG).setTitle(OResource.string(context, R.string.drawer_timesheet_title)).
                setGroupTitle());
        menu.add(new ODrawerItem(TAG).setTitle(OResource.string(context, R.string.drawer_timesheet_activities)).
                setIcon(R.drawable.ic_action_timer)
                .setInstance(new TimeSheet()));
        return menu;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), db().uri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public Class<HrAnalyticTimeSheet> database() {
        return HrAnalyticTimeSheet.class;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_timesheet, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_create_new:
                Intent i = new Intent(getActivity(), TimeSheetDetail.class);
                startActivity(i);
                break;
            default:
                Toast.makeText(mContext, OResource.string(mContext, R.string.toast_invalid_choice), Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
//        String[] result = mChronometer.getText().toString().split(":");
//        String time = "";
//        int hour = Integer.parseInt(result[0].toString());
//        time = (hour > 0) ? hour + "." + result[1] : "0." + result[1];
//        mChronometer.setBase(SystemClock.elapsedRealtime());
        if (btnStartStop.getText().equals("Start")) {
            btnStartStop.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_stop), null);
            mOchronometer.start();
            btnStartStop.setText("Stop");
        } else {
            btnStartStop.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_play), null);
            mOchronometer.getBase();
            mOchronometer.stop();
//            ProjectTaskWork ptWork = new ProjectTaskWork(mContext, null);
//            OValues values = new OValues();
//            values.put("name", mTaskName);
//            values.put("hours", time);
//            values.put("date", ODateUtils.getDate());
//            values.put("user_id", db().getUser().getUser_id());
//            values.put("task_id", mTaskId + "");
//            int id = ptWork.insert(values);
//            if (id > 0)
//                Toast.makeText(mContext, OResource.string(mContext, R.string.toast_record_inserted), Toast.LENGTH_LONG).show();
            btnStartStop.setText("Start");
        }
    }

}