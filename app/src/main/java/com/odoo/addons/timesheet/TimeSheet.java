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
 * Created on 23/3/15 3:48 PM
 */
package com.odoo.addons.timesheet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.timesheet.models.ProjectProject;
import com.odoo.addons.timesheet.models.ProjectTask;
import com.odoo.addons.timesheet.models.ProjectTaskWork;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.IOnItemClickListener;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.List;

public class TimeSheet extends BaseFragment implements

        IOnItemClickListener, AdapterView.OnItemSelectedListener, View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, OCursorListAdapter.OnViewBindListener {
    public static final String TAG = TimeSheet.class.getSimpleName();
    private View mView;
    private Cursor mCursor;
    private ProjectProject mProject;
    private Spinner spn_task;
    private List<String> project_list = null;
    private ArrayAdapter adapter = null;
    private List<String> spinnerArray = null;
    private Chronometer mChronometer;
    private Context mContext = null;
    private Button btnStartStop;
    private TextView txvProjecName;
    private ListView lstTaskList;
    private OCursorListAdapter mAdapter = null;

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
        mProject = new ProjectProject(mContext, null);
        mChronometer = (Chronometer) mView.findViewById(R.id.chn_chronometer);
        btnStartStop = (Button) mView.findViewById(R.id.btn_start_stop);
        txvProjecName = (TextView) mView.findViewById(R.id.txv_project_name);
        lstTaskList = (ListView) mView.findViewById(R.id.lst_task_list);
        btnStartStop.setOnClickListener(this);
        spn_task = (Spinner) mView.findViewById(R.id.spn_tasks);
        spn_task.setOnItemSelectedListener(this);
        mProject = new ProjectProject(getActivity(), null);
        if (db().isEmptyTable()) {
            parent().sync().requestSync(ProjectTask.AUTHORITY);
        }
        initSpinners();
        initAdapter();
    }

    public void initSpinners() {
        spinnerArray = new ArrayList();
        for (ODataRow rows : db().select(new String[]{"name"})) {
            if (!rows.getString("name").equals("false"))
                spinnerArray.add(rows.getString("name"));
        }
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerArray);
        spn_task.setAdapter(adapter);
    }

    private void initAdapter() {
        mAdapter = new OCursorListAdapter(getActivity(), null,
                R.layout.timesheet_custom_listview_row);
        mAdapter.setOnViewBindListener(this);
        lstTaskList.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        mCursor = cursor;
        TextView txvRowProjectName, txvRowTaskName, txvRowTime;
        txvRowProjectName = (TextView) view.findViewById(R.id.txvRowProjectName);
        txvRowTaskName = (TextView) view.findViewById(R.id.txvRowTaskName);
        txvRowTime = (TextView) view.findViewById(R.id.txvRowTime);
        txvRowProjectName.setText(row.getM2ORecord("project_id").getName());
        txvRowTaskName.setText(row.getString("name"));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), db().uri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> menu = new ArrayList<>();
        menu.add(new ODrawerItem(TAG).setTitle(OResource.string(context, R.string.drawer_activities)).
                setIcon(R.drawable.ic_action_timer)
                .setInstance(new TimeSheet()));
        return menu;
    }

    @Override
    public Class<ProjectTask> database() {
        return ProjectTask.class;
    }

    @Override
    public void onItemDoubleClick(View view, int position) {

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

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
//        Intent i = new Intent(getActivity(), TimeSheetDetail.class);
//        i.putExtra("name", mCursor.getString(mCursor.getColumnIndex("name")));
//        startActivity(i);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ODataRow row = db().browse(new String[]{"project_id"}, "name = ?", new String[]{spinnerArray.get(position)});
        ODataRow project = mProject.browse(row.getInt("project_id"));
        if (!project.getString("account_name").equals("false"))
            txvProjecName.setText(project.getString("account_name"));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        if (btnStartStop.getText().equals("Start")) {
            mChronometer.start();
            btnStartStop.setText("Stop");
        } else {
            ProjectTaskWork ptWork = new ProjectTaskWork(mContext, null);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            OValues values = new OValues();
            values.put("name", "sample");
            values.put("hour", mChronometer.getText().toString());
            values.put("date", ODateUtils.getDate());
            values.put("user_id", db().getUser().getUser_id());
            int id = ptWork.insert(values);
            if (id > 0)
                Toast.makeText(mContext, OResource.string(mContext, R.string.toast_record_inserted), Toast.LENGTH_LONG).show();
            btnStartStop.setText("Start");
        }
    }
}