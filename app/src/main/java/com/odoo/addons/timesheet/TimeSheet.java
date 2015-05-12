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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.timesheet.models.HrAnalyticTimeSheet;
import com.odoo.addons.timesheet.models.ProjectTask;
import com.odoo.addons.timesheet.models.ProjectTaskWork;
import com.odoo.addons.timesheet.utils.OChronometer;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.IOnItemClickListener;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OCursorUtils;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.logger.OLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import odoo.controls.fab.FloatingActionButton;

public class TimeSheet extends BaseFragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, OCursorListAdapter.OnViewBindListener, IOnItemClickListener, OCursorListAdapter.OnRowViewClickListener {
    public static final String TAG = TimeSheet.class.getSimpleName();
    private View mView;
    private Context mContext = null;
    private FloatingActionButton mFabButton;
    private TextView txvMotivation;
    private OCursorListAdapter mAdapter = null;
    private OChronometer mOchronometer;
    private ListView mListView;
    private boolean flag = false;
    private ImageView imgStartStop;

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
        mListView = (ListView) mView.findViewById(R.id.lst_task_listview);
        mFabButton = (FloatingActionButton) mView.findViewById(R.id.fab_button);
        txvMotivation = (TextView) mView.findViewById(R.id.txv_motivation);
        mFabButton.setOnClickListener(this);
        if (db().isEmptyTable()) {
            parent().sync().requestSync(HrAnalyticTimeSheet.AUTHORITY);
        }
        initListView();
        displayMotivationMesssage();
    }

    private void initListView() {
        setHasFloatingButton(mView, R.id.fab_button, mListView, this);
        mAdapter = new OCursorListAdapter(mContext, null, R.layout.timesheet_row_view);
        mAdapter.setOnViewBindListener(this);
        mAdapter.setOnRowViewClickListener(R.id.img_start_stop, this);
        mListView.setAdapter(mAdapter);
        mAdapter.handleItemClickListener(mListView, this);
        getLoaderManager().initLoader(0, null, this);
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
    public Class<ProjectTask> database() {
        return ProjectTask.class;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_timesheet, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                Toast.makeText(mContext, OResource.string(mContext, R.string.toast_invalid_choice), Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_button:
                Intent i = new Intent(getActivity(), TimeSheetDetail.class);
                startActivity(i);
                break;
        }

    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        OControls.setText(view, R.id.txv_project_name, row.getString("project_name"));
        OControls.setText(view, R.id.txv_task_name, row.getString("name"));
//        if (!row.getString("work_hour").equals("false")) {
//            OControls.setText(view, R.id.txv_spent_time, String.format("%.2f", Double.parseDouble(row.getString("work_hour"))));
//        } else
//            OControls.setText(view, R.id.txv_spent_time, "0.0");
    }

    @Override
    public void onItemDoubleClick(View view, int position) {

    }

    @Override
    public void onItemClick(View view, int position) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        Bundle bundle = new Bundle();
        if (!row.getString("work_hour").equals("false"))
            bundle.putString("hours", row.getString("work_hour"));
        else
            bundle.putString("hours", "0.0");
        bundle.putString("project_name", row.getString("project_name"));
        bundle.putString("task_name", row.getString("name"));
        bundle.putInt("task_id", row.getInt("_id"));
        IntentUtils.startActivity(mContext, TimeSheetDetail.class, bundle);
    }

    @Override
    public void onRowViewClick(int position, Cursor cursor, View view, View parent) {
        switch (view.getId()) {
            case R.id.img_start_stop:
                ODataRow row = OCursorUtils.toDatarow(cursor);
                mOchronometer = (OChronometer) parent.findViewById(R.id.chn_Ochronometer);
                imgStartStop = (ImageView) parent.findViewById(R.id.img_start_stop);
                if (flag) {
                    flag = false;
                    mOchronometer.stop();
                    imgStartStop.setImageResource(R.drawable.ic_action_play);
                    String[] result = mOchronometer.getOChronoText().split(":");
                    String time = "";
                    float hour = Float.parseFloat(result[0].toString());
                    float minutes = Float.parseFloat(result[1].toString());
                    minutes = minutes / 60;
                    time = hour + minutes + "";
                    ProjectTaskWork ptWork = new ProjectTaskWork(mContext, null);
                    OValues values = new OValues();
                    values.put("name", row.getString("name"));
                    values.put("hours", time);
                    values.put("date", ODateUtils.getDate());
                    values.put("user_id", db().getUser().getUserId());
                    values.put("task_id", row.getInt("_id"));
                    int id = ptWork.insert(values);
                    if (id > 0)
                        Toast.makeText(mContext, OResource.string(mContext, R.string.toast_record_inserted), Toast.LENGTH_LONG).show();
                } else {
                    flag = true;
                    mOchronometer.start();
                    imgStartStop.setImageResource(R.drawable.ic_action_stop);
                }
                break;
            default:
                OLog.log("Invalid Choice...!");
                break;
        }

    }
}