package com.odoo.addons.leave;

import android.content.Context;
import android.database.Cursor;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.odoo.R;
import com.odoo.addons.leave.models.HrHolidays;
import com.odoo.addons.leave.utils.WeekViewWrapper;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.IOnItemClickListener;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OStringColorUtil;
import com.odoo.core.utils.logger.OLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HrLeaveRequest extends BaseFragment implements OCursorListAdapter.OnViewBindListener,
        LoaderManager.LoaderCallbacks<Cursor>, IOnItemClickListener, View.OnClickListener,
        CalendarView.OnDateChangeListener, MonthLoader.MonthChangeListener, WeekView.EventClickListener,
        WeekView.EventLongPressListener {
    public static final String TAG = HrLeaveRequest.class.getSimpleName();
    private View mView = null;
    private Bundle extra = null;
    private HrHolidays holidays = null;
    private GridView gridView = null;
    private OCursorListAdapter adapter = null;
    private WeekView mWeekView;

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hr_leave_request, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        extra = getArguments();
        holidays = new HrHolidays(getActivity(), user());
        setHasOptionsMenu(true);
        initAdapter();
    }

    private void initAdapter() {

        mWeekView = (WeekView) mView.findViewById(R.id.weekView);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);

        setupDateTimeInterpreter(false);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_calendar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id) {
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {


     /*   OControls.setText(view, R.id.statusType, row.getString("name"));
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
        view.findViewById(R.id.applyLeave);*/
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

    @Override
    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        List<ODataRow> rows = holidays.select();
        if (!rows.isEmpty()) {
            for (ODataRow row : rows) {
                WeekViewEvent event = WeekViewWrapper.get(row);
                if (event != null) {
                    event.setColor(OStringColorUtil.getStringColor(getContext(), row.getString("name")));
                    events.add(event);
                }
            }
        }
        return events;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(getActivity(), event.getName(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

        Toast.makeText(getActivity(), "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();

    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

}