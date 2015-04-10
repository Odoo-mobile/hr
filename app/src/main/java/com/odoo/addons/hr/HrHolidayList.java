package com.odoo.addons.hr; /**
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
 * Created on 16/3/15 5:41 PM
 */

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.hr.models.HrHolidays;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.logger.OLog;

import java.util.ArrayList;
import java.util.List;

public class HrHolidayList extends BaseFragment implements ISyncStatusObserverListener,
        LoaderManager.LoaderCallbacks<Cursor>, OCursorListAdapter.OnViewBindListener,
        SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = HrHolidays.class.getSimpleName();
    public static final String KEY_MENU = "key_menu_item";
    private boolean syncRequested = false;
    private OCursorListAdapter mCursorListAdapter = null;
    private ListView mList = null;
    private View mView = null;

    public enum Type {
        HOLIDAY
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.common_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        setHasSyncStatusObserver(TAG, this, db());
        initAdapter();
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> menu = new ArrayList<ODrawerItem>();
        menu.add(new ODrawerItem(TAG).setGroupTitle().setTitle("Leaves"));
        menu.add(new ODrawerItem(TAG).setTitle("Leave Request").setInstance(new HrHolidayList()).
                setExtra(data(Type.HOLIDAY)));
        return menu;
    }

    private Bundle data(Type type) {
        Bundle extra = new Bundle();
        extra.putString(KEY_MENU, type.toString());
        return extra;
    }

    @Override
    public Class<HrHolidays> database() {
        return HrHolidays.class;
    }

    private void initAdapter() {
        mList = (ListView) mView.findViewById(R.id.listview);
        mCursorListAdapter = new OCursorListAdapter(getActivity(), null,
                R.layout.hr_holidays_listitem);
        mCursorListAdapter.setOnViewBindListener(this);
        mList.setAdapter(mCursorListAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        OLog.log("DATA " +row);
        OControls.setText(view, R.id.holidayDisc, row.getString("name"));
        OControls.setText(view, R.id.holidayDateFrom, row.getString("date_from"));
        OControls.setText(view, R.id.holidayDateTo, row.getString("date_to"));
    }

    @Override
    public void onStatusChange(Boolean refreshing) {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), db().uri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursorListAdapter.changeCursor(cursor);
        OLog.log("Load Finished");
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                OLog.log("DATA" + cursor.getString(cursor.getColumnIndex("name")));
            }
        }
        if (cursor.getCount() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setVisible(mView, R.id.swipe_container);
                    OControls.setGone(mView, R.id.no_items);
                    setHasSwipeRefreshView(mView, R.id.swipe_container, HrHolidayList.this);
                }
            }, 500);
        } else {
            if (db().isEmptyTable() && !syncRequested) {
                syncRequested = true;
                onRefresh();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container);
                    OControls.setVisible(mView, R.id.no_items);
                    setHasSwipeRefreshView(mView, R.id.no_items, HrHolidayList.this);
//                OControls.setImage(mView, R.id.icon, R.drawable.ic_action_leads
//                );
                    OControls.setText(mView, R.id.title, "No Leaves Found");
                    OControls.setText(mView, R.id.subTitle, "");
                }
            }, 500);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(HrHolidays.AUTHORITY);
            setSwipeRefreshing(true);
        } else {
            hideRefreshingProgress();
            Toast.makeText(getActivity(), _s(R.string.toast_network_required), Toast.LENGTH_LONG)
                    .show();
        }
    }


}