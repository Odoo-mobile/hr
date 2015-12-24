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
 * Created on 8/12/15 4:20 PM
 */
package com.odoo.addons.leave;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.odoo.R;
import com.odoo.addons.leave.models.HrHolidays;
import com.odoo.addons.leave.models.HrHolidaysStatus;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.IOnItemClickListener;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.logger.OLog;

import java.util.ArrayList;
import java.util.List;

public class HrLeaveSummary extends BaseFragment implements OCursorListAdapter.OnViewBindListener,
        LoaderManager.LoaderCallbacks<Cursor>, IOnItemClickListener, View.OnClickListener {
    public static final String TAG = HrLeaveSummary.class.getSimpleName();
    private View mView = null;
    private Bundle extra = null;
    private HrHolidays holidays = null;
    private GridView gridView = null;
    private OCursorListAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.common_gridview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        extra = getArguments();
        holidays = new HrHolidays(getActivity(), user());
        initAdapter();
    }

    private void initAdapter() {
        gridView = (GridView) mView.findViewById(R.id.gridView);
        adapter = new OCursorListAdapter(getActivity(), null,
                R.layout.hr_leave_summary);
        adapter.setOnViewBindListener(this);
        gridView.setAdapter(adapter);
        adapter.handleItemClickListener(gridView, this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> menu = new ArrayList<>();
        menu.add(new ODrawerItem(TAG).setTitle("Leave Summary").setInstance(new HrLeaveSummary()));
        return menu;
    }

    @Override
    public Class<HrHolidays> database() {
        return HrHolidays.class;
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        OControls.setText(view, R.id.statusType, row.getString("name"));
        HrHolidaysStatus status = new HrHolidaysStatus(getActivity(), user());
        ODataRow data = status.select(null, OColumn.ROW_ID + " = ?",
                new String[]{row.getString("holiday_status_id")}).get(0);
        OControls.setText(view, R.id.takenLeave, data.getString("taken_leave"));
        OControls.setText(view, R.id.allocated_leave, data.getString("allocated_leave"));
        view.findViewById(R.id.applyLeave).setOnClickListener(this);
        view.findViewById(R.id.applyLeave).setTag(row);
        String s = status.getColorCode(data.getString("color_name")).
                get("dark").toString();
        ((LinearLayout) view).findViewById(R.id.summryDetail);
        view.findViewById(R.id.applyLeave);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String where = "(holiday_type = ? and state != ?)) group by((holiday_status_id)";
        String[] whereArgs = new String[]{"employee", "refuse"};
        return new CursorLoader(getActivity(), db().uri(), null, where, whereArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.changeCursor(cursor);
        if (cursor.moveToFirst()) {
            OLog.log(cursor.getCount() + "  number of item");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

        
    }

    @Override
    public void onItemDoubleClick(View view, int position) {


    }

    @Override
    public void onItemClick(View view, int position) {
    }


    @Override
    public void onClick(View v) {
        ODataRow row = (ODataRow) v.getTag();
        Bundle bundle = new Bundle();
        bundle.putInt("holiday_status_id", row.getInt("holiday_status_id"));
        IntentUtils.startActivity(getActivity(), HrHolidayDetail.class, bundle);
    }
}